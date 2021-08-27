package dev.phonis.sharedwaypoints.client.keybindings;

import dev.phonis.sharedwaypoints.client.SharedWaypointsClient;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.TranslatableText;

public class Keybindings {

    public static void handle(MinecraftClient client) {
        boolean needToUpdateConfig = false;

        while (SharedWaypointsClient.openConfigScreenKeyBinding.wasPressed()) {
            Keybindings.openConfig(client);
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

    private static void openConfig(MinecraftClient client) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(client.currentScreen)
            // .setTransparentBackground(true)
            .setTitle(new TranslatableText("title.sharedwaypoints.config"));

        builder.setSavingRunnable(SWConfig::trySave);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(new TranslatableText("category.sharedwaypoints.waypoints"));

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.sharedwaypoints.toggleWaypoints"), SWConfig.INSTANCE.renderWaypoints)
                .setDefaultValue(SWConfig.defaultRenderWaypoints)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.toggleWaypoints"))
                .setSaveConsumer(
                    newValue -> SWConfig.INSTANCE.renderWaypoints = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.sharedwaypoints.fullWaypointNames"), SWConfig.INSTANCE.fullWaypointNames)
                .setDefaultValue(SWConfig.defaultFullWaypointNames)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.fullWaypointNames"))
                .setSaveConsumer(
                    newValue -> SWConfig.INSTANCE.fullWaypointNames = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.sharedwaypoints.highlightHoveredWaypoint"), SWConfig.INSTANCE.highlightClosest)
                .setDefaultValue(SWConfig.defaultHighlightClosest)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.highlightHoveredWaypoint"))
                .setSaveConsumer(
                    newValue -> SWConfig.INSTANCE.highlightClosest = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.sharedwaypoints.crossDimensionalWaypoints"), SWConfig.INSTANCE.crossDimensionalWaypoints)
                .setDefaultValue(SWConfig.defaultCrossDimensionalWaypoints)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.crossDimensionalWaypoints"))
                .setSaveConsumer(
                    newValue -> SWConfig.INSTANCE.crossDimensionalWaypoints = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.sharedwaypoints.renderScale"), SWConfig.INSTANCE.scale, 0, 100)
                .setDefaultValue(SWConfig.defaultRenderScale)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.renderScale"))
                .setSaveConsumer(
                    (value) -> SWConfig.INSTANCE.scale = value
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.sharedwaypoints.waypointColor"), SWConfig.INSTANCE.plateBackground.toSheDanielColor())
                .setDefaultValue(SWConfig.defaultPlateBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.waypointColor"))
                .setSaveConsumer2(
                    (value) -> SWConfig.INSTANCE.plateBackground.updateRGB(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.sharedwaypoints.waypointTransparency"), SWConfig.INSTANCE.plateBackground.a, 0, 255)
                .setDefaultValue(SWConfig.defaultPlateBackground.a)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.waypointTransparency"))
                .setSaveConsumer(
                    (value) -> SWConfig.INSTANCE.plateBackground.updateA(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.sharedwaypoints.highlightWaypointColor"), SWConfig.INSTANCE.fullBackground.toSheDanielColor())
                .setDefaultValue(SWConfig.defaultFullBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.highlightWaypointColor"))
                .setSaveConsumer2(
                    (value) -> SWConfig.INSTANCE.fullBackground.updateRGB(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.sharedwaypoints.highlightWaypointTransparency"), SWConfig.INSTANCE.fullBackground.a, 0, 255)
                .setDefaultValue(SWConfig.defaultFullBackground.a)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.highlightWaypointTransparency"))
                .setSaveConsumer(
                    (value) -> SWConfig.INSTANCE.fullBackground.updateA(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.sharedwaypoints.distanceBackgroundColor"), SWConfig.INSTANCE.distanceBackground.toSheDanielColor())
                .setDefaultValue(SWConfig.defaultDistanceBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.distanceBackgroundColor"))
                .setSaveConsumer2(
                    (value) -> SWConfig.INSTANCE.distanceBackground.updateRGB(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.sharedwaypoints.distanceBackgroundTransparency"), SWConfig.INSTANCE.distanceBackground.a, 0, 255)
                .setDefaultValue(SWConfig.defaultDistanceBackground.a)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.distanceBackgroundTransparency"))
                .setSaveConsumer(
                    (value) -> SWConfig.INSTANCE.distanceBackground.updateA(value)
                )
                .build()
        );

        SubCategoryBuilder subCategoryBuilder = entryBuilder.startSubCategory(new TranslatableText("category.sharedwaypoints.waypoints.keybindings"));

        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, SharedWaypointsClient.openConfigScreenKeyBinding, "binding.sharedwaypoints.sWMenu");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleWaypointsKeyBinding, "binding.sharedwaypoints.toggleWaypoints");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleWaypointFullNamesKeyBinding, "binding.sharedwaypoints.toggleFullNames");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleHighlightClosestKeyBinding, "binding.sharedwaypoints.toggleClosestHighlight");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleCrossDimensionalWaypointsKeyBinding, "binding.sharedwaypoints.toggleCrossDimensional");

        category.addEntry(
            subCategoryBuilder
                .setExpanded(false)
                .setTooltip(new TranslatableText("tooltip.sharedwaypoints.waypoints.keybindings"))
                .build()
        );

        Screen screen = builder.build();

        client.setScreen(screen);
    }

    private static KeyCodeEntry getKeybindingOption(ConfigEntryBuilder entryBuilder, KeyBinding keyBinding, String translationKey) {
        return entryBuilder.startKeyCodeField(new TranslatableText(translationKey), KeyBindingHelper.getBoundKeyOf(keyBinding))
            .setDefaultValue(keyBinding.getDefaultKey())
            .setSaveConsumer(
                (code) -> {
                    keyBinding.setBoundKey(code);
                    KeyBinding.updateKeysByCode();
                    MinecraftClient.getInstance().options.write();
                }
            )
            .build();
    }

    private static void addKeybindingEntryToSubCategory(SubCategoryBuilder subCategoryBuilder, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding, String translationKey) {
        subCategoryBuilder.add(Keybindings.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static void addKeybindingEntryToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding, String translationKey) {
        category.addEntry(Keybindings.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

}
