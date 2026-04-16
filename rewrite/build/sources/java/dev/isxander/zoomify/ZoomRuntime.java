package dev.isxander.zoomify;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import dev.isxander.zoomify.config.ZoomKeyBehaviour;
import dev.isxander.zoomify.config.ZoomifyConfig;
import dev.isxander.zoomify.zoom.InstantInterpolator;
import dev.isxander.zoomify.zoom.MathUtil;
import dev.isxander.zoomify.zoom.SmoothInterpolator;
import dev.isxander.zoomify.zoom.TimedInterpolator;
import dev.isxander.zoomify.zoom.TransitionInterpolator;
import dev.isxander.zoomify.zoom.ZoomHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class ZoomRuntime {
    private static final String KEY_CATEGORY = "key.category.zoomify.category";

    private static final KeyBinding ZOOM_KEY = new KeyBinding("zoomify.key.zoom", Keyboard.KEY_C, KEY_CATEGORY);
    private static final KeyBinding SECONDARY_ZOOM_KEY = new KeyBinding("zoomify.key.zoom.secondary", Keyboard.KEY_F6, KEY_CATEGORY);
    private static final KeyBinding SCROLL_ZOOM_IN_KEY = new KeyBinding("zoomify.key.zoom.in", Keyboard.KEY_NONE, KEY_CATEGORY);
    private static final KeyBinding SCROLL_ZOOM_OUT_KEY = new KeyBinding("zoomify.key.zoom.out", Keyboard.KEY_NONE, KEY_CATEGORY);

    private static final ZoomEventHandler EVENT_HANDLER = new ZoomEventHandler();

    private static final ZoomHelper ZOOM_HELPER = new ZoomHelper(
        new TransitionInterpolator(
            () -> ZoomifyConfig.zoomInTransition,
            () -> ZoomifyConfig.zoomOutTransition,
            () -> ZoomifyConfig.zoomInTime,
            () -> ZoomifyConfig.zoomOutTime
        ),
        new SmoothInterpolator(() -> MathUtil.lerp(ZoomifyConfig.scrollZoomSmoothness / 100.0D, 1.0D, 0.1D)),
        () -> ZoomifyConfig.initialZoom,
        () -> ZoomifyConfig.zoomPerStep,
        () -> ZoomifyConfig.scrollStepCount
    );

    private static final ZoomHelper SECONDARY_ZOOM_HELPER = new ZoomHelper(
        new TimedInterpolator(() -> ZoomifyConfig.secondaryZoomInTime, () -> ZoomifyConfig.secondaryZoomOutTime),
        new InstantInterpolator(),
        () -> ZoomifyConfig.secondaryZoomAmount,
        () -> 100,
        () -> 0
    );

    private static boolean zooming = false;
    private static boolean secondaryZooming = false;

    private static int scrollSteps = 0;
    private static double previousZoomDivisor = 1.0D;
    private static float lastRenderTickDelta = 1.0F;

    private static boolean firstLaunchHintPending = false;

    private static boolean sensitivityOverrideActive = false;
    private static float baseMouseSensitivity = 0.5F;

    private static boolean smoothCameraOverrideActive = false;
    private static boolean baseSmoothCamera = false;

    private static boolean viewBobbingOverrideActive = false;
    private static boolean baseViewBobbing = true;

    private ZoomRuntime() {
    }

    public static void initialize() {
        ClientRegistry.registerKeyBinding(ZOOM_KEY);
        ClientRegistry.registerKeyBinding(SECONDARY_ZOOM_KEY);
        ClientRegistry.registerKeyBinding(SCROLL_ZOOM_IN_KEY);
        ClientRegistry.registerKeyBinding(SCROLL_ZOOM_OUT_KEY);

        if (ZoomifyConfig.firstLaunch) {
            firstLaunchHintPending = true;
            ZoomifyConfig.firstLaunch = false;
            ZoomifyConfig.getConfiguration().get("misc", "firstLaunch", true).set(false);
            ZoomifyConfig.save();
        }
    }

    public static void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
        FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
    }

    public static void onConfigReload() {
        scrollSteps = Math.max(0, Math.min(scrollSteps, getMaxScrollTiers()));
    }

    public static void onClientTickEnd() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null) {
            return;
        }

        switch (ZoomifyConfig.zoomKeyBehaviour) {
            case HOLD:
                zooming = isKeyCurrentlyDown(ZOOM_KEY);
                break;
            case TOGGLE:
                while (ZOOM_KEY.isPressed()) {
                    zooming = !zooming;
                }
                break;
            default:
                break;
        }

        while (SECONDARY_ZOOM_KEY.isPressed()) {
            secondaryZooming = !secondaryZooming;
        }

        if (ZoomifyConfig.keybindScrolling) {
            while (SCROLL_ZOOM_IN_KEY.isPressed()) {
                scrollSteps++;
            }
            while (SCROLL_ZOOM_OUT_KEY.isPressed()) {
                scrollSteps--;
            }
            scrollSteps = Math.max(0, Math.min(scrollSteps, getMaxScrollTiers()));
        }

        // Spyglass integration settings are preserved in config for parity,
        // but vanilla 1.7.10 has no spyglass item to bind behavior against.

        ZOOM_HELPER.tick(zooming, scrollSteps, 0.05D);
        SECONDARY_ZOOM_HELPER.tick(secondaryZooming, scrollSteps, 0.05D);

        applyRuntimeOptionOverrides(minecraft);
        showFirstLaunchHintIfNeeded(minecraft);
    }

    public static void onRenderTickStart(float renderTickDelta) {
        lastRenderTickDelta = renderTickDelta;
    }

    public static void applyFovModifier(FOVUpdateEvent event) {
        // 1.7.10 does not expose separate world/hand FOV events, so we use a
        // stack-based hand render heuristic when hand FOV should remain unzoomed.
        if (!ZoomifyConfig.affectHandFov && isLikelyHandRenderCall()) {
            return;
        }

        double divisor = getCombinedZoomDivisor(lastRenderTickDelta);
        if (divisor <= 0.0D) {
            return;
        }

        event.newfov = (float) (event.newfov / divisor);
    }

    public static boolean handleMouseScrollEvent(int wheelDelta) {
        if (!ZoomifyConfig.scrollZoom || !zooming || ZoomifyConfig.keybindScrolling) {
            return false;
        }

        if (wheelDelta > 0) {
            scrollSteps++;
        } else if (wheelDelta < 0) {
            scrollSteps--;
        }

        scrollSteps = Math.max(0, Math.min(scrollSteps, getMaxScrollTiers()));
        return wheelDelta != 0;
    }

    public static boolean shouldHideHud() {
        return secondaryZooming && ZoomifyConfig.secondaryHideHUDOnZoom;
    }

    public static boolean isZooming() {
        return zooming;
    }

    public static boolean isSecondaryZooming() {
        return secondaryZooming;
    }

    public static double getPreviousZoomDivisor() {
        return previousZoomDivisor;
    }

    private static int getMaxScrollTiers() {
        return ZoomifyConfig.scrollStepCount;
    }

    private static double getCombinedZoomDivisor(float tickDelta) {
        if (!zooming) {
            if (!ZoomifyConfig.retainZoomSteps) {
                scrollSteps = 0;
            }
            ZOOM_HELPER.reset();
        }

        double primaryDivisor = ZOOM_HELPER.getZoomDivisor(tickDelta);
        previousZoomDivisor = primaryDivisor;

        return primaryDivisor * SECONDARY_ZOOM_HELPER.getZoomDivisor(tickDelta);
    }

    private static void applyRuntimeOptionOverrides(Minecraft minecraft) {
        boolean primaryZoomAnimating = Math.abs(previousZoomDivisor - 1.0D) > 0.0001D;

        boolean shouldForceSmoothCamera = secondaryZooming || (zooming && ZoomifyConfig.cinematicCamera > 0);

        if (shouldForceSmoothCamera) {
            if (!smoothCameraOverrideActive) {
                smoothCameraOverrideActive = true;
                baseSmoothCamera = minecraft.gameSettings.smoothCamera;
            }
            minecraft.gameSettings.smoothCamera = true;
        } else if (smoothCameraOverrideActive) {
            minecraft.gameSettings.smoothCamera = baseSmoothCamera;
            smoothCameraOverrideActive = false;
        }

        if (zooming || primaryZoomAnimating) {
            if (!sensitivityOverrideActive) {
                sensitivityOverrideActive = true;
                baseMouseSensitivity = minecraft.gameSettings.mouseSensitivity;
            }

            double factor = MathUtil.lerp(
                ZoomifyConfig.relativeSensitivity / 100.0D,
                1.0D,
                previousZoomDivisor
            );

            if (factor <= 0.0D) {
                factor = 0.0001D;
            }

            minecraft.gameSettings.mouseSensitivity = (float) MathUtil.clamp(
                baseMouseSensitivity / factor,
                0.0D,
                1.0D
            );
        } else if (sensitivityOverrideActive) {
            minecraft.gameSettings.mouseSensitivity = baseMouseSensitivity;
            sensitivityOverrideActive = false;
        }

        if ((zooming || primaryZoomAnimating) && ZoomifyConfig.relativeViewBobbing) {
            // Legacy fallback: disable bobbing while zoomed instead of scaling
            // bob amplitude, which requires mixin/coremod-level hooks in 1.7.10.
            if (!viewBobbingOverrideActive) {
                viewBobbingOverrideActive = true;
                baseViewBobbing = minecraft.gameSettings.viewBobbing;
            }
            minecraft.gameSettings.viewBobbing = false;
        } else if (viewBobbingOverrideActive) {
            minecraft.gameSettings.viewBobbing = baseViewBobbing;
            viewBobbingOverrideActive = false;
        }
    }

    private static void showFirstLaunchHintIfNeeded(Minecraft minecraft) {
        if (!firstLaunchHintPending || minecraft.thePlayer == null) {
            return;
        }

        firstLaunchHintPending = false;

        if (isZoomKeyConflicting(minecraft)) {
            minecraft.thePlayer.addChatMessage(new ChatComponentText("[Zoomify] "
                + "your zoom key conflicts with another keybind. Change it in Controls."));
        }
    }

    private static boolean isZoomKeyConflicting(Minecraft minecraft) {
        int zoomKeyCode = ZOOM_KEY.getKeyCode();
        if (zoomKeyCode == Keyboard.KEY_NONE) {
            return false;
        }

        for (KeyBinding keyBinding : minecraft.gameSettings.keyBindings) {
            if (keyBinding != ZOOM_KEY && keyBinding.getKeyCode() == zoomKeyCode) {
                return true;
            }
        }

        return false;
    }

    private static boolean isLikelyHandRenderCall() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String method = element.getMethodName();
            if ("func_78476_b".equals(method) || "renderHand".equals(method) || "renderItemInFirstPerson".equals(method)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isKeyCurrentlyDown(KeyBinding keyBinding) {
        int keyCode = keyBinding.getKeyCode();

        if (keyCode == Keyboard.KEY_NONE) {
            return false;
        }

        if (keyCode < 0) {
            return Mouse.isButtonDown(keyCode + 100);
        }

        return Keyboard.isKeyDown(keyCode);
    }
}
