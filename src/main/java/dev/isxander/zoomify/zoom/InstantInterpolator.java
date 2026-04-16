package dev.isxander.zoomify.zoom;

public final class InstantInterpolator implements Interpolator {
    @Override
    public double tickInterpolation(double targetInterpolation, double currentInterpolation, double tickDelta) {
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
        return false;
    }
}
