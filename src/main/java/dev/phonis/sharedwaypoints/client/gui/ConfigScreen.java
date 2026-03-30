package dev.phonis.sharedwaypoints.client.gui;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.keybindings.Keybindings;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ConfigScreen
{

    private static final TranslatableContents configTitle
        = new TranslatableContents("title.sharedwaypoints.config", null, null);
    private static final TranslatableContents waypointCategoryName
        = new TranslatableContents("category.sharedwaypoints.waypoints", null, null);
    private static final TranslatableContents toggleWaypointsOption
        = new TranslatableContents("option.sharedwaypoints.toggleWaypoints", null, null);
    private static final TranslatableContents toggleWaypointsTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.toggleWaypoints", null, null);
    private static final TranslatableContents fullWaypointNamesOption
        = new TranslatableContents("option.sharedwaypoints.fullWaypointNames", null, null);
    private static final TranslatableContents fullWaypointNamesTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.fullWaypointNames", null, null);
    private static final TranslatableContents highlightHoveredWaypointOption
        = new TranslatableContents("option.sharedwaypoints.highlightHoveredWaypoint", null, null);
    private static final TranslatableContents highlightHoveredWaypointTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.highlightHoveredWaypoint", null, null);
    private static final TranslatableContents crossDimensionalWaypointsOption
        = new TranslatableContents("option.sharedwaypoints.crossDimensionalWaypoints", null, null);
    private static final TranslatableContents crossDimensionalWaypointsTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.crossDimensionalWaypoints", null, null);
    private static final TranslatableContents renderScaleOption
        = new TranslatableContents("option.sharedwaypoints.renderScale", null, null);
    private static final TranslatableContents renderScaleTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.renderScale", null, null);
    private static final TranslatableContents waypointColorOption
        = new TranslatableContents("option.sharedwaypoints.waypointColor", null, null);
    private static final TranslatableContents waypointColorTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.waypointColor", null, null);
    private static final TranslatableContents waypointTransparencyOption
        = new TranslatableContents("option.sharedwaypoints.waypointTransparency", null, null);
    private static final TranslatableContents waypointTransparencyTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.waypointTransparency", null, null);
    private static final TranslatableContents highlightWaypointColorOption
        = new TranslatableContents("option.sharedwaypoints.highlightWaypointColor", null, null);
    private static final TranslatableContents highlightWaypointColorTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.highlightWaypointColor", null, null);
    private static final TranslatableContents highlightWaypointTransparencyOption
        = new TranslatableContents("option.sharedwaypoints.highlightWaypointTransparency", null, null);
    private static final TranslatableContents highlightWaypointTransparencyTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.highlightWaypointTransparency", null, null);
    private static final TranslatableContents distanceBackgroundColorOption
        = new TranslatableContents("option.sharedwaypoints.distanceBackgroundColor", null, null);
    private static final TranslatableContents distanceBackgroundColorTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.distanceBackgroundColor", null, null);
    private static final TranslatableContents distanceBackgroundTransparencyOption
        = new TranslatableContents("option.sharedwaypoints.distanceBackgroundTransparency", null, null);
    private static final TranslatableContents distanceBackgroundTransparencyTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.distanceBackgroundTransparency", null, null);
    private static final TranslatableContents textColorOption
        = new TranslatableContents("option.sharedwaypoints.textColor", null, null);
    private static final TranslatableContents textColorTooltip
        = new TranslatableContents("tooltip.sharedwaypoints.textColor", null, null);
    private static final TranslatableContents keybindingsCategoryName
        = new TranslatableContents("category.sharedwaypoints.keybindings", null, null);
    private static final TranslatableContents sWMenuBindingName
        = new TranslatableContents("binding.sharedwaypoints.sWMenu", null, null);
    private static final TranslatableContents toggleWaypointsBindingName
        = new TranslatableContents("binding.sharedwaypoints.toggleWaypoints", null, null);
    private static final TranslatableContents toggleFullNamesBindingName
        = new TranslatableContents("binding.sharedwaypoints.toggleFullNames", null, null);
    private static final TranslatableContents toggleHighlightClosestBindingName
        = new TranslatableContents("binding.sharedwaypoints.toggleClosestHighlight", null, null);
    private static final TranslatableContents toggleCrossDimensionalBindingName
        = new TranslatableContents("binding.sharedwaypoints.toggleCrossDimensional", null, null);

    public static Screen getConfigScreen(Screen parent)
    {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTransparentBackground(true)
            .setTitle(MutableComponent.create(ConfigScreen.configTitle));
        builder.setSavingRunnable(SWConfig::trySave);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(MutableComponent.create(ConfigScreen.waypointCategoryName));

        category.addEntry(entryBuilder.startBooleanToggle(MutableComponent.create(ConfigScreen.toggleWaypointsOption), SWConfig.INSTANCE.renderWaypoints)
            .setDefaultValue(SWConfig.defaultRenderWaypoints)
            .setTooltip(MutableComponent.create(ConfigScreen.toggleWaypointsTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.renderWaypoints = newValue).build());

        category.addEntry(entryBuilder.startBooleanToggle(MutableComponent.create(ConfigScreen.fullWaypointNamesOption), SWConfig.INSTANCE.fullWaypointNames)
            .setDefaultValue(SWConfig.defaultFullWaypointNames)
            .setTooltip(MutableComponent.create(ConfigScreen.fullWaypointNamesTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.fullWaypointNames = newValue).build());

        category.addEntry(entryBuilder.startBooleanToggle(MutableComponent.create(ConfigScreen.highlightHoveredWaypointOption), SWConfig.INSTANCE.highlightClosest)
            .setDefaultValue(SWConfig.defaultHighlightClosest)
            .setTooltip(MutableComponent.create(ConfigScreen.highlightHoveredWaypointTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.highlightClosest = newValue).build());

        category.addEntry(entryBuilder.startBooleanToggle(MutableComponent.create(ConfigScreen.crossDimensionalWaypointsOption), SWConfig.INSTANCE.crossDimensionalWaypoints)
            .setDefaultValue(SWConfig.defaultCrossDimensionalWaypoints)
            .setTooltip(MutableComponent.create(ConfigScreen.crossDimensionalWaypointsTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.crossDimensionalWaypoints = newValue).build());

        category.addEntry(entryBuilder.startIntSlider(MutableComponent.create(ConfigScreen.renderScaleOption), SWConfig.INSTANCE.renderScale, 0, 100)
            .setDefaultValue(SWConfig.defaultRenderScale).setTooltip(MutableComponent.create(ConfigScreen.renderScaleTooltip))
            .setSaveConsumer((value) -> SWConfig.INSTANCE.renderScale = value).build());

        category.addEntry(entryBuilder.startColorField(MutableComponent.create(ConfigScreen.waypointColorOption), SWConfig.INSTANCE.plateBackground.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultPlateBackground.toInt() & 0x00FFFFFF)
            .setTooltip(MutableComponent.create(ConfigScreen.waypointColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.plateBackground::updateRGB).build());

        category.addEntry(entryBuilder.startIntSlider(MutableComponent.create(ConfigScreen.waypointTransparencyOption), SWConfig.INSTANCE.plateBackground.a, 0, 255)
            .setDefaultValue(SWConfig.defaultPlateBackground.a)
            .setTooltip(MutableComponent.create(ConfigScreen.waypointTransparencyTooltip))
            .setSaveConsumer(SWConfig.INSTANCE.plateBackground::updateA).build());

        category.addEntry(entryBuilder.startColorField(MutableComponent.create(ConfigScreen.highlightWaypointColorOption), SWConfig.INSTANCE.fullBackground.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultFullBackground.toInt() & 0x00FFFFFF)
            .setTooltip(MutableComponent.create(ConfigScreen.highlightWaypointColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.fullBackground::updateRGB).build());

        category.addEntry(entryBuilder.startIntSlider(MutableComponent.create(ConfigScreen.highlightWaypointTransparencyOption), SWConfig.INSTANCE.fullBackground.a, 0, 255)
            .setDefaultValue(SWConfig.defaultFullBackground.a)
            .setTooltip(MutableComponent.create(ConfigScreen.highlightWaypointTransparencyTooltip))
            .setSaveConsumer(SWConfig.INSTANCE.fullBackground::updateA).build());

        category.addEntry(entryBuilder.startColorField(MutableComponent.create(ConfigScreen.distanceBackgroundColorOption), SWConfig.INSTANCE.distanceBackground.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultDistanceBackground.toInt() & 0x00FFFFFF)
            .setTooltip(MutableComponent.create(ConfigScreen.distanceBackgroundColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.distanceBackground::updateRGB).build());

        category.addEntry(entryBuilder.startIntSlider(MutableComponent.create(ConfigScreen.distanceBackgroundTransparencyOption), SWConfig.INSTANCE.distanceBackground.a, 0, 255)
            .setDefaultValue(SWConfig.defaultDistanceBackground.a)
            .setTooltip(MutableComponent.create(ConfigScreen.distanceBackgroundTransparencyTooltip))
            .setSaveConsumer(SWConfig.INSTANCE.distanceBackground::updateA).build());

        category.addEntry(entryBuilder.startColorField(MutableComponent.create(ConfigScreen.textColorOption), SWConfig.INSTANCE.textColor.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultTextColor.toInt() & 0x00FFFFFF)
            .setTooltip(MutableComponent.create(ConfigScreen.textColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.textColor::updateRGB).build());

        ConfigCategory keybindingsCategory
            = builder.getOrCreateCategory(MutableComponent.create(ConfigScreen.keybindingsCategoryName));

        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder, Keybindings.openConfigScreenKeyBinding, ConfigScreen.sWMenuBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder, Keybindings.toggleWaypointsKeyBinding, ConfigScreen.toggleWaypointsBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder, Keybindings.toggleWaypointFullNamesKeyBinding, toggleFullNamesBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder, Keybindings.toggleHighlightClosestKeyBinding, ConfigScreen.toggleHighlightClosestBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder, Keybindings.toggleCrossDimensionalWaypointsKeyBinding, ConfigScreen.toggleCrossDimensionalBindingName);

        return builder.build();
    }

    private static void addKeybindingEntryToSubCategory(SubCategoryBuilder subCategoryBuilder,
                                                        ConfigEntryBuilder entryBuilder, KeyMapping keyBinding,
                                                        TranslatableContents translationKey)
    {
        subCategoryBuilder.add(ConfigScreen.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static void addKeybindingEntryToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder,
                                                     KeyMapping keyBinding, TranslatableContents translationKey)
    {
        category.addEntry(ConfigScreen.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static KeyCodeEntry getKeybindingOption(ConfigEntryBuilder entryBuilder, KeyMapping keyBinding,
                                                    TranslatableContents translationKey)
    {
        return entryBuilder.startKeyCodeField(MutableComponent.create(translationKey), KeyBindingHelper.getBoundKeyOf(keyBinding))
            .setDefaultValue(keyBinding.getDefaultKey()).setKeySaveConsumer((code) ->
            {
                keyBinding.setKey(code);
                KeyMapping.resetMapping();
                Minecraft.getInstance().options.save();
            }).build();
    }

}
