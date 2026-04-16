package dev.isxander.zoomify.zoom;

public interface Interpolator {
    double tickInterpolation(double targetInterpolation, double currentInterpolation, double tickDelta);

    double modifyInterpolation(double interpolation);

    double modifyPrevInterpolation(double interpolation);

    boolean isSmooth();
}
