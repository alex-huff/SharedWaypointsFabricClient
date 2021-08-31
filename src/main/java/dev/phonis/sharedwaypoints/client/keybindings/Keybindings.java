package dev.phonis.sharedwaypoints.client.keybindings;

import dev.phonis.sharedwaypoints.client.SharedWaypointsClient;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.gui.ConfigScreen;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.minecraft.client.MinecraftClient;

public class Keybindings {

    public static void handle(MinecraftClient client) {
        boolean needToUpdateConfig = false;

        while (SharedWaypointsClient.openConfigScreenKeyBinding.wasPressed()) {
            client.setScreen(ConfigScreen.getConfigScreen(client.currentScreen));
        }

        while (SharedWaypointsClient.toggleWaypointsKeyBinding.wasPressed()) {
            SWConfig.INSTANCE.renderWaypoints = !SWConfig.INSTANCE.renderWaypoints;
            needToUpdateConfig = true;

            if (!SWConfig.INSTANCE.renderWaypoints) {
                SWStateManager.INSTANCE.clearHoveredWaypoint();
            }
        }

        while (SharedWaypointsClient.toggleWaypointFullNamesKeyBinding.wasPressed()) {
            SWConfig.INSTANCE.fullWaypointNames = !SWConfig.INSTANCE.fullWaypointNames;
            needToUpdateConfig = true;
        }

        while (SharedWaypointsClient.toggleHighlightClosestKeyBinding.wasPressed()) {
            SWConfig.INSTANCE.highlightClosest = !SWConfig.INSTANCE.highlightClosest;
            needToUpdateConfig = true;
        }

        while (SharedWaypointsClient.toggleCrossDimensionalWaypointsKeyBinding.wasPressed()) {
            SWConfig.INSTANCE.crossDimensionalWaypoints = !SWConfig.INSTANCE.crossDimensionalWaypoints;
            needToUpdateConfig = true;
        }

        if (needToUpdateConfig) {
            SWConfig.trySave();
        }
    }

}
