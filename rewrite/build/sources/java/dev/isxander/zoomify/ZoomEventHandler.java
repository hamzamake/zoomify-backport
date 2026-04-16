package dev.isxander.zoomify;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.isxander.zoomify.config.ZoomifyConfig;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ZoomEventHandler {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ZoomRuntime.onClientTickEnd();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ZoomRuntime.onRenderTickStart(event.renderTickTime);
        }
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event) {
        ZoomRuntime.applyFovModifier(event);
    }

    @SubscribeEvent
    public void onMouseScroll(MouseEvent event) {
        if (ZoomRuntime.handleMouseScrollEvent(event.dwheel)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (ZoomRuntime.shouldHideHud() && event.type == RenderGameOverlayEvent.ElementType.ALL) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (ZoomifyLegacy.MODID.equals(event.modID)) {
            ZoomifyConfig.sync();
            ZoomRuntime.onConfigReload();
        }
    }
}
