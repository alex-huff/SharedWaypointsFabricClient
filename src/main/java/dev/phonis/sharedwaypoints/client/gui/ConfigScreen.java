package dev.phonis.sharedwaypoints.client.gui;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.keybindings.Keybindings;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;

public
class ConfigScreen
{

    private static final TranslatableTextContent configTitle                           = new TranslatableTextContent(
        "title.sharedwaypoints.config", null, null);
    private static final TranslatableTextContent waypointCategoryName                  = new TranslatableTextContent(
        "category.sharedwaypoints.waypoints", null, null);
    private static final TranslatableTextContent toggleWaypointsOption                 = new TranslatableTextContent(
        "option.sharedwaypoints.toggleWaypoints", null, null);
    private static final TranslatableTextContent toggleWaypointsTooltip                = new TranslatableTextContent(
        "tooltip.sharedwaypoints.toggleWaypoints", null, null);
    private static final TranslatableTextContent fullWaypointNamesOption               = new TranslatableTextContent(
        "option.sharedwaypoints.fullWaypointNames", null, null);
    private static final TranslatableTextContent fullWaypointNamesTooltip              = new TranslatableTextContent(
        "tooltip.sharedwaypoints.fullWaypointNames", null, null);
    private static final TranslatableTextContent highlightHoveredWaypointOption        = new TranslatableTextContent(
        "option.sharedwaypoints.highlightHoveredWaypoint", null, null);
    private static final TranslatableTextContent highlightHoveredWaypointTooltip       = new TranslatableTextContent(
        "tooltip.sharedwaypoints.highlightHoveredWaypoint", null, null);
    private static final TranslatableTextContent crossDimensionalWaypointsOption       = new TranslatableTextContent(
        "option.sharedwaypoints.crossDimensionalWaypoints", null, null);
    private static final TranslatableTextContent crossDimensionalWaypointsTooltip      = new TranslatableTextContent(
        "tooltip.sharedwaypoints.crossDimensionalWaypoints", null, null);
    private static final TranslatableTextContent renderScaleOption                     = new TranslatableTextContent(
        "option.sharedwaypoints.renderScale", null, null);
    private static final TranslatableTextContent renderScaleTooltip                    = new TranslatableTextContent(
        "tooltip.sharedwaypoints.renderScale", null, null);
    private static final TranslatableTextContent waypointColorOption                   = new TranslatableTextContent(
        "option.sharedwaypoints.waypointColor", null, null);
    private static final TranslatableTextContent waypointColorTooltip                  = new TranslatableTextContent(
        "tooltip.sharedwaypoints.waypointColor", null, null);
    private static final TranslatableTextContent waypointTransparencyOption            = new TranslatableTextContent(
        "option.sharedwaypoints.waypointTransparency", null, null);
    private static final TranslatableTextContent waypointTransparencyTooltip           = new TranslatableTextContent(
        "tooltip.sharedwaypoints.waypointTransparency", null, null);
    private static final TranslatableTextContent highlightWaypointColorOption          = new TranslatableTextContent(
        "option.sharedwaypoints.highlightWaypointColor", null, null);
    private static final TranslatableTextContent highlightWaypointColorTooltip         = new TranslatableTextContent(
        "tooltip.sharedwaypoints.highlightWaypointColor", null, null);
    private static final TranslatableTextContent highlightWaypointTransparencyOption   = new TranslatableTextContent(
        "option.sharedwaypoints.highlightWaypointTransparency", null, null);
    private static final TranslatableTextContent highlightWaypointTransparencyTooltip  = new TranslatableTextContent(
        "tooltip.sharedwaypoints.highlightWaypointTransparency", null, null);
    private static final TranslatableTextContent distanceBackgroundColorOption         = new TranslatableTextContent(
        "option.sharedwaypoints.distanceBackgroundColor", null, null);
    private static final TranslatableTextContent distanceBackgroundColorTooltip        = new TranslatableTextContent(
        "tooltip.sharedwaypoints.distanceBackgroundColor", null, null);
    private static final TranslatableTextContent distanceBackgroundTransparencyOption  = new TranslatableTextContent(
        "option.sharedwaypoints.distanceBackgroundTransparency", null, null);
    private static final TranslatableTextContent distanceBackgroundTransparencyTooltip = new TranslatableTextContent(
        "tooltip.sharedwaypoints.distanceBackgroundTransparency", null, null);
    private static final TranslatableTextContent textColorOption                       = new TranslatableTextContent(
        "option.sharedwaypoints.textColor", null, null);
    private static final TranslatableTextContent textColorTooltip                      = new TranslatableTextContent(
        "tooltip.sharedwaypoints.textColor", null, null);
    private static final TranslatableTextContent keybindingsCategoryName               = new TranslatableTextContent(
        "category.sharedwaypoints.keybindings", null, null);
    private static final TranslatableTextContent sWMenuBindingName                     = new TranslatableTextContent(
        "binding.sharedwaypoints.sWMenu", null, null);
    private static final TranslatableTextContent toggleWaypointsBindingName            = new TranslatableTextContent(
        "binding.sharedwaypoints.toggleWaypoints", null, null);
    private static final TranslatableTextContent toggleFullNamesBindingName            = new TranslatableTextContent(
        "binding.sharedwaypoints.toggleFullNames", null, null);
    private static final TranslatableTextContent toggleHighlightClosestBindingName     = new TranslatableTextContent(
        "binding.sharedwaypoints.toggleClosestHighlight", null, null);
    private static final TranslatableTextContent toggleCrossDimensionalBindingName     = new TranslatableTextContent(
        "binding.sharedwaypoints.toggleCrossDimensional", null, null);

    public static
    Screen getConfigScreen(Screen parent)
    {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTransparentBackground(true)
            .setTitle(MutableText.of(ConfigScreen.configTitle));
        builder.setSavingRunnable(SWConfig::trySave);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(MutableText.of(ConfigScreen.waypointCategoryName));

        category.addEntry(entryBuilder.startBooleanToggle(MutableText.of(ConfigScreen.toggleWaypointsOption),
                SWConfig.INSTANCE.renderWaypoints).setDefaultValue(SWConfig.defaultRenderWaypoints)
            .setTooltip(MutableText.of(ConfigScreen.toggleWaypointsTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.renderWaypoints = newValue).build());

        category.addEntry(entryBuilder.startBooleanToggle(MutableText.of(ConfigScreen.fullWaypointNamesOption),
                SWConfig.INSTANCE.fullWaypointNames).setDefaultValue(SWConfig.defaultFullWaypointNames)
            .setTooltip(MutableText.of(ConfigScreen.fullWaypointNamesTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.fullWaypointNames = newValue).build());

        category.addEntry(entryBuilder.startBooleanToggle(MutableText.of(ConfigScreen.highlightHoveredWaypointOption),
                SWConfig.INSTANCE.highlightClosest).setDefaultValue(SWConfig.defaultHighlightClosest)
            .setTooltip(MutableText.of(ConfigScreen.highlightHoveredWaypointTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.highlightClosest = newValue).build());

        category.addEntry(entryBuilder.startBooleanToggle(MutableText.of(ConfigScreen.crossDimensionalWaypointsOption),
                SWConfig.INSTANCE.crossDimensionalWaypoints).setDefaultValue(SWConfig.defaultCrossDimensionalWaypoints)
            .setTooltip(MutableText.of(ConfigScreen.crossDimensionalWaypointsTooltip))
            .setSaveConsumer(newValue -> SWConfig.INSTANCE.crossDimensionalWaypoints = newValue).build());

        category.addEntry(
            entryBuilder.startIntSlider(MutableText.of(ConfigScreen.renderScaleOption), SWConfig.INSTANCE.renderScale,
                    0, 100).setDefaultValue(SWConfig.defaultRenderScale)
                .setTooltip(MutableText.of(ConfigScreen.renderScaleTooltip))
                .setSaveConsumer((value) -> SWConfig.INSTANCE.renderScale = value).build());

        category.addEntry(entryBuilder.startColorField(MutableText.of(ConfigScreen.waypointColorOption),
                SWConfig.INSTANCE.plateBackground.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultPlateBackground.toInt() & 0x00FFFFFF)
            .setTooltip(MutableText.of(ConfigScreen.waypointColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.plateBackground::updateRGB).build());

        category.addEntry(entryBuilder.startIntSlider(MutableText.of(ConfigScreen.waypointTransparencyOption),
                SWConfig.INSTANCE.plateBackground.a, 0, 255).setDefaultValue(SWConfig.defaultPlateBackground.a)
            .setTooltip(MutableText.of(ConfigScreen.waypointTransparencyTooltip))
            .setSaveConsumer(SWConfig.INSTANCE.plateBackground::updateA).build());

        category.addEntry(entryBuilder.startColorField(MutableText.of(ConfigScreen.highlightWaypointColorOption),
                SWConfig.INSTANCE.fullBackground.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultFullBackground.toInt() & 0x00FFFFFF)
            .setTooltip(MutableText.of(ConfigScreen.highlightWaypointColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.fullBackground::updateRGB).build());

        category.addEntry(entryBuilder.startIntSlider(MutableText.of(ConfigScreen.highlightWaypointTransparencyOption),
                SWConfig.INSTANCE.fullBackground.a, 0, 255).setDefaultValue(SWConfig.defaultFullBackground.a)
            .setTooltip(MutableText.of(ConfigScreen.highlightWaypointTransparencyTooltip))
            .setSaveConsumer(SWConfig.INSTANCE.fullBackground::updateA).build());

        category.addEntry(entryBuilder.startColorField(MutableText.of(ConfigScreen.distanceBackgroundColorOption),
                SWConfig.INSTANCE.distanceBackground.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultDistanceBackground.toInt() & 0x00FFFFFF)
            .setTooltip(MutableText.of(ConfigScreen.distanceBackgroundColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.distanceBackground::updateRGB).build());

        category.addEntry(entryBuilder.startIntSlider(MutableText.of(ConfigScreen.distanceBackgroundTransparencyOption),
                SWConfig.INSTANCE.distanceBackground.a, 0, 255).setDefaultValue(SWConfig.defaultDistanceBackground.a)
            .setTooltip(MutableText.of(ConfigScreen.distanceBackgroundTransparencyTooltip))
            .setSaveConsumer(SWConfig.INSTANCE.distanceBackground::updateA).build());

        category.addEntry(entryBuilder.startColorField(MutableText.of(ConfigScreen.textColorOption),
                SWConfig.INSTANCE.textColor.toSheDanielColor())
            .setDefaultValue(SWConfig.defaultTextColor.toInt() & 0x00FFFFFF)
            .setTooltip(MutableText.of(ConfigScreen.textColorTooltip))
            .setSaveConsumer2(SWConfig.INSTANCE.textColor::updateRGB).build());

        ConfigCategory keybindingsCategory = builder.getOrCreateCategory(
            MutableText.of(ConfigScreen.keybindingsCategoryName));

        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder,
            Keybindings.openConfigScreenKeyBinding, ConfigScreen.sWMenuBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder,
            Keybindings.toggleWaypointsKeyBinding, ConfigScreen.toggleWaypointsBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder,
            Keybindings.toggleWaypointFullNamesKeyBinding, toggleFullNamesBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder,
            Keybindings.toggleHighlightClosestKeyBinding, ConfigScreen.toggleHighlightClosestBindingName);
        ConfigScreen.addKeybindingEntryToCategory(keybindingsCategory, entryBuilder,
            Keybindings.toggleCrossDimensionalWaypointsKeyBinding,
            ConfigScreen.toggleCrossDimensionalBindingName);

        return builder.build();
    }

    private static
    void addKeybindingEntryToSubCategory(SubCategoryBuilder subCategoryBuilder, ConfigEntryBuilder entryBuilder,
                                         KeyBinding keyBinding, TranslatableTextContent translationKey)
    {
        subCategoryBuilder.add(ConfigScreen.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static
    void addKeybindingEntryToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding,
                                      TranslatableTextContent translationKey)
    {
        category.addEntry(ConfigScreen.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static
    KeyCodeEntry getKeybindingOption(ConfigEntryBuilder entryBuilder, KeyBinding keyBinding,
                                     TranslatableTextContent translationKey)
    {
        return entryBuilder.startKeyCodeField(MutableText.of(translationKey),
                KeyBindingHelper.getBoundKeyOf(keyBinding)).setDefaultValue(keyBinding.getDefaultKey())
            .setKeySaveConsumer((code) ->
            {
                keyBinding.setBoundKey(code);
                KeyBinding.updateKeysByCode();
                MinecraftClient.getInstance().options.write();
            }).build();
    }

}
