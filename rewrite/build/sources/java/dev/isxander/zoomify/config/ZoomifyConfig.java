package dev.isxander.zoomify.config;

import dev.isxander.zoomify.zoom.TransitionType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public final class ZoomifyConfig {
    private static final String CATEGORY_BEHAVIOUR = "behaviour";
    private static final String CATEGORY_SCROLL = "scroll";
    private static final String CATEGORY_MISC = "misc";
    private static final String CATEGORY_SECONDARY = "secondary";
    private static final String CATEGORY_SPYGLASS = "spyglass";

    private static Configuration configuration;

    public static int initialZoom = 4;
    public static double zoomInTime = 1.0D;
    public static double zoomOutTime = 0.5D;
    public static TransitionType zoomInTransition = TransitionType.EASE_OUT_EXP;
    public static TransitionType zoomOutTransition = TransitionType.EASE_OUT_EXP;
    public static boolean affectHandFov = true;
    public static boolean retainZoomSteps = false;

    public static boolean scrollZoom = true;
    public static int scrollStepCount = 10;
    public static int zoomPerStep = 150;
    public static int scrollZoomSmoothness = 70;

    public static ZoomKeyBehaviour zoomKeyBehaviour = ZoomKeyBehaviour.HOLD;
    public static boolean keybindScrolling = false;
    public static int relativeSensitivity = 100;
    public static boolean relativeViewBobbing = true;
    public static int cinematicCamera = 0;

    public static SpyglassBehaviour spyglassBehaviour = SpyglassBehaviour.COMBINE;
    public static OverlayVisibility spyglassOverlayVisibility = OverlayVisibility.HOLDING;
    public static SoundBehaviour spyglassSoundBehaviour = SoundBehaviour.WITH_OVERLAY;

    public static int secondaryZoomAmount = 4;
    public static double secondaryZoomInTime = 10.0D;
    public static double secondaryZoomOutTime = 1.0D;
    public static boolean secondaryHideHUDOnZoom = true;

    public static boolean firstLaunch = true;

    private ZoomifyConfig() {
    }

    public static void initialize(File file) {
        if (configuration == null) {
            configuration = new Configuration(file);
        }
        sync();
    }

    public static void sync() {
        if (configuration == null) {
            return;
        }

        initialZoom = configuration.getInt(
            "initialZoom",
            CATEGORY_BEHAVIOUR,
            initialZoom,
            1,
            500,
            "Zoom level when first pressing zoom."
        );

        zoomInTime = getDouble("zoomInTime", CATEGORY_BEHAVIOUR, zoomInTime, "Seconds to complete zoom-in.");
        zoomOutTime = getDouble("zoomOutTime", CATEGORY_BEHAVIOUR, zoomOutTime, "Seconds to complete zoom-out.");

        zoomInTransition = TransitionType.fromString(
            configuration.get(
                CATEGORY_BEHAVIOUR,
                "zoomInTransition",
                zoomInTransition.name(),
                "Transition used while zooming in."
            ).getString(),
            zoomInTransition
        );

        zoomOutTransition = TransitionType.fromString(
            configuration.get(
                CATEGORY_BEHAVIOUR,
                "zoomOutTransition",
                zoomOutTransition.name(),
                "Transition used while zooming out."
            ).getString(),
            zoomOutTransition
        );

        affectHandFov = configuration.getBoolean(
            "affectHandFov",
            CATEGORY_BEHAVIOUR,
            affectHandFov,
            "If false, attempt to keep first-person hand FOV unzoomed."
        );

        retainZoomSteps = configuration.getBoolean(
            "retainZoomSteps",
            CATEGORY_BEHAVIOUR,
            retainZoomSteps,
            "Keep scroll zoom tier between activations."
        );

        scrollZoom = configuration.getBoolean(
            "scrollZoom",
            CATEGORY_SCROLL,
            scrollZoom,
            "Enable mouse wheel zoom stepping while zooming."
        );

        scrollStepCount = configuration.getInt(
            "scrollStepCount",
            CATEGORY_SCROLL,
            scrollStepCount,
            0,
            200,
            "Maximum number of scroll zoom tiers."
        );

        zoomPerStep = configuration.getInt(
            "zoomPerStep",
            CATEGORY_SCROLL,
            zoomPerStep,
            100,
            500,
            "Percent zoom multiplier per scroll tier (150 = 1.5x)."
        );

        scrollZoomSmoothness = configuration.getInt(
            "scrollZoomSmoothness",
            CATEGORY_SCROLL,
            scrollZoomSmoothness,
            0,
            100,
            "Scroll interpolation smoothing percentage."
        );

        zoomKeyBehaviour = ZoomKeyBehaviour.fromString(
            configuration.get(
                CATEGORY_MISC,
                "zoomKeyBehaviour",
                zoomKeyBehaviour.name(),
                "HOLD or TOGGLE."
            ).getString(),
            zoomKeyBehaviour
        );

        keybindScrolling = configuration.getBoolean(
            "keybindScrolling",
            CATEGORY_MISC,
            keybindScrolling,
            "Use dedicated zoom in/out keybinds instead of mouse wheel."
        );

        relativeSensitivity = configuration.getInt(
            "relativeSensitivity",
            CATEGORY_MISC,
            relativeSensitivity,
            1,
            400,
            "Relative mouse sensitivity while zoomed (100 = unchanged)."
        );

        relativeViewBobbing = configuration.getBoolean(
            "relativeViewBobbing",
            CATEGORY_MISC,
            relativeViewBobbing,
            "Reduce view bobbing while zoomed (legacy approximation in 1.7.10)."
        );

        cinematicCamera = configuration.getInt(
            "cinematicCamera",
            CATEGORY_MISC,
            cinematicCamera,
            0,
            400,
            "Cinematic smoothing intensity trigger (0 disables)."
        );

        spyglassBehaviour = SpyglassBehaviour.fromString(
            configuration.get(
                CATEGORY_SPYGLASS,
                "spyglassBehaviour",
                spyglassBehaviour.name(),
                "Spyglass integration mode; kept for config parity in legacy versions."
            ).getString(),
            spyglassBehaviour
        );

        spyglassOverlayVisibility = OverlayVisibility.fromString(
            configuration.get(
                CATEGORY_SPYGLASS,
                "spyglassOverlayVisibility",
                spyglassOverlayVisibility.name(),
                "Spyglass overlay visibility mode; legacy compatibility setting."
            ).getString(),
            spyglassOverlayVisibility
        );

        spyglassSoundBehaviour = SoundBehaviour.fromString(
            configuration.get(
                CATEGORY_SPYGLASS,
                "spyglassSoundBehaviour",
                spyglassSoundBehaviour.name(),
                "Spyglass sound behavior mode; legacy compatibility setting."
            ).getString(),
            spyglassSoundBehaviour
        );

        secondaryZoomAmount = configuration.getInt(
            "secondaryZoomAmount",
            CATEGORY_SECONDARY,
            secondaryZoomAmount,
            1,
            500,
            "Base zoom amount for secondary zoom."
        );

        secondaryZoomInTime = getDouble(
            "secondaryZoomInTime",
            CATEGORY_SECONDARY,
            secondaryZoomInTime,
            "Seconds to complete secondary zoom-in."
        );

        secondaryZoomOutTime = getDouble(
            "secondaryZoomOutTime",
            CATEGORY_SECONDARY,
            secondaryZoomOutTime,
            "Seconds to complete secondary zoom-out."
        );

        secondaryHideHUDOnZoom = configuration.getBoolean(
            "secondaryHideHUDOnZoom",
            CATEGORY_SECONDARY,
            secondaryHideHUDOnZoom,
            "Hide HUD while secondary zoom is active."
        );

        firstLaunch = configuration.getBoolean(
            "firstLaunch",
            CATEGORY_MISC,
            firstLaunch,
            "Internal flag used to detect first launch."
        );

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void save() {
        if (configuration != null && configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    private static double getDouble(String key, String category, double def, String comment) {
        Property property = configuration.get(category, key, def, comment);
        return property.getDouble(def);
    }
}
