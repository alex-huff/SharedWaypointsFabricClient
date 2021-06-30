package phonis.survival;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import phonis.survival.networking.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class RTSurvival implements ClientModInitializer {

	public static final int protocolVersion = 1;
	public static final String configDirectory = "config/RTSurvival/";
	public static final Identifier rtIdentifier = new Identifier("rtsurvival:main");
	private static final KeyBinding toggleWaypointsKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Toggle Waypoints",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_N,
			"RTSurvival"
		)
	);
	private static final KeyBinding toggleWaypointFullNamesKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Toggle Full Waypoint Names",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V,
			"RTSurvival"
		)
	);
	private static final KeyBinding stogKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Spectog",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UP,
			"RTSurvival"
		)
	);
	private static final KeyBinding ficClearBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"FIC Clear",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT,
			"RTSurvival"
		)
	);
	private static final KeyBinding tetherClearBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Tether Clear",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT,
			"RTSurvival"
		)
	);
	private static final KeyBinding toggleHighlightClosestBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Toggle Closest Waypoint Highlight",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			"RTSurvival"
		)
	);
	private static final KeyBinding ficCurrentHeldItem = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"FIC on the Current Held Item",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_DOWN,
			"RTSurvival"
		)
	);
	private static final KeyBinding tetherOnHoveredWaypoint = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Tether on Hovered Waypoint",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_J,
			"RTSurvival"
		)
	);
	private static final KeyBinding sTPToHoveredWaypoint = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Spec TP to Hovered Waypoint",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_K,
			"RTSurvival"
		)
	);
	private static final KeyBinding changeRenderScale = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"Render Scale Modifier",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_G,
			"RTSurvival"
		)
	);

	private void saveConfig() {
		try {
			State.config.saveToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(rtIdentifier, new RTSurvivalReceiver());
		C2SPlayChannelEvents.REGISTER.register(
			(clientPlayNetworkHandler, packetSender, minecraftClient, ids) -> {
				for (Identifier id : ids) {
					if (id.equals(RTSurvival.rtIdentifier)) {
						try {
							packetSender.sendPacket(
								rtIdentifier,
								this.packetToByteBuf(new RTRegister(RTSurvival.protocolVersion))
							);
						} catch (IOException e) {
							e.printStackTrace();
						}

						break;
					}
				}
			}
		);
		ClientPlayConnectionEvents.DISCONNECT.register(
			(clientPlayNetworkHandler, minecraftClient) -> {
				State.waypointState = null;
				State.tetherState = null;
				State.chestFindState = null;
			}
		);
		ClientTickEvents.END_CLIENT_TICK.register(
			client -> {
				boolean needToUpdateConfig = false;

				while (toggleWaypointsKeyBinding.wasPressed()) {
					boolean current = State.config.renderWaypoints;
					State.config.renderWaypoints = !current; // ok to be non-atomic since this is the only thread writing to this variable
					needToUpdateConfig = true;

					if (current) {
						State.hoveredWaypoint = null;
					}
				}

				while (toggleWaypointFullNamesKeyBinding.wasPressed()) {
					State.config.fullWaypointNames = !State.config.fullWaypointNames; // ok to be non-atomic since this is the only thread writing to this variable
					needToUpdateConfig = true;
				}

				while (toggleHighlightClosestBinding.wasPressed()) {
					State.config.highlightClosest = !State.config.highlightClosest; // ok to be non-atomic since this is the only thread writing to this variable
					needToUpdateConfig = true;
				}

				while (stogKeyBinding.wasPressed()) {
					this.sendPacket(new RTSTog());
				}

				while (ficClearBinding.wasPressed()) {
					if (changeRenderScale.isPressed()) {
						if (State.config.scale > .02f) State.config.scale -= .005f; // ok to be non-atomic since this is the only thread writing to this variable
						needToUpdateConfig = true;
					} else {
						this.sendPacket(new RTFICClear());
					}
				}

				while (tetherClearBinding.wasPressed()) {
					if (changeRenderScale.isPressed()) {
						if (State.config.scale < .1f) State.config.scale += .005f; // ok to be non-atomic since this is the only thread writing to this variable
						needToUpdateConfig = true;
					} else {
						this.sendPacket(new RTTetherClear());
					}
				}

				while (ficCurrentHeldItem.wasPressed()) {
					this.sendPacket(new RTFIC());
				}

				while (tetherOnHoveredWaypoint.wasPressed()) {
					RTWaypoint hoveredWaypoint = State.hoveredWaypoint;

					if (hoveredWaypoint == null) {
						continue;
					}

					this.sendPacket(new RTTetherOnHoveredWaypoint(hoveredWaypoint.name));
				}

				while (sTPToHoveredWaypoint.wasPressed()) {
					RTWaypoint hoveredWaypoint = State.hoveredWaypoint;

					if (hoveredWaypoint == null) {
						continue;
					}

					this.sendPacket(new RTSTPToHoveredWaypoint(hoveredWaypoint.name));
				}

				if (needToUpdateConfig) this.saveConfig();
			}
		);
	}

	private void sendPacket(RTPacket packet) {
		try {
			ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

			if (handler == null) return;

			handler.sendPacket(ClientPlayNetworking.createC2SPacket(rtIdentifier, this.packetToByteBuf(packet)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PacketByteBuf packetToByteBuf(RTPacket packet) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		PacketByteBuf packetBuffer = PacketByteBufs.create();

		oos.writeObject(packet);
		oos.flush();
		packetBuffer.writeBytes(baos.toByteArray());

		return packetBuffer;
	}

}
