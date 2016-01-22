import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Build {
    public static void main(String[] args) {
        try (PrintWriter out = new PrintWriter("artificial.py")) {
            int tests = 10;
            out.println("x = int(open(\"artificial.in\", \"r\").read())");
            out.println("with open(\"artificial.out\", \"w\") as f:");
            for (int test = 1; test <= tests; ++test) {
                out.println("\tif x == " + test + ":");
                out.print("\t\tf.write(\"\"\"");
                try (Scanner in = new Scanner(new FileInputStream("artificial-" + test + ".out"))) {
                    while (in.hasNext()) {
                        out.println(in.nextLine());
                    }
                }
                out.println("\"\"\")");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
