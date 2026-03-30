package dev.phonis.sharedwaypoints.client.keybindings;

import com.mojang.blaze3d.platform.InputConstants;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.gui.ConfigScreen;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class Keybindings
{

    private static final KeyMapping.Category category = KeyMapping.Category.register(ResourceLocation.parse("sharedwaypoints:sharedwaypoints"));
    public static final KeyMapping openConfigScreenKeyBinding
        = KeyBindingHelper.registerKeyBinding(new KeyMapping("binding.sharedwaypoints.sWMenu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, Keybindings.category));
    public static final KeyMapping toggleWaypointsKeyBinding
        = KeyBindingHelper.registerKeyBinding(new KeyMapping("binding.sharedwaypoints.toggleWaypoints", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, Keybindings.category));
    public static final KeyMapping toggleWaypointFullNamesKeyBinding
        = KeyBindingHelper.registerKeyBinding(new KeyMapping("binding.sharedwaypoints.toggleFullNames", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, Keybindings.category));
    public static final KeyMapping toggleHighlightClosestKeyBinding
        = KeyBindingHelper.registerKeyBinding(new KeyMapping("binding.sharedwaypoints.toggleClosestHighlight", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, Keybindings.category));
    public static final KeyMapping toggleCrossDimensionalWaypointsKeyBinding
        = KeyBindingHelper.registerKeyBinding(new KeyMapping("binding.sharedwaypoints.toggleCrossDimensional", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, Keybindings.category));

    public static void handle(Minecraft client)
    {
        boolean needToUpdateConfig = false;

        while (Keybindings.openConfigScreenKeyBinding.consumeClick())
        {
            client.setScreen(ConfigScreen.getConfigScreen(client.screen));
        }

        while (Keybindings.toggleWaypointsKeyBinding.consumeClick())
        {
            SWConfig.INSTANCE.renderWaypoints = !SWConfig.INSTANCE.renderWaypoints;
            needToUpdateConfig = true;

            if (!SWConfig.INSTANCE.renderWaypoints)
            {
                SWStateManager.INSTANCE.clearHoveredWaypoint();
            }
        }

        while (Keybindings.toggleWaypointFullNamesKeyBinding.consumeClick())
        {
            SWConfig.INSTANCE.fullWaypointNames = !SWConfig.INSTANCE.fullWaypointNames;
            needToUpdateConfig = true;
        }

        while (Keybindings.toggleHighlightClosestKeyBinding.consumeClick())
        {
            SWConfig.INSTANCE.highlightClosest = !SWConfig.INSTANCE.highlightClosest;
            needToUpdateConfig = true;
        }

        while (Keybindings.toggleCrossDimensionalWaypointsKeyBinding.consumeClick())
        {
            SWConfig.INSTANCE.crossDimensionalWaypoints = !SWConfig.INSTANCE.crossDimensionalWaypoints;
            needToUpdateConfig = true;
        }

        if (needToUpdateConfig)
        {
            SWConfig.trySave();
        }
    }

}
