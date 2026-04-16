package dev.isxander.zoomify;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dev.isxander.zoomify.config.ZoomifyConfig;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(
    modid = ZoomifyLegacy.MODID,
    name = ZoomifyLegacy.NAME,
    version = ZoomifyLegacy.VERSION,
    guiFactory = "dev.isxander.zoomify.config.ZoomifyGuiFactory",
    acceptableRemoteVersions = "*"
)
public class ZoomifyLegacy {
    public static final String MODID = "zoomify";
    public static final String NAME = "Zoomify";
    public static final String VERSION = "2.16.0-1.7.10";

    @Mod.Instance(MODID)
    public static ZoomifyLegacy instance;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        ZoomifyConfig.initialize(event.getSuggestedConfigurationFile());
        ZoomRuntime.initialize();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        ZoomRuntime.registerHandlers();
        ClientCommandHandler.instance.registerCommand(new ZoomifyClientCommand());
    }
}
