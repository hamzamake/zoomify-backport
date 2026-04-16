package dev.isxander.zoomify.zoom;

import java.util.function.IntSupplier;

public class ZoomHelper {
    private final Interpolator initialInterpolator;
    private final Interpolator scrollInterpolator;

    private final IntSupplier initialZoom;
    private final IntSupplier zoomPerStep;
    private final IntSupplier maxScrollTiers;

    private double prevInitialInterpolation = 0.0D;
    private double initialInterpolation = 0.0D;
    private boolean zoomingLastTick = false;

    private double prevScrollInterpolation = 0.0D;
    private double scrollInterpolation = 0.0D;
    private int lastScrollTier = 0;

    private boolean resetting = false;
    private double resetMultiplier = 0.0D;

    public ZoomHelper(
        Interpolator initialInterpolator,
        Interpolator scrollInterpolator,
        IntSupplier initialZoom,
        IntSupplier zoomPerStep,
        IntSupplier maxScrollTiers
    ) {
        this.initialInterpolator = initialInterpolator;
        this.scrollInterpolator = scrollInterpolator;
        this.initialZoom = initialZoom;
        this.zoomPerStep = zoomPerStep;
        this.maxScrollTiers = maxScrollTiers;
    }

    public void tick(boolean zooming, int scrollTiers, double lastFrameDuration) {
        tickInitial(zooming, lastFrameDuration);
        tickScroll(scrollTiers, lastFrameDuration);
    }

    private void tickInitial(boolean zooming, double lastFrameDuration) {
        if (zooming && !zoomingLastTick) {
            resetting = false;
        }

        double targetZoom = zooming ? 1.0D : 0.0D;

        prevInitialInterpolation = initialInterpolation;
        initialInterpolation = initialInterpolator.tickInterpolation(targetZoom, initialInterpolation, lastFrameDuration);
        prevInitialInterpolation = initialInterpolator.modifyPrevInterpolation(prevInitialInterpolation);

        if (!initialInterpolator.isSmooth()) {
            prevInitialInterpolation = initialInterpolation;
        }

        zoomingLastTick = zooming;
    }

    private void tickScroll(int scrollTiers, double lastFrameDuration) {
        if (scrollTiers > lastScrollTier) {
            resetting = false;
        }

        double targetZoom;
        if (maxScrollTiers.getAsInt() > 0) {
            targetZoom = scrollTiers / (double) maxScrollTiers.getAsInt();
        } else {
            targetZoom = 0.0D;
        }

        prevScrollInterpolation = scrollInterpolation;
        scrollInterpolation = scrollInterpolator.tickInterpolation(targetZoom, scrollInterpolation, lastFrameDuration);
        prevScrollInterpolation = scrollInterpolator.modifyPrevInterpolation(prevScrollInterpolation);

        if (!initialInterpolator.isSmooth()) {
            prevInitialInterpolation = initialInterpolation;
        }

        lastScrollTier = scrollTiers;
    }

    public double getZoomDivisor(float tickDelta) {
        double initialMultiplier = getInitialZoomMultiplier(tickDelta);
        double baseDivisor = 1.0D / initialMultiplier;

        double scrollT;
        if (resetting) {
            scrollT = 0.0D;
        } else {
            if (scrollInterpolator.isSmooth()) {
                double interpolated = MathUtil.lerp(tickDelta, prevScrollInterpolation, scrollInterpolation);
                scrollT = scrollInterpolator.modifyInterpolation(interpolated);
            } else {
                scrollT = scrollInterpolation;
            }
        }

        double stepMultiplier = zoomPerStep.getAsInt() / 100.0D;
        int maxSteps = maxScrollTiers.getAsInt();
        double currentStep = scrollT * maxSteps;

        double rawDivisor = baseDivisor * Math.pow(stepMultiplier, currentStep);
        double finalDivisor = MathUtil.clamp(rawDivisor, 0.5D, 500.0D);

        if (initialInterpolation == 0.0D && scrollInterpolation == 0.0D) {
            resetting = false;
        }

        if (!resetting) {
            resetMultiplier = 1.0D / finalDivisor;
        }

        return finalDivisor;
    }

    private double getInitialZoomMultiplier(float tickDelta) {
        double interpolation;
        if (initialInterpolator.isSmooth()) {
            interpolation = initialInterpolator.modifyInterpolation(
                MathUtil.lerp(tickDelta, prevInitialInterpolation, initialInterpolation)
            );
        } else {
            interpolation = initialInterpolation;
        }

        double endValue;
        if (!resetting) {
            endValue = 1.0D / initialZoom.getAsInt();
        } else {
            endValue = resetMultiplier;
        }

        return MathUtil.lerp(interpolation, 1.0D, endValue);
    }

    public void reset() {
        if (!resetting && scrollInterpolation > 0.0D) {
            resetting = true;
            scrollInterpolation = 0.0D;
            prevScrollInterpolation = 0.0D;
        }
    }

    public void setToZero(boolean initial, boolean scroll) {
        if (initial) {
            initialInterpolation = 0.0D;
            prevInitialInterpolation = 0.0D;
            zoomingLastTick = false;
        }

        if (scroll) {
            scrollInterpolation = 0.0D;
            prevScrollInterpolation = 0.0D;
            lastScrollTier = 0;
        }

        resetting = false;
    }

    public void skipInitial() {
        initialInterpolation = 1.0D;
        prevInitialInterpolation = 1.0D;
    }
}
