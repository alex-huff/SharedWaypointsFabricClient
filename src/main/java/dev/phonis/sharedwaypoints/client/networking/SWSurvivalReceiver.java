package dev.phonis.sharedwaypoints.client.networking;

import dev.phonis.sharedwaypoints.client.networking.payload.SWPayload;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public
class SWSurvivalReceiver implements ClientPlayNetworking.PlayPayloadHandler<SWPayload>
{

    public static SWSurvivalReceiver INSTANCE = new SWSurvivalReceiver();

    @Override
    public
    void receive(SWPayload payload, ClientPlayNetworking.Context context)
    {
        MinecraftClient client         = context.client();
        PacketSender    responseSender = context.responseSender();

        try
        {
            DataInputStream dis      = new DataInputStream(new ByteArrayInputStream(payload.bytes()));
            byte            packetID = dis.readByte();
            SWPacket packet = switch (packetID)
                {
                    case Packets.In.SWUnsupportedID -> SWUnsupported.fromBytes(dis);
                    case Packets.In.SWWaypointInitializeID -> SWWaypointInitialize.fromBytes(dis);
                    case Packets.In.SWWaypointUpdateID -> SWWaypointUpdate.fromBytes(dis);
                    case Packets.In.SWWaypointRemoveID -> SWWaypointRemove.fromBytes(dis);
                    default -> null;
                };

            dis.close();
            this.handlePacket(client, responseSender, packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private
    void handlePacket(MinecraftClient client, PacketSender responseSender, SWPacket packet)
    {
        if (packet instanceof SWUnsupported rtUnsupported)
        {
            System.out.println("Unsupported: " + rtUnsupported.protocolVersion);
        }
        else if (packet instanceof SWWaypointInitialize waypointInitialize)
        {
            SWStateManager.INSTANCE.initializeWaypoints(waypointInitialize.waypoints);
        }
        else if (packet instanceof SWWaypointUpdate waypointUpdate)
        {
            SWStateManager.INSTANCE.updateWaypoint(waypointUpdate.newWaypoint);
        }
        else if (packet instanceof SWWaypointRemove waypointRemove)
        {
            SWStateManager.INSTANCE.removeWaypoint(waypointRemove.toRemove);
        }
        else
        {
            System.out.println("Unrecognised packet.");
        }
    }
}
