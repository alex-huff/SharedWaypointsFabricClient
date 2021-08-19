package phonis.survival;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.TranslatableText;
import phonis.survival.networking.*;

public class Keybindings {

    public static void handle(MinecraftClient client) {
        boolean needToUpdateConfig = false;

        while (RTSurvival.openConfigScreen.wasPressed()) {
            Keybindings.openConfig(client);
        }

        while (RTSurvival.toggleWaypointsKeyBinding.wasPressed()) {
            RTConfig.INSTANCE.renderWaypoints = !RTConfig.INSTANCE.renderWaypoints;
            needToUpdateConfig = true;

            if (!RTConfig.INSTANCE.renderWaypoints) {
                State.hoveredWaypoint = null;
            }
        }

        while (RTSurvival.toggleWaypointFullNamesKeyBinding.wasPressed()) {
            RTConfig.INSTANCE.fullWaypointNames = !RTConfig.INSTANCE.fullWaypointNames;
            needToUpdateConfig = true;
        }

        while (RTSurvival.toggleHighlightClosestBinding.wasPressed()) {
            RTConfig.INSTANCE.highlightClosest = !RTConfig.INSTANCE.highlightClosest;
            needToUpdateConfig = true;
        }

        while (RTSurvival.stogKeyBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTSTog());
        }

        while (RTSurvival.ficClearBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTFICClear());
        }

        while (RTSurvival.tetherClearBinding.wasPressed()) {
            RTSurvival.sendPacket(new RTTetherClear());
        }

        while (RTSurvival.ficCurrentHeldItem.wasPressed()) {
            RTSurvival.sendPacket(new RTFIC());
        }

        while (RTSurvival.tetherOnHoveredWaypoint.wasPressed()) {
            RTWaypoint hoveredWaypoint = State.hoveredWaypoint;

            if (hoveredWaypoint == null) {
                continue;
            }

            RTSurvival.sendPacket(new RTTetherOnHoveredWaypoint(hoveredWaypoint.name));
        }

        while (RTSurvival.sTPToHoveredWaypoint.wasPressed()) {
            RTWaypoint hoveredWaypoint = State.hoveredWaypoint;

            if (hoveredWaypoint == null) {
                continue;
            }

            RTSurvival.sendPacket(new RTSTPToHoveredWaypoint(hoveredWaypoint.name));
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

        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.openConfigScreen, "binding.rtsurvival.rtMenu");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.ficCurrentHeldItem, "binding.rtsurvival.ficCurrentItem");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.stogKeyBinding, "binding.rtsurvival.spectog");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.ficClearBinding, "binding.rtsurvival.ficClear");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.tetherClearBinding, "binding.rtsurvival.tetherClear");

        category = builder.getOrCreateCategory(new TranslatableText("category.rtsurvival.waypoints"));

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.toggleWaypoints"), RTConfig.INSTANCE.renderWaypoints)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.toggleWaypoints"))
                .setSaveConsumer(
                    newValue -> {
                        RTConfig.INSTANCE.renderWaypoints = newValue;

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.fullWaypointNames"), RTConfig.INSTANCE.fullWaypointNames)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.fullWaypointNames"))
                .setSaveConsumer(
                    newValue -> {
                        RTConfig.INSTANCE.fullWaypointNames = newValue;

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startBooleanToggle(new TranslatableText("option.rtsurvival.highlightHoveredWaypoint"), RTConfig.INSTANCE.highlightClosest)
                .setDefaultValue(true)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.highlightHoveredWaypoint"))
                .setSaveConsumer(
                    newValue -> {
                        RTConfig.INSTANCE.highlightClosest = newValue;

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.renderScale"), RTConfig.INSTANCE.scale, 0, 100)
                .setDefaultValue(50)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.renderScale"))
                .setSaveConsumer(
                    (value) -> {
                        RTConfig.INSTANCE.scale = value;

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.rtsurvival.waypointColor"), RTConfig.INSTANCE.plateBackground.toSheDanielColor())
                .setDefaultValue(RTConfig.defaultPlateBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.waypointColor"))
                .setSaveConsumer2(
                    (value) -> {
                        RTConfig.INSTANCE.plateBackground.updateRGB(value);

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.waypointTransparency"), RTConfig.INSTANCE.plateBackground.a, 0, 255)
                .setDefaultValue(RTConfig.defaultPlateBackground.a)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.waypointTransparency"))
                .setSaveConsumer(
                    (value) -> {
                        RTConfig.INSTANCE.plateBackground.updateA(value);

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.rtsurvival.highlightWaypointColor"), RTConfig.INSTANCE.fullBackground.toSheDanielColor())
                .setDefaultValue(RTConfig.defaultFullBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.highlightWaypointColor"))
                .setSaveConsumer2(
                    (value) -> {
                        RTConfig.INSTANCE.fullBackground.updateRGB(value);

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.highlightWaypointTransparency"), RTConfig.INSTANCE.fullBackground.a, 0, 255)
                .setDefaultValue(RTConfig.defaultFullBackground.a)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.highlightWaypointTransparency"))
                .setSaveConsumer(
                    (value) -> {
                        RTConfig.INSTANCE.fullBackground.updateA(value);

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startColorField(new TranslatableText("option.rtsurvival.distanceBackgroundColor"), RTConfig.INSTANCE.distanceBackground.toSheDanielColor())
                .setDefaultValue(RTConfig.defaultDistanceBackground.toInt() & 0x00FFFFFF)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.distanceBackgroundColor"))
                .setSaveConsumer2(
                    (value) -> {
                        RTConfig.INSTANCE.distanceBackground.updateRGB(value);

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        category.addEntry(
            entryBuilder.startIntSlider(new TranslatableText("option.rtsurvival.distanceBackgroundTransparency"), RTConfig.INSTANCE.distanceBackground.a, 0, 255)
                .setDefaultValue(RTConfig.defaultDistanceBackground.a)
                .setTooltip(new TranslatableText("tooltip.rtsurvival.distanceBackgroundTransparency"))
                .setSaveConsumer(
                    (value) -> {
                        RTConfig.INSTANCE.distanceBackground.updateA(value);

                        RTConfig.trySave();
                    }
                )
                .build()
        );

        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.toggleWaypointsKeyBinding, "binding.rtsurvival.toggleWaypoints");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.toggleWaypointFullNamesKeyBinding, "binding.rtsurvival.toggleFullNames");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.toggleHighlightClosestBinding, "binding.rtsurvival.closestHighlight");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.tetherOnHoveredWaypoint, "binding.rtsurvival.tetherHoveredWaypoint");
        Keybindings.addKeybindingEntryToCategory(category, entryBuilder, RTSurvival.sTPToHoveredWaypoint, "binding.rtsurvival.stpHoveredWaypoint");

        Screen screen = builder.build();

        client.setScreen(screen);
    }

    private static void addKeybindingEntryToCategory(ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding, String translationKey) {
        category.addEntry(
            entryBuilder.startKeyCodeField(new TranslatableText(translationKey), KeyBindingHelper.getBoundKeyOf(keyBinding))
            .setDefaultValue(keyBinding.getDefaultKey())
            .setSaveConsumer(
                (code) -> {
                    keyBinding.setBoundKey(code);
                    KeyBinding.updateKeysByCode();
                    MinecraftClient.getInstance().options.write();
                }
            )
            .build()
        );
    }

}
