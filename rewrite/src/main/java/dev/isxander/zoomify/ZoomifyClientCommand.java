package dev.isxander.zoomify;

import dev.isxander.zoomify.config.ZoomifyConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class ZoomifyClientCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "zoomify";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/zoomify";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.displayGuiScreen(new ZoomifyConfigGui(minecraft.currentScreen));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
