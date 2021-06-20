package phonis.survival;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import phonis.survival.networking.RTRegister;
import phonis.survival.networking.RTSurvivalReceiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class RTSurvival implements ClientModInitializer {

	public static final Identifier rtIdentifier = new Identifier("rtsurvival:main");

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(rtIdentifier, new RTSurvivalReceiver());
		ClientPlayConnectionEvents.JOIN.register(
			(clientPlayNetworkHandler, packetSender, minecraftClient) -> {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					PacketByteBuf packetBuffer = PacketByteBufs.create();

					oos.writeObject(new RTRegister());
					oos.flush();
					packetBuffer.writeBytes(baos.toByteArray());
					packetSender.sendPacket(
						rtIdentifier,
						packetBuffer
					);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		);
		ClientPlayConnectionEvents.DISCONNECT.register(
			(clientPlayNetworkHandler, minecraftClient) -> {
				State.waypointState = null;
				State.tetherState = null;
			}
		);
	}

}
