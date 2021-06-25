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
								this.packetToByteBuf(new RTRegister())
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
				while (toggleWaypointsKeyBinding.wasPressed()) {
					State.renderWaypoints = !State.renderWaypoints; // ok to be non-atomic since this is the only thread writing to this variable
				}
				while (toggleWaypointFullNamesKeyBinding.wasPressed()) {
					State.fullWaypointNames = !State.fullWaypointNames; // ok to be non-atomic since this is the only thread writing to this variable
				}
				while (stogKeyBinding.wasPressed()) {
					this.sendPacket(new RTSTog());
				}
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
