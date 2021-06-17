package phonis.survival.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RTSurvivalReceiver implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        byte[] data = new byte[buf.readableBytes()];

        buf.getBytes(0, data);

        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            RTPacket packet = (RTPacket) ois.readObject();

            this.handlePacket(client, responseSender, packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(MinecraftClient client, PacketSender responseSender, RTPacket packet) {
        if (packet instanceof RTWaypointInitialize waypointInitialize) {
            StringBuilder messageBuilder = new StringBuilder();

            for (RTWaypoint waypoint : waypointInitialize.waypoints) {
                messageBuilder.append(waypoint.name).append('\n');
            }

            client.inGameHud.addChatMessage(MessageType.CHAT, Text.of(messageBuilder.toString()), client.player.getUuid());
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

}
