import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Emulator {
    private static final int MOVE = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;

    private static final int[] dx = new int[]{0, 1, 0, -1};
    private static final int[] dy = new int[]{1, 0, -1, 0};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PrintWriter out = new PrintWriter(System.out);
        int fieldSize = in.nextInt();
        int automatonSize = in.nextInt();
        int maxSteps = in.nextInt();
        char[][] field = new char[fieldSize][];
        for (int i = 0; i < fieldSize; ++i) {
            field[i] = in.next().toCharArray();
        }
        int[][] edges = new int[2][automatonSize];
        int[][] output = new int[2][automatonSize];
        for (int i = 0; i < automatonSize; ++i) {
            edges[0][i] = in.nextInt() - 1;
            edges[1][i] = in.nextInt() - 1;
            output[0][i] = directionId(in.next());
            output[1][i] = directionId(in.next());
        }

        int state = 0;
        int direction = 0;
        int x = 0;
        int y = 0;
        int step = 0;
        while (in.hasNext()) {
            in.next();
            out.println("step = " + step++ + ", state = " + (state + 1));
            print(field, state, direction, x, y, edges, output, out);
            out.flush();
            int nx = (x + dx[direction] + fieldSize) % fieldSize;
            int ny = (y + dy[direction] + fieldSize) % fieldSize;
            int apple = field[nx][ny] == '*' ? 1 : 0;
            field[nx][ny] = '.';
            if (output[apple][state] == MOVE) {
                x = nx;
                y = ny;
            } else if (output[apple][state] == LEFT) {
                direction = (direction + 3) % 4;
            } else if (output[apple][state] == RIGHT) {
                direction = (direction + 1) % 4;
            }
            state = edges[apple][state];
        }
        in.close();
        out.close();
    }

    private static void print(char[][] field, int state, int direction, int x, int y, int[][] edges, int[][] output,
                              PrintWriter out) {
        for (int i = 0; i < field.length; ++i) {
            for (int j = 0; j < field[i].length; ++j) {
                if (i == x && j == y) {
                    char ch;
                    switch (direction) {
                        case 0:
                            ch = '>';
                            break;
                        case 1:
                            ch = 'v';
                            break;
                        case 2:
                            ch = '<';
                            break;
                        case 3:
                            ch = '^';
                            break;
                        default:
                            throw new AssertionError("impossible: " + direction);
                    }
                    out.print(ch);
                } else {
                    out.print(field[i][j]);
                }
            }
            out.println();
        }
    }

    private static int directionId(String direction) {
        switch (direction) {
            case "L":
                return LEFT;
            case "R":
                return RIGHT;
            case "M":
                return MOVE;
            default:
                throw new AssertionError("impossible: " + direction);
        }
    }
}
