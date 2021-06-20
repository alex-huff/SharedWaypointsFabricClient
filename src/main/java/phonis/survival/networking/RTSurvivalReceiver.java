package phonis.survival.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import phonis.survival.State;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            State.waypointState = waypointInitialize.waypoints;
        } else if (packet instanceof RTWaypointUpdate waypointUpdate) {
            if (State.waypointState == null) return;

            List<RTWaypoint> newWaypointState = State.waypointState.stream().filter(waypoint -> !waypoint.name.equals(waypointUpdate.newWaypoint.name)).collect(Collectors.toList());

            newWaypointState.add(waypointUpdate.newWaypoint);

            State.waypointState = newWaypointState;
        } else if (packet instanceof RTWaypointRemove waypointRemove) {
            if (State.waypointState == null) return;

            List<RTWaypoint> newWaypointState = State.waypointState.stream().filter(waypoint -> !waypoint.name.equals(waypointRemove.toRemove)).collect(Collectors.toList());

            State.waypointState = newWaypointState;
        } else if (packet instanceof RTTetherUpdate tetherUpdate) {
            if (State.tetherState == null) {
                State.tetherState = Collections.singletonList(tetherUpdate.toUpdate);
            } else {
                List<RTTether> newTetherState = State.tetherState.stream().filter(tether -> !tether.equals(tetherUpdate.toUpdate)).collect(Collectors.toList());

                newTetherState.add(tetherUpdate.toUpdate);

                State.tetherState = newTetherState;
            }
        } else if (packet instanceof RTTetherRemove tetherRemove) {
            if (State.tetherState == null) return;

            List<RTTether> newTetherState = State.tetherState.stream().filter(tether -> !tether.equals(tetherRemove.toRemove)).collect(Collectors.toList());

            State.tetherState = newTetherState;
        } else {
            System.out.println("Unrecognised packet.");
        }
    }

}
