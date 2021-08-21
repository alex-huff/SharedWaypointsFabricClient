package dev.phonis.rtsurvival;

import dev.phonis.rtsurvival.networking.*;
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
import dev.phonis.rtsurvival.state.RTStateManager;

public class Keybindings {

    public static void handle(MinecraftClient client) {
        boolean needToUpdateConfig = false;

        while (RTSurvival.openConfigScreenKeyBinding.wasPressed()) {
            Keybindings.openConfig(client);
        }

        while (RTSurvival.toggleWaypointsKeyBinding.wasPressed()) {
            RTConfig.INSTANCE.renderWaypoints = !RTConfig.INSTANCE.renderWaypoints;
            needToUpdateConfig = true;

            if (!RTConfig.INSTANCE.renderWaypoints) {
                RTStateManager.INSTANCE.clearHoveredWaypoint();
            }
        }

        while (RTSurvival.toggleWaypointFullNamesKeyBinding.wasPressed()) {
            RTConfig.INSTANCE.fullWaypointNames = !RTConfig.INSTANCE.fullWaypointNames;
            needToUpdateConfig = true;
        }

        while (RTSurvival.toggleHighlightClosestKeyBinding.wasPressed()) {
            RTConfig.INSTANCE.highlightClosest = !RTConfig.INSTANCE.highlightClosest;
            needToUpdateConfig = true;
        }

        while (RTSurvival.toggleCrossDimensionalWaypointsKeyBinding.wasPressed()) {
            RTConfig.INSTANCE.crossDimensionalWaypoints = !RTConfig.INSTANCE.crossDimensionalWaypoints;
            needToUpdateConfig = true;
        }

        while (RTSurvival.stogKeyBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTSTog());
        }

        while (RTSurvival.ficClearKeyBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTFICClear());
        }

        while (RTSurvival.tetherClearKeyBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTTetherClear());
        }

        while (RTSurvival.ficCurrentHeldItemKeyBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTFIC());
        }

        while (RTSurvival.tetherOnHoveredWaypointKeyBinding.wasPressed()) {
            String hoveredWaypointName = RTStateManager.INSTANCE.getHoveredWaypoint();

            if (hoveredWaypointName == null) {
                continue;
            }

            RTSurvival.sendPacket(new RTTetherOnHoveredWaypoint(hoveredWaypointName));
        }

        while (RTSurvival.sTPToHoveredWaypointKeyBinding.wasPressed()) {
            String hoveredWaypointName = RTStateManager.INSTANCE.getHoveredWaypoint();

            if (hoveredWaypointName == null) {
                continue;
            }

            RTSurvival.sendPacket(new RTSTPToHoveredWaypoint(hoveredWaypointName));
        }

        if (needToUpdateConfig) {
            RTConfig.trySave();
        }
    }

    private static void openConfig(MinecraftClient client) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(client.currentScreen)
            // .setTransparentBackground(true)
            .setTitle(new TranslatableText("title.rtsurvival.config"));

        builder.setSavingRunnable(RTConfig::trySave);

        ConfigCategory category = builder.getOrCreateCategory(new TranslatableText("category.rtsurvival.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        SubCategoryBuilder subCategoryBuilder = entryBuilder.startSubCategory(new TranslatableText("category.rtsurvival.general.keybindings"));

        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.openConfigScreenKeyBinding, "binding.rtsurvival.rtMenu");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.ficCurrentHeldItemKeyBinding, "binding.rtsurvival.ficCurrentItem");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.stogKeyBinding, "binding.rtsurvival.spectog");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.ficClearKeyBinding, "binding.rtsurvival.ficClear");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.tetherClearKeyBinding, "binding.rtsurvival.tetherClear");

        category.addEntry(
            subCategoryBuilder
                .setExpanded(true)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.general.keybindings"))
                .build()
        );

        category = builder.getOrCreateCategory(new TranslatableText("category.rtsurvival.waypoints"));

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.toggleWaypoints"), RTConfig.INSTANCE.renderWaypoints)
                .setDefaultValue(RTConfig.defaultRenderWaypoints)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.toggleWaypoints"))
                .setSaveConsumer(
                    newValue -> RTConfig.INSTANCE.renderWaypoints = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.fullWaypointNames"), RTConfig.INSTANCE.fullWaypointNames)
                .setDefaultValue(RTConfig.defaultFullWaypointNames)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.fullWaypointNames"))
                .setSaveConsumer(
                    newValue -> RTConfig.INSTANCE.fullWaypointNames = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.highlightHoveredWaypoint"), RTConfig.INSTANCE.highlightClosest)
                .setDefaultValue(RTConfig.defaultHighlightClosest)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.highlightHoveredWaypoint"))
                .setSaveConsumer(
                    newValue -> RTConfig.INSTANCE.highlightClosest = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.crossDimensionalWaypoints"), RTConfig.INSTANCE.crossDimensionalWaypoints)
                .setDefaultValue(RTConfig.defaultCrossDimensionalWaypoints)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.crossDimensionalWaypoints"))
                .setSaveConsumer(
                    newValue -> RTConfig.INSTANCE.crossDimensionalWaypoints = newValue
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.renderScale"), RTConfig.INSTANCE.scale, 0, 100)
                .setDefaultValue(RTConfig.defaultRenderScale)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.renderScale"))
                .setSaveConsumer(
                    (value) -> RTConfig.INSTANCE.scale = value
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.rtsurvival.waypointColor"), RTConfig.INSTANCE.plateBackground.toSheDanielColor())
                .setDefaultValue(RTConfig.defaultPlateBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.waypointColor"))
                .setSaveConsumer2(
                    (value) -> RTConfig.INSTANCE.plateBackground.updateRGB(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.waypointTransparency"), RTConfig.INSTANCE.plateBackground.a, 0, 255)
                .setDefaultValue(RTConfig.defaultPlateBackground.a)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.waypointTransparency"))
                .setSaveConsumer(
                    (value) -> RTConfig.INSTANCE.plateBackground.updateA(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.rtsurvival.highlightWaypointColor"), RTConfig.INSTANCE.fullBackground.toSheDanielColor())
                .setDefaultValue(RTConfig.defaultFullBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.highlightWaypointColor"))
                .setSaveConsumer2(
                    (value) -> RTConfig.INSTANCE.fullBackground.updateRGB(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.highlightWaypointTransparency"), RTConfig.INSTANCE.fullBackground.a, 0, 255)
                .setDefaultValue(RTConfig.defaultFullBackground.a)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.highlightWaypointTransparency"))
                .setSaveConsumer(
                    (value) -> RTConfig.INSTANCE.fullBackground.updateA(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.rtsurvival.distanceBackgroundColor"), RTConfig.INSTANCE.distanceBackground.toSheDanielColor())
                .setDefaultValue(RTConfig.defaultDistanceBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.distanceBackgroundColor"))
                .setSaveConsumer2(
                    (value) -> RTConfig.INSTANCE.distanceBackground.updateRGB(value)
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.distanceBackgroundTransparency"), RTConfig.INSTANCE.distanceBackground.a, 0, 255)
                .setDefaultValue(RTConfig.defaultDistanceBackground.a)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.distanceBackgroundTransparency"))
                .setSaveConsumer(
                    (value) -> RTConfig.INSTANCE.distanceBackground.updateA(value)
                )
                .build()
        );

        subCategoryBuilder = entryBuilder.startSubCategory(new TranslatableText("category.rtsurvival.waypoints.keybindings"));

        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.toggleWaypointsKeyBinding, "binding.rtsurvival.toggleWaypoints");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.toggleWaypointFullNamesKeyBinding, "binding.rtsurvival.toggleFullNames");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.toggleHighlightClosestKeyBinding, "binding.rtsurvival.toggleClosestHighlight");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.toggleCrossDimensionalWaypointsKeyBinding, "binding.rtsurvival.toggleCrossDimensional");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.tetherOnHoveredWaypointKeyBinding, "binding.rtsurvival.tetherHoveredWaypoint");
        Keybindings.addKeybindingEntryToSubCategory(subCategoryBuilder, entryBuilder, RTSurvival.sTPToHoveredWaypointKeyBinding, "binding.rtsurvival.stpHoveredWaypoint");

        category.addEntry(
            subCategoryBuilder
                .setExpanded(false)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.waypoints.keybindings"))
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
