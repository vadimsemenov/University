import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Artificial {
    public static final int POPULATION_SIZE = 1000;
    public static final int POPULATION_MUTATION_PERIOD = 500;
    public static final int ESTIMATED_INCREASE_BY_PERIOD = 20;
    public static final int BEST_THRESHOLD = POPULATION_SIZE / 20;

    public static void main(String[] args) {
        final String inputFile = "artificial.in";
        final int tests = 10;
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
            List<Future<?>> futures = new ArrayList<>(tests);
            for (int testCase = 1; testCase <= tests; ++testCase) {
                StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
                int fieldSize = Integer.parseInt(tokenizer.nextToken());
                int automatonSize = Integer.parseInt(tokenizer.nextToken());
                int maxSteps = Integer.parseInt(tokenizer.nextToken());
                Worker worker = new Worker(testCase, automatonSize, maxSteps, Field.createField(fieldSize, reader));
                futures.add(pool.submit(worker));
            }
            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static class Worker implements Runnable {
        final int id;
        final int automatonSize;
        final int maxSteps;
        final Field field;
        final Random random;

        Worker(int id, int automatonSize, int maxSteps, Field field) {
            this.id = id;
            this.automatonSize = automatonSize;
            this.field = field;
            this.maxSteps = maxSteps;
            this.random = ThreadLocalRandom.current();
        }

        @Override
        public void run() {
            int previous = 0;
            Automaton[] population = new Automaton[POPULATION_SIZE];
            Automaton[] nextPopulation = new Automaton[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; ++i) {
                population[i] = Automaton.createRandomAutomaton(automatonSize, random);
            }
            int[] aux = new int[POPULATION_SIZE];
            Integer[] order = new Integer[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; ++i) {
                order[i] = i;
            }
            int[] position = new int[POPULATION_SIZE];
            for (int iteration = 0; ; ++iteration) {
                for (int i = 0; i < POPULATION_SIZE; ++i) {
                    aux[i] = field.estimate(population[i], maxSteps);
                    if (aux[i] < 0) {
                        try (PrintWriter out = new PrintWriter("artificial-" + id + ".out")) {
                            population[i].print(out);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        System.err.println("done #" + id + " @ " + (iteration + 1));
                        return;
                    }
                }
                Arrays.sort(order, (first, second) -> Integer.compare(aux[first], aux[second]));
                for (int i = 0; i < POPULATION_SIZE; ++i) {
                    position[order[i]] = i;
                }
                if ((iteration + 1) % POPULATION_MUTATION_PERIOD == 0 && aux[order[0]] - previous < ESTIMATED_INCREASE_BY_PERIOD) {
                    if (random.nextBoolean()) {
                        System.err.println("[" + id + "] small mutation @" + (iteration + 1));
                        smallMutatiton(population, position);
                    } else {
                        System.err.println("[" + id + "] big mutation @" + (iteration + 1));
                        bigMutation(population);
                    }
                } else {
                    if ((iteration + 1) % POPULATION_MUTATION_PERIOD == 0) {
                        previous = aux[order[0]];
                    }
                    Automaton crossoverPart = null;
                    for (int i = 0; i < POPULATION_SIZE; ++i) {
                        if (position[i] > BEST_THRESHOLD) {
                            if (random.nextBoolean()) {
                                nextPopulation[i] = population[i].clone();
                                nextPopulation[i].mutate(random);
                            } else {
                                if (crossoverPart != null) {
                                    nextPopulation[i] = crossoverPart;
                                    crossoverPart = null;
                                } else {
                                    nextPopulation[i] = population[random.nextInt(POPULATION_SIZE)];
                                    crossoverPart = population[random.nextInt(POPULATION_SIZE)];
                                    Automaton.crossover(nextPopulation[i], crossoverPart, random);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void smallMutatiton(Automaton[] population, int[] position) {
            final int divisor = 10;
            for (int i = 0; i < POPULATION_SIZE; ++i) {
                if (position[i] > POPULATION_SIZE / divisor) {
                    population[i].mutate(random);
                }
            }
        }

        private void bigMutation(Automaton[] population) {
            for (int i = 0; i < POPULATION_SIZE; ++i) {
                if (random.nextBoolean()) {
                    population[i].mutate(random);
                } else {
                    population[i] = Automaton.createRandomAutomaton(population[i].states, random);
                }
            }
        }
    }

    static class Field {
        static final int[] dx = new int[]{0, 1, 0, -1};
        static final int[] dy = new int[]{1, 0, -1, 0};

        int size;
        int[][] field;
        int[][] copy;
        int applesQty;

        Field(int size) {
            this.size = size;
            field = new int[size][size];
            copy = new int[size][size];
        }

        public static Field createField(int size, BufferedReader reader) throws IOException {
            Field field = new Field(size);
            for (int i = 0; i < size; ++i) {
                String row = reader.readLine();
                assert row != null && row.length() == size;
                for (int j = 0; j < size; ++j) {
                    field.field[i][j] = row.charAt(j) == '*' ? 1 : 0;
                    field.applesQty += field.field[i][j];
                }
                System.arraycopy(field.field[i], 0, field.copy[i], 0, size);
            }
            return field;
        }

        public int estimate(Automaton automaton, int maxSteps) {
            int x = 0;
            int y = 0;
            int state = automaton.startState;
            int direction = 0; // EAST
            int counter = 0;
            int last = maxSteps + 1;
            for (int i = 0; i < maxSteps; ++i) {
                if (field[x][y] == 1) {
                    last = i;
                    ++counter;
                    field[x][y] = 0;
                }
                int nx = (x + dx[direction] + size) % size;
                int ny = (y + dy[direction] + size) % size;
                switch (automaton.output[field[nx][ny]][state]) {
                    case Automaton.LEFT:
                        direction = (direction + 3) % 4;
                        break;
                    case Automaton.RIGHT:
                        direction = (direction + 1) % 4;
                        break;
                    case Automaton.MOVE:
                        x = nx;
                        y = ny;
                        break;
                    default:
                        throw new AssertionError("unknown output: " + automaton.output[field[nx][ny]][state]);
                }
                state = automaton.edges[field[nx][ny]][state];
            }
            if (field[x][y] == 1) {
                last = maxSteps;
                ++counter;
            }
            cleanup();
            int result = counter * maxSteps + (maxSteps - last);
            return counter < applesQty ? result : -(result + 1);
        }

        private void cleanup() {
            for (int i = 0; i < size; ++i) {
                System.arraycopy(copy[i], 0, field[i], 0, size);
            }
        }
    }

    /**
     * Mealy machine
     */
    static class Automaton {
        static final char[] OUTPUT_SYMBOLS = "LRM".toCharArray();
        static final int LEFT = 0;
        static final int RIGHT = 1;
        static final int MOVE = 2;

        int states;
        int startState;
        int[][] edges;
        int[][] output;

        Automaton(int states, int startState) {
            this.states = states;
            this.startState = startState;
            this.edges = new int[2][states];
            this.output = new int[2][states];
        }

        Automaton(int states) {
            this(states, 0);
        }

        public static Automaton createRandomAutomaton(int states, Random random) {
            Automaton automaton = new Automaton(states, random.nextInt(states));
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < states; ++j) {
                    automaton.edges[i][j] = random.nextInt(states);
                    if (i == 0) {
                        automaton.output[i][j] = random.nextInt(OUTPUT_SYMBOLS.length);
                    } else { // as soon as we see an apple, we eat it! /hack
                        automaton.output[i][j] = MOVE;
                    }
                }
            }
            return automaton;
        }

        public void mutate(Random random) {
            int type = random.nextInt(4);
            int hashedEdge, input, state;
            switch (type) {
                case 0: // start state
                    startState = random.nextInt(states);
                    break;
                case 1:
                    hashedEdge = random.nextInt(2 * states);
                    input = hashedEdge / states;
                    state = hashedEdge % states;
                    int nextOutput = 0;
                    if (output[input][state] == nextOutput) ++nextOutput;
                    if (random.nextBoolean()) ++nextOutput;
                    if (output[input][state] == nextOutput) ++nextOutput;
                    output[input][state] = nextOutput;
                    break;
                case 2:
                    hashedEdge = random.nextInt(2 * states);
                    input = hashedEdge / states;
                    state = hashedEdge % states;
                    edges[input][state] = random.nextInt(states);
                    break;
                case 3:
                    state = random.nextInt(states);
                    output[0][state] ^= output[1][state];
                    output[1][state] ^= output[0][state];
                    output[0][state] ^= output[1][state];
                    edges[0][state] ^= edges[1][state];
                    edges[1][state] ^= edges[0][state];
                    edges[0][state] ^= edges[1][state];
                    break;
            }
        }

        public static void crossover(Automaton first, Automaton second, Random random) {
            if (random.nextBoolean()) {
                int tmp = first.startState;
                first.startState = second.startState;
                second.startState = tmp;
            }
            int states = first.states;
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < states; ++j) {
                    if (random.nextBoolean()) {
                        int tmp = first.output[i][j];
                        first.output[i][j] = second.output[i][j];
                        second.output[i][j] = tmp;
                        tmp = first.edges[i][j];
                        first.edges[i][j] = second.edges[i][j];
                        second.edges[i][j] = tmp;
                    }
                }
            }
        }

        public static void secondCrossover(Automaton first, Automaton second, int[][] field, int steps, Random random) {
            // nothing now
        }

        @Override
        public Automaton clone() {
            Automaton automaton = new Automaton(states, startState);
            automaton.edges = new int[2][states];
            System.arraycopy(edges[0], 0, automaton.edges[0], 0, states);
            System.arraycopy(edges[1], 0, automaton.edges[1], 0, states);
            automaton.output = new int[2][states];
            System.arraycopy(output[0], 0, automaton.output[0], 0, states);
            System.arraycopy(output[1], 0, automaton.output[1], 0, states);
            return automaton;
        }

        public void print(PrintWriter out) {
            swap(0, startState);
            for (int i = 0; i < states; ++i) {
                out.println((edges[0][i] + 1) + " " + (edges[1][i] + 1) + " " +
                        OUTPUT_SYMBOLS[output[0][i]] + " " + OUTPUT_SYMBOLS[output[1][i]]);
            }
            swap(0, startState);
        }

        private void swap(int first, int second) {
            if (first == second) {
                return;
            }
            for (int i = 0; i < 2; ++i) {
                int tmp = edges[i][first];
                edges[i][first] = edges[i][second];
                edges[i][second] = tmp;
                tmp = output[i][first];
                output[i][first] = output[i][second];
                output[i][second] = tmp;
            }
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < states; ++j) {
                    if (edges[i][j] == first || edges[i][j] == second) {
                        edges[i][j] ^= first ^ second;
                    }
                }
            }
        }
    }
}
