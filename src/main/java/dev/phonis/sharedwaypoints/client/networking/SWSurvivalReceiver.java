package dev.phonis.sharedwaypoints.client.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SWSurvivalReceiver implements ClientPlayNetworking.PlayChannelHandler {

    public static SWSurvivalReceiver INSTANCE = new SWSurvivalReceiver();

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        byte[] data = new byte[buf.readableBytes()];

        buf.getBytes(0, data);

        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            byte packetID = dis.readByte();
            SWPacket packet = switch (packetID) {
                case Packets.In.SWUnsupportedID -> SWUnsupported.fromBytes(dis);
                case Packets.In.SWWaypointInitializeID -> SWWaypointInitialize.fromBytes(dis);
                case Packets.In.SWWaypointUpdateID -> SWWaypointUpdate.fromBytes(dis);
                case Packets.In.SWWaypointRemoveID -> SWWaypointRemove.fromBytes(dis);
                default -> null;
            };

            dis.close();
            this.handlePacket(client, responseSender, packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(MinecraftClient client, PacketSender responseSender, SWPacket packet) {
        if (packet instanceof SWUnsupported rtUnsupported) {
            System.out.println("Unsupported: " + rtUnsupported.protocolVersion);
        } else if (packet instanceof SWWaypointInitialize waypointInitialize) {
            SWStateManager.INSTANCE.initializeWaypoints(waypointInitialize.waypoints);
        } else if (packet instanceof SWWaypointUpdate waypointUpdate) {
            SWStateManager.INSTANCE.updateWaypoint(waypointUpdate.newWaypoint);
        } else if (packet instanceof SWWaypointRemove waypointRemove) {
            SWStateManager.INSTANCE.removeWaypoint(waypointRemove.toRemove);
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

}
