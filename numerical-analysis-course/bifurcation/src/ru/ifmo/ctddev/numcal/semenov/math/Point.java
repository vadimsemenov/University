package ru.ifmo.ctddev.numcal.semenov.math;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    public Point sub(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    public Point mul(Point other) {
        return new Point(this.x * other.x - this.y * other.y, this.x * other.y + this.y * other.x);
    }

    public Point mul(double k) {
        return new Point(k * x, k * y);
    }

    public Point div(Point other) {
        return mul(other.conj()).div(other.squaredAbs());
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Point div(double k) {
        return new Point(x / k, y / k);
    }

    public Point conj() {
        return new Point(x, -y);
    }

    public double abs() {
        return Math.sqrt(squaredAbs());
    }

    public double distanceTo(Point other) {
        return Math.sqrt(fastHypot(this, other));
    }

    private double squaredAbs() {
        return x * x + y * y;
    }

    private static double fastHypot(Point first, Point second) {
        final double dx = first.x - second.x;
        final double dy = first.y - second.y;
        return dx * dx + dy * dy;
    }
}
