package dev.phonis.sharedwaypoints.client.keybindings;

import dev.phonis.sharedwaypoints.client.SharedWaypointsClient;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.gui.ConfigScreen;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public
class Keybindings
{

    private static final String     category                                  = "category.sharedwaypoints.sharedWaypoints";
    public static final  KeyBinding openConfigScreenKeyBinding                = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("binding.sharedwaypoints.sWMenu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, Keybindings.category));
    public static final  KeyBinding toggleWaypointsKeyBinding                 = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("binding.sharedwaypoints.toggleWaypoints", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N,
            Keybindings.category));
    public static final  KeyBinding toggleWaypointFullNamesKeyBinding         = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("binding.sharedwaypoints.toggleFullNames", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
            Keybindings.category));
    public static final  KeyBinding toggleHighlightClosestKeyBinding          = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("binding.sharedwaypoints.toggleClosestHighlight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
            Keybindings.category));
    public static final  KeyBinding toggleCrossDimensionalWaypointsKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("binding.sharedwaypoints.toggleCrossDimensional", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
            Keybindings.category));

    public static
    void handle(MinecraftClient client)
    {
        boolean needToUpdateConfig = false;

        while (Keybindings.openConfigScreenKeyBinding.wasPressed())
        {
            client.setScreen(ConfigScreen.getConfigScreen(client.currentScreen));
        }

        while (Keybindings.toggleWaypointsKeyBinding.wasPressed())
        {
            SWConfig.INSTANCE.renderWaypoints = !SWConfig.INSTANCE.renderWaypoints;
            needToUpdateConfig                = true;

            if (!SWConfig.INSTANCE.renderWaypoints)
            {
                SWStateManager.INSTANCE.clearHoveredWaypoint();
            }
        }

        while (Keybindings.toggleWaypointFullNamesKeyBinding.wasPressed())
        {
            SWConfig.INSTANCE.fullWaypointNames = !SWConfig.INSTANCE.fullWaypointNames;
            needToUpdateConfig                  = true;
        }

        while (Keybindings.toggleHighlightClosestKeyBinding.wasPressed())
        {
            SWConfig.INSTANCE.highlightClosest = !SWConfig.INSTANCE.highlightClosest;
            needToUpdateConfig                 = true;
        }

        while (Keybindings.toggleCrossDimensionalWaypointsKeyBinding.wasPressed())
        {
            SWConfig.INSTANCE.crossDimensionalWaypoints = !SWConfig.INSTANCE.crossDimensionalWaypoints;
            needToUpdateConfig                          = true;
        }

        if (needToUpdateConfig)
        {
            SWConfig.trySave();
        }
    }

}
