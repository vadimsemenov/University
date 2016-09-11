package ru.ifmo.ctddev.numcal.semenov.math;

/**
 * @author Vadim Semenov (semenov@rain.ifmo.ru)
 */
public abstract class Function {
    public static final Function sample = new Function() {
        private final Point one = new Point(1, 0);
        private final Point three = new Point(3, 0);

        @Override
        public Point apply(Point arg) {
            return arg.mul(arg.mul(arg)).sub(one);
        }

        @Override
        public Point applyDerivative(Point arg) {
            return three.mul(arg.mul(arg));
        }
    };

    public abstract Point apply(Point arg);
    public abstract Point applyDerivative(Point arg);
}
