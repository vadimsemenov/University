import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class ArtificialBrute {
    public static void main(String[] args) {
        final int tests = 2;
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        try (BufferedReader in = Files.newBufferedReader(Paths.get("artificial-bf.in"))) {
            List<Future<?>> futures = new ArrayList<>(tests);
            for (int test = 1; test <= tests; ++test) {
                StringTokenizer tokenizer = new StringTokenizer(in.readLine());
                int fieldSize = Integer.parseInt(tokenizer.nextToken());
                int automatonSize = Integer.parseInt(tokenizer.nextToken());
                int maxSteps = Integer.parseInt(tokenizer.nextToken());
                Worker worker = new Worker(test, maxSteps, Field.createField(fieldSize, in), Automaton.simpleAutomaton(automatonSize));
                futures.add(pool.submit(worker));
            }
            pool.shutdown();
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class Worker implements Runnable {
        final int id;
        final int maxSteps;
        final Field field;
        Automaton current;

        Worker(int id, int maxSteps, Field field, Automaton start) {
            this.id = id;
            this.maxSteps = maxSteps;
            this.field = field;
            this.current = start;
        }

        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();
            while (field.estimate(current, maxSteps) >= 0) {
                current = current.getNext();
            }
            try (PrintWriter out = new PrintWriter("aritficial-bf-" + id + ".out")) {
                current.print(out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final long finishTime = System.currentTimeMillis();
            System.out.println("done #" + id + " (in " + (finishTime - startTime) + "ms.)");
        }
    }

    static class Automaton {
        static final char[] OUTPUT_SYMBOLS = "LRM".toCharArray();
        static final int LEFT = 0;
        static final int RIGHT = 1;
        static final int MOVE = 2;

        int states;
        int[][] edges;
        int[][] output;

        Automaton(int states) {
            this.states = states;
            this.edges = new int[2][states];
            this.output = new int[2][states];
        }

        public static Automaton simpleAutomaton(int states) {
            Automaton automaton = new Automaton(states);
            for (int i = 0; i < states; ++i) {
                automaton.output[1][i] = MOVE;
            }
            return automaton;
        }

        public Automaton getNext() {
            Automaton automaton = clone();
            for (int i = 0; i < states; ++i) {
                if (automaton.output[0][i] != MOVE) {
                    ++automaton.output[0][i];
                    return automaton;
                } else {
                    automaton.output[0][i] = 0;
                }
                for (int input = 0; input < 2; ++input) {
                    if (automaton.edges[input][i] != states - 1) {
                        ++automaton.edges[input][i];
                        return automaton;
                    } else {
                        automaton.edges[input][i] = 0;
                    }
                }
            }
            return null;
        }

        @Override
        public Automaton clone() {
            Automaton automaton = new Automaton(states);
            automaton.edges = new int[2][states];
            System.arraycopy(edges[0], 0, automaton.edges[0], 0, states);
            System.arraycopy(edges[1], 0, automaton.edges[1], 0, states);
            automaton.output = new int[2][states];
            System.arraycopy(output[0], 0, automaton.output[0], 0, states);
            System.arraycopy(output[1], 0, automaton.output[1], 0, states);
            return automaton;
        }

        public void print(PrintWriter out) {
            for (int i = 0; i < states; ++i) {
                out.println((edges[0][i] + 1) + " " + (edges[1][i] + 1) + " " +
                        OUTPUT_SYMBOLS[output[0][i]] + " " + OUTPUT_SYMBOLS[output[1][i]]);
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
            int state = 0;
            int direction = 0; // EAST
            int counter = 0;
            int last = maxSteps;
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
}
