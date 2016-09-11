package ru.ifmo.ctddev.numcal.semenov;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ru.ifmo.ctddev.numcal.semenov.math.Function;
import ru.ifmo.ctddev.numcal.semenov.math.NewtonsMethod;
import ru.ifmo.ctddev.numcal.semenov.math.Point;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class ConvergenceController implements Initializable {
    private final boolean DRAW_GRID = false;
    private final double BUBEN = 4.7; // empiric constant to draw nice fractal
    private final double STEP_MUL_SCALE = 0.5; // empiric constant for fast drawing (wait about a minute)
    private final double SCALE = 75;
    private final double STEP = STEP_MUL_SCALE / SCALE;
    private final double EPS = 1e-6; // empiric constant

    private double XMIN;
    private double XMAX;
    private double YMIN;
    private double YMAX;

    @FXML
    private Canvas fractalCanvas;
    @FXML
    private Canvas pathCanvas;
//    @FXML
//    private ProgressBar progressBar;

    private GraphicsContext fractalContext;
    private GraphicsContext pathContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert fractalCanvas.getWidth() == pathCanvas.getWidth() && fractalCanvas.getHeight() == pathCanvas.getHeight();
        fractalContext = fractalCanvas.getGraphicsContext2D();
        pathContext = pathCanvas.getGraphicsContext2D();
        System.out.println("Contexts initialized");
        XMIN = -fractalCanvas.getHeight() / SCALE / 2;
        XMAX = fractalCanvas.getHeight() / SCALE / 2;
        YMIN = -fractalCanvas.getWidth() / SCALE / 2;
        YMAX = fractalCanvas.getWidth() / SCALE / 2;
        drawCanvas();
    }

    @FXML
    public void drawPath(MouseEvent event) {
        pathContext.clearRect(0, 0, pathCanvas.getWidth(), pathCanvas.getHeight());
        final Point start = new Point(xViewToModel(event.getX()), yViewToModel(event.getY()));
//        System.out.println(start);
        final List<Point> path = NewtonsMethod.findPath(Function.sample, start, EPS);
        pathContext.beginPath();
        pathContext.moveTo(xModelToView(start.x), yModelToView(start.y));
        for (Point point : path) {
            pathContext.lineTo(xModelToView(point.x), yModelToView(point.y));
        }
        pathContext.stroke();
    }

    private void drawCanvas() {
        final long startTime = System.currentTimeMillis();
        long canvasTime = 0;
        System.out.println("drawCanvas()");

        // roots of Function.sample = (z^3 - 1)
        final Point[] roots = new Point[]{
                new Point(1, 0),
                new Point(-0.5, 0.8660254038),
                new Point(-0.5, -0.8660254038)
        }; // TODO: calculate on fly, not hardcode
        final Color[] colors = new Color[]{
                Color.RED.brighter().brighter(),
                Color.CORNFLOWERBLUE.brighter().brighter(),
                Color.GREEN.brighter().brighter()
        };
//        Map<Integer, Integer> sizes = new HashMap<>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        final double X_CYCLE_ITERS = (XMAX - XMIN) / STEP;
        final double Y_CYCLE_ITERS = (YMAX - YMIN) / STEP;
        for (double x = XMIN; x < XMAX; x += STEP) {
            for (double y = YMIN; y < YMAX; y += STEP) {
//                progressBar.setProgress((x * Y_CYCLE_ITERS + y) / (X_CYCLE_ITERS * Y_CYCLE_ITERS));
                Point start = new Point(x + STEP / 2, y + STEP / 2); // middle of current rectangle
                List<Point> path = NewtonsMethod.findPath(Function.sample, start, EPS);
                Point root = path.get(path.size() - 1);
                max = Math.max(max, path.size());
                min = Math.min(min, path.size());
                int id = 0;
                for (int i = 1; i < roots.length; ++i) {
                    if (root.distanceTo(roots[i]) < root.distanceTo(roots[id])) {
                        id = i;
                    }
                }
//                Integer foo = sizes.get(path.size());
//                if (foo == null) foo = 0;
//                sizes.put(path.size(), foo + 1);
                final long begin = System.currentTimeMillis();
                fractalContext.setFill(colors[id].deriveColor(0.0, 1.0, BUBEN / path.size(), 1.0));
//                System.out.println(String.format("%f %f %f %f", (x - XMIN) * SCALE, (y - YMIN) * SCALE, STEP * SCALE, STEP * SCALE));
//                System.out.println(root + " " + id);
                fractalContext.fillRect(xModelToView(x), yModelToView(y), STEP * SCALE, STEP * SCALE);
                canvasTime += System.currentTimeMillis() - begin;
            }
        }
//        System.out.println(sizes);
        if (DRAW_GRID) {
            final double GRID_GAP = 0.5;
            {
                double current = Math.ceil(XMIN);
                while (current < XMAX) {
                    fractalContext.moveTo(xModelToView(current), 0);
                    fractalContext.setLineWidth(1);
                    fractalContext.lineTo(xModelToView(current), fractalCanvas.getWidth());
                    fractalContext.stroke();
                    current += GRID_GAP;
                }
            }
            {
                double current = Math.ceil(YMIN);
                while (current < YMAX) {
                    fractalContext.moveTo(0, yModelToView(current));
                    fractalContext.setLineWidth(1);
                    fractalContext.lineTo(fractalCanvas.getHeight(), yModelToView(current));
                    fractalContext.stroke();
                    current += GRID_GAP;
                }
            }
        }
        System.out.println("done drawCanvas() in " + (System.currentTimeMillis() - startTime) + "ms. (operations with canvas took " + canvasTime + "ms.");
    }

    private double xViewToModel(double x) {
        return x / SCALE + XMIN;
    }

    private double yViewToModel(double y) {
        return (fractalCanvas.getWidth() - y) / SCALE + YMIN;
    }

    private double xModelToView(double x) {
        return (x - XMIN) * SCALE;
    }

    private double yModelToView(double y) {
        return fractalCanvas.getWidth() - (y - YMIN) * SCALE;
    }
}
