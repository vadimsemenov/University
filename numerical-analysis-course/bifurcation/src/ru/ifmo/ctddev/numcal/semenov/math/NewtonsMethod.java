package ru.ifmo.ctddev.numcal.semenov.math;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class NewtonsMethod {
    public static Point findRoot(Function function, Point start, double eps) {
        List<Point> path = findPath(function, start, eps);
        return path.get(path.size() - 1);
    }

    public static List<Point> findPath(Function function, Point start, double eps) {
        List<Point> path = new ArrayList<>();
        path.add(start);
        Point current = start;
        while (function.apply(current).abs() > eps) {
            Point foo = function.apply(current);
            Point bar = function.applyDerivative(current);
            current = current.sub(foo.div(bar));
            path.add(current);
        }
        return path;
    }
}
