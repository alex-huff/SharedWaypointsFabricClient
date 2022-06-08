package dev.phonis.sharedwaypoints.client.gui;

import dev.phonis.sharedwaypoints.client.SharedWaypointsClient;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
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

public class ConfigScreen
{

    private static final TranslatableTextContent configTitle                           = new TranslatableTextContent(
        "title.sharedwaypoints" +
        ".config");
    private static final TranslatableTextContent waypointCategoryName                  = new TranslatableTextContent(
        "category" +
        ".sharedwaypoints.waypoints");
    private static final TranslatableTextContent toggleWaypointsOption                 = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".toggleWaypoints");
    private static final TranslatableTextContent toggleWaypointsTooltip                = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.toggleWaypoints");
    private static final TranslatableTextContent fullWaypointNamesOption               = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".fullWaypointNames");
    private static final TranslatableTextContent fullWaypointNamesTooltip              = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.fullWaypointNames");
    private static final TranslatableTextContent highlightHoveredWaypointOption        = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".highlightHoveredWaypoint");
    private static final TranslatableTextContent highlightHoveredWaypointTooltip       = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.highlightHoveredWaypoint");
    private static final TranslatableTextContent crossDimensionalWaypointsOption       = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".crossDimensionalWaypoints");
    private static final TranslatableTextContent crossDimensionalWaypointsTooltip      = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.crossDimensionalWaypoints");
    private static final TranslatableTextContent renderScaleOption                     = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".renderScale");
    private static final TranslatableTextContent renderScaleTooltip                    = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.renderScale");
    private static final TranslatableTextContent waypointColorOption                   = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".waypointColor");
    private static final TranslatableTextContent waypointColorTooltip                  = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.waypointColor");
    private static final TranslatableTextContent waypointTransparencyOption            = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".waypointTransparency");
    private static final TranslatableTextContent waypointTransparencyTooltip           = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.waypointTransparency");
    private static final TranslatableTextContent highlightWaypointColorOption          = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".highlightWaypointColor");
    private static final TranslatableTextContent highlightWaypointColorTooltip         = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.highlightWaypointColor");
    private static final TranslatableTextContent highlightWaypointTransparencyOption   = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".highlightWaypointTransparency");
    private static final TranslatableTextContent highlightWaypointTransparencyTooltip  = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.highlightWaypointTransparency");
    private static final TranslatableTextContent distanceBackgroundColorOption         = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".distanceBackgroundColor");
    private static final TranslatableTextContent distanceBackgroundColorTooltip        = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.distanceBackgroundColor");
    private static final TranslatableTextContent distanceBackgroundTransparencyOption  = new TranslatableTextContent(
        "option" +
        ".sharedwaypoints" +
        ".distanceBackgroundTransparency");
    private static final TranslatableTextContent distanceBackgroundTransparencyTooltip = new TranslatableTextContent(
        "tooltip" +
        ".sharedwaypoints.distanceBackgroundTransparency");
    private static final TranslatableTextContent waypointsKeybindingsCategoryName      =
        new TranslatableTextContent("category.sharedwaypoints.waypoints.keybindings");
    private static final TranslatableTextContent sWMenuBindingName                     = new TranslatableTextContent(
        "binding.sharedwaypoints.sWMenu");
    private static final TranslatableTextContent toggleWaypointsBindingName            =
        new TranslatableTextContent("binding.sharedwaypoints.toggleWaypoints");
    private static final TranslatableTextContent toggleFullNamesBindingName            =
        new TranslatableTextContent(
            "binding.sharedwaypoints.toggleFullNames");
    private static final TranslatableTextContent toggleHighlightClosestBindingName     =
        new TranslatableTextContent(
            "binding.sharedwaypoints.toggleClosestHighlight");
    private static final TranslatableTextContent toggleCrossDimensionalBindingName     =
        new TranslatableTextContent(
            "binding.sharedwaypoints.toggleCrossDimensional");
    private static final TranslatableTextContent waypointKeybindingsCategoryTooltip    = new TranslatableTextContent(
        "tooltip.sharedwaypoints.waypoints.keybindings");

    public static Screen getConfigScreen(Screen parent)
    {
        ConfigBuilder builder = ConfigBuilder.create()
                                             .setParentScreen(parent)
                                             // .setTransparentBackground(true)
                                             .setTitle(MutableText.of(ConfigScreen.configTitle));

        builder.setSavingRunnable(SWConfig::trySave);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(
            MutableText.of(ConfigScreen.waypointCategoryName));

        category.addEntry(
            entryBuilder.startBooleanToggle(
                            MutableText.of(ConfigScreen.toggleWaypointsOption),
                            SWConfig.INSTANCE.renderWaypoints
                        )
                        .setDefaultValue(SWConfig.defaultRenderWaypoints)
                        .setTooltip(MutableText.of(ConfigScreen.toggleWaypointsTooltip))
                        .setSaveConsumer(
                            newValue -> SWConfig.INSTANCE.renderWaypoints = newValue
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(
                            MutableText.of(ConfigScreen.fullWaypointNamesOption),
                            SWConfig.INSTANCE.fullWaypointNames
                        )
                        .setDefaultValue(SWConfig.defaultFullWaypointNames)
                        .setTooltip(MutableText.of(ConfigScreen.fullWaypointNamesTooltip))
                        .setSaveConsumer(
                            newValue -> SWConfig.INSTANCE.fullWaypointNames = newValue
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(
                            MutableText.of(ConfigScreen.highlightHoveredWaypointOption),
                            SWConfig.INSTANCE.highlightClosest
                        )
                        .setDefaultValue(SWConfig.defaultHighlightClosest)
                        .setTooltip(MutableText.of(ConfigScreen.highlightHoveredWaypointTooltip))
                        .setSaveConsumer(
                            newValue -> SWConfig.INSTANCE.highlightClosest = newValue
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(
                            MutableText.of(ConfigScreen.crossDimensionalWaypointsOption),
                            SWConfig.INSTANCE.crossDimensionalWaypoints
                        )
                        .setDefaultValue(SWConfig.defaultCrossDimensionalWaypoints)
                        .setTooltip(MutableText.of(ConfigScreen.crossDimensionalWaypointsTooltip))
                        .setSaveConsumer(
                            newValue -> SWConfig.INSTANCE.crossDimensionalWaypoints = newValue
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder
                .startIntSlider(MutableText.of(ConfigScreen.renderScaleOption), SWConfig.INSTANCE.scale, 0,
                                100
                )
                .setDefaultValue(SWConfig.defaultRenderScale)
                .setTooltip(MutableText.of(ConfigScreen.renderScaleTooltip))
                .setSaveConsumer(
                    (value) -> SWConfig.INSTANCE.scale = value
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(
                            MutableText.of(ConfigScreen.waypointColorOption),
                            SWConfig.INSTANCE.plateBackground.toSheDanielColor()
                        )
                        .setDefaultValue(SWConfig.defaultPlateBackground.toInt() & 0x00FFFFFF)
                        .setTooltip(MutableText.of(ConfigScreen.waypointColorTooltip))
                        .setSaveConsumer2(
                            (value) -> SWConfig.INSTANCE.plateBackground.updateRGB(value)
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(MutableText.of(ConfigScreen.waypointTransparencyOption),
                                        SWConfig.INSTANCE.plateBackground.a, 0, 255
                        )
                        .setDefaultValue(SWConfig.defaultPlateBackground.a)
                        .setTooltip(MutableText.of(ConfigScreen.waypointTransparencyTooltip))
                        .setSaveConsumer(
                            (value) -> SWConfig.INSTANCE.plateBackground.updateA(value)
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startColorField(
                            MutableText.of(ConfigScreen.highlightWaypointColorOption),
                            SWConfig.INSTANCE.fullBackground.toSheDanielColor()
                        )
                        .setDefaultValue(SWConfig.defaultFullBackground.toInt() & 0x00FFFFFF)
                        .setTooltip(MutableText.of(ConfigScreen.highlightWaypointColorTooltip))
                        .setSaveConsumer2(
                            (value) -> SWConfig.INSTANCE.fullBackground.updateRGB(value)
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(MutableText.of(ConfigScreen.highlightWaypointTransparencyOption),
                                        SWConfig.INSTANCE.fullBackground.a, 0, 255
                        )
                        .setDefaultValue(SWConfig.defaultFullBackground.a)
                        .setTooltip(MutableText.of(ConfigScreen.highlightWaypointTransparencyTooltip))
                        .setSaveConsumer(
                            (value) -> SWConfig.INSTANCE.fullBackground.updateA(value)
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startColorField(
                            MutableText.of(ConfigScreen.distanceBackgroundColorOption),
                            SWConfig.INSTANCE.distanceBackground.toSheDanielColor()
                        )
                        .setDefaultValue(SWConfig.defaultDistanceBackground.toInt() & 0x00FFFFFF)
                        .setTooltip(MutableText.of(ConfigScreen.distanceBackgroundColorTooltip))
                        .setSaveConsumer2(
                            (value) -> SWConfig.INSTANCE.distanceBackground.updateRGB(value)
                        )
                        .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(MutableText.of(ConfigScreen.distanceBackgroundTransparencyOption),
                                        SWConfig.INSTANCE.distanceBackground.a, 0, 255
                        )
                        .setDefaultValue(SWConfig.defaultDistanceBackground.a)
                        .setTooltip(MutableText.of(ConfigScreen.distanceBackgroundTransparencyTooltip))
                        .setSaveConsumer(
                            (value) -> SWConfig.INSTANCE.distanceBackground.updateA(value)
                        )
                        .build()
        );

        SubCategoryBuilder subCategoryBuilder = entryBuilder.startSubCategory(
            MutableText.of(ConfigScreen.waypointsKeybindingsCategoryName));

        ConfigScreen.addKeybindingEntryToSubCategory(
            subCategoryBuilder, entryBuilder, SharedWaypointsClient.openConfigScreenKeyBinding,
            ConfigScreen.sWMenuBindingName
        );
        ConfigScreen.addKeybindingEntryToSubCategory(
            subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleWaypointsKeyBinding,
            ConfigScreen.toggleWaypointsBindingName
        );
        ConfigScreen.addKeybindingEntryToSubCategory(
            subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleWaypointFullNamesKeyBinding,
            toggleFullNamesBindingName
        );
        ConfigScreen.addKeybindingEntryToSubCategory(
            subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleHighlightClosestKeyBinding,
            ConfigScreen.toggleHighlightClosestBindingName
        );
        ConfigScreen.addKeybindingEntryToSubCategory(
            subCategoryBuilder, entryBuilder, SharedWaypointsClient.toggleCrossDimensionalWaypointsKeyBinding,
            ConfigScreen.toggleCrossDimensionalBindingName
        );

        category.addEntry(
            subCategoryBuilder
                .setExpanded(true)
                .setTooltip(MutableText.of(waypointKeybindingsCategoryTooltip))
                .build()
        );

        return builder.build();
    }

    private static void addKeybindingEntryToSubCategory(
        SubCategoryBuilder subCategoryBuilder, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding,
        TranslatableTextContent translationKey
    )
    {
        subCategoryBuilder.add(ConfigScreen.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static void addKeybindingEntryToCategory(
        ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding,
        TranslatableTextContent translationKey
    )
    {
        category.addEntry(ConfigScreen.getKeybindingOption(entryBuilder, keyBinding, translationKey));
    }

    private static KeyCodeEntry getKeybindingOption(
        ConfigEntryBuilder entryBuilder, KeyBinding keyBinding, TranslatableTextContent translationKey
    )
    {
        return entryBuilder
            .startKeyCodeField(MutableText.of(translationKey), KeyBindingHelper.getBoundKeyOf(keyBinding))
            .setDefaultValue(keyBinding.getDefaultKey())
            .setSaveConsumer(
                (code) ->
                {
                    keyBinding.setBoundKey(code);
                    KeyBinding.updateKeysByCode();
                    MinecraftClient.getInstance().options.write();
                }
            )
            .build();
    }

}
