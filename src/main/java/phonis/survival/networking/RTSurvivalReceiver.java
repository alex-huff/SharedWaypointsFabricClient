package phonis.survival.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import phonis.survival.state.RTStateManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RTSurvivalReceiver implements ClientPlayNetworking.PlayChannelHandler {

    public static RTSurvivalReceiver INSTANCE = new RTSurvivalReceiver();

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
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

}
