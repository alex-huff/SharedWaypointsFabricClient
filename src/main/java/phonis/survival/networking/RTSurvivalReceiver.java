package phonis.survival.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import phonis.survival.state.RTStateManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class RTSurvivalReceiver implements ClientPlayNetworking.PlayChannelHandler {

    public static RTSurvivalReceiver INSTANCE = new RTSurvivalReceiver();

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        byte[] data = new byte[buf.readableBytes()];

        buf.getBytes(0, data);

        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            byte packetID = dis.readByte();
            RTPacket packet = switch (packetID) {
                case Packets.In.RTUnsupportedID -> RTUnsupported.fromBytes(dis);
                case Packets.In.RTWaypointInitializeID -> RTWaypointInitialize.fromBytes(dis);
                case Packets.In.RTWaypointUpdateID -> RTWaypointUpdate.fromBytes(dis);
                case Packets.In.RTWaypointRemoveID -> RTWaypointRemove.fromBytes(dis);
                case Packets.In.RTTetherUpdateID -> RTTetherUpdate.fromBytes(dis);
                case Packets.In.RTTetherRemoveID -> RTTetherRemove.fromBytes(dis);
                case Packets.In.RTChestFindSessionID -> RTChestFindSession.fromBytes(dis);
                case Packets.In.RTChestFindSessionClearID -> RTChestFindSessionClear.fromBytes(dis);
                default -> null;
            };

            dis.close();
            this.handlePacket(client, responseSender, packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(MinecraftClient client, PacketSender responseSender, RTPacket packet) {
        if (packet instanceof RTUnsupported rtUnsupported) {
            System.out.println("Unsupported: " + rtUnsupported.protocolVersion);
        } else if (packet instanceof RTWaypointInitialize waypointInitialize) {
            RTStateManager.INSTANCE.initializeWaypoints(waypointInitialize.waypoints);
        } else if (packet instanceof RTWaypointUpdate waypointUpdate) {
            RTStateManager.INSTANCE.updateWaypoint(waypointUpdate.newWaypoint);
        } else if (packet instanceof RTWaypointRemove waypointRemove) {
            RTStateManager.INSTANCE.removeWaypoint(waypointRemove.toRemove);
        } else if (packet instanceof RTTetherUpdate tetherUpdate) {
            RTStateManager.INSTANCE.updateTether(tetherUpdate.toUpdate);
        } else if (packet instanceof RTTetherRemove tetherRemove) {
            RTStateManager.INSTANCE.removeTether(tetherRemove.toRemove);
        } else if (packet instanceof RTChestFindSession chestFindSession) {
            RTStateManager.INSTANCE.updateChestFindSession(chestFindSession);
        } else if (packet instanceof RTChestFindSessionClear) {
            RTStateManager.INSTANCE.clearChestFindSession();
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

}
