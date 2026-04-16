package dev.isxander.zoomify.zoom;

public final class MathUtil {
    private MathUtil() {
    }

    public static double lerp(double t, double a, double b) {
        return a + (b - a) * t;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
