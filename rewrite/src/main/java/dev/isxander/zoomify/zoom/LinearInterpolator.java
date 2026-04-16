package dev.isxander.zoomify.zoom;

public abstract class LinearInterpolator implements Interpolator {
    protected boolean goingIn = true;

    @Override
    public double tickInterpolation(double targetInterpolation, double currentInterpolation, double tickDelta) {
        if (targetInterpolation > currentInterpolation) {
            goingIn = true;
            return Math.min(
                currentInterpolation + getTimeIncrement(false, tickDelta, targetInterpolation, currentInterpolation),
                targetInterpolation
            );
        }
        if (targetInterpolation < currentInterpolation) {
            goingIn = false;
            return Math.max(
                currentInterpolation - getTimeIncrement(true, tickDelta, targetInterpolation, currentInterpolation),
                targetInterpolation
            );
        }

        goingIn = true;
        return targetInterpolation;
    }

    @Override
    public double modifyInterpolation(double interpolation) {
        return interpolation;
    }

    @Override
    public double modifyPrevInterpolation(double interpolation) {
        return interpolation;
    }

    @Override
    public boolean isSmooth() {
        return true;
    }

    protected abstract double getTimeIncrement(
        boolean zoomingOut,
        double tickDelta,
        double targetInterpolation,
        double currentInterpolation
    );
}
