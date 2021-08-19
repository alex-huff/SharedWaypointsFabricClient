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
import phonis.survival.state.RTStateManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RTSurvival implements ClientModInitializer {

	public static final int protocolVersion = 1;
	public static final String configDirectory = "config/RTSurvival/";
	public static final Identifier rtIdentifier = new Identifier("rtsurvival:main");
	private static final String category = "category.rtsurvival.rtSurvival";
	public static final KeyBinding openConfigScreenKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"binding.rtsurvival.rtMenu",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_G,
			RTSurvival.category
		)
	);
	public static final KeyBinding toggleWaypointsKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "binding.rtsurvival.toggleWaypoints",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
			RTSurvival.category
        )
    );
	public static final KeyBinding toggleWaypointFullNamesKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "binding.rtsurvival.toggleFullNames",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
			RTSurvival.category
        )
    );
	public static final KeyBinding toggleHighlightClosestKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"binding.rtsurvival.toggleClosestHighlight",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			RTSurvival.category
		)
	);
	public static final KeyBinding toggleCrossDimensionalWaypointsKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"binding.rtsurvival.toggleCrossDimensional",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			RTSurvival.category
		)
	);
	public static final KeyBinding tetherOnHoveredWaypointKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"binding.rtsurvival.tetherHoveredWaypoint",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_J,
			RTSurvival.category
		)
	);
	public static final KeyBinding sTPToHoveredWaypointKeyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"binding.rtsurvival.stpHoveredWaypoint",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_K,
			RTSurvival.category
		)
	);
	public static final KeyBinding stogKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "binding.rtsurvival.spectog",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
			RTSurvival.category
        )
    );
	public static final KeyBinding ficClearKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "binding.rtsurvival.ficClear",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT,
			RTSurvival.category
        )
    );
	public static final KeyBinding tetherClearKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "binding.rtsurvival.tetherClear",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT,
			RTSurvival.category
        )
    );
	public static final KeyBinding ficCurrentHeldItemKeyBinding = KeyBindingHelper.registerKeyBinding(
        new KeyBinding(
            "binding.rtsurvival.ficCurrentItem",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
			RTSurvival.category
        )
    );

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(rtIdentifier, RTSurvivalReceiver.INSTANCE);
		C2SPlayChannelEvents.REGISTER.register(
			(clientPlayNetworkHandler, packetSender, minecraftClient, ids) -> {
				for (Identifier id : ids) {
					if (id.equals(RTSurvival.rtIdentifier)) {
						try {
							clientPlayNetworkHandler.sendPacket(
								ClientPlayNetworking.createC2SPacket(
									rtIdentifier,
									RTSurvival.packetToByteBuf(new RTRegister(RTSurvival.protocolVersion))
								)
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
				RTStateManager.INSTANCE.clearState();
			}
		);
		ClientTickEvents.END_CLIENT_TICK.register(
			Keybindings::handle
		);
	}

	public static void sendPacket(RTPacket packet) {
		try {
			ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

			if (handler == null) return;

			handler.sendPacket(ClientPlayNetworking.createC2SPacket(rtIdentifier, RTSurvival.packetToByteBuf(packet)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static PacketByteBuf packetToByteBuf(RTPacket packet) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream das = new DataOutputStream(baos);
		PacketByteBuf packetBuffer = PacketByteBufs.create();

		das.writeByte(packet.getID());
		packet.toBytes(das);
		das.close();
		packetBuffer.writeBytes(baos.toByteArray());

		return packetBuffer;
	}

}
