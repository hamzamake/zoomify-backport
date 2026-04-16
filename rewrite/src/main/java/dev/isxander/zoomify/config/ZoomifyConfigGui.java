package dev.isxander.zoomify.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import dev.isxander.zoomify.ZoomifyLegacy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ZoomifyConfigGui extends GuiConfig {
    public ZoomifyConfigGui(GuiScreen parentScreen) {
        super(
            parentScreen,
            getConfigElements(),
            ZoomifyLegacy.MODID,
            false,
            false,
            I18n.format("zoomify.config.title")
        );
    }

    private static List<IConfigElement> getConfigElements() {
        Configuration config = ZoomifyConfig.getConfiguration();
        List<IConfigElement> elements = new ArrayList<IConfigElement>();

        if (config == null) {
            return elements;
        }

        for (String categoryName : config.getCategoryNames()) {
            elements.add(new ConfigElement(config.getCategory(categoryName)));
        }

        return elements;
    }
}
