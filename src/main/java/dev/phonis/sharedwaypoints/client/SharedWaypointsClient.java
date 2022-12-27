package dev.phonis.sharedwaypoints.client;

import dev.phonis.sharedwaypoints.client.keybindings.Keybindings;
import dev.phonis.sharedwaypoints.client.networking.SWPacket;
import dev.phonis.sharedwaypoints.client.networking.SWRegister;
import dev.phonis.sharedwaypoints.client.networking.SWSurvivalReceiver;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class SharedWaypointsClient implements ClientModInitializer
{

    public static final  int        protocolVersion                           = 1;
    public static final  Identifier sWIdentifier                              = new Identifier("sharedwaypoints:main");

    @Override
    public
    void onInitializeClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(sWIdentifier, SWSurvivalReceiver.INSTANCE);
        C2SPlayChannelEvents.REGISTER.register((clientPlayNetworkHandler, packetSender, minecraftClient, ids) ->
        {
            for (Identifier id : ids)
            {
                if (id.equals(SharedWaypointsClient.sWIdentifier))
                {
                    try
                    {
                        if (Thread.currentThread().getName().equals("Render thread"))
                        {
                            clientPlayNetworkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(sWIdentifier,
                                SharedWaypointsClient.packetToByteBuf(
                                    new SWRegister(SharedWaypointsClient.protocolVersion))));
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register(
            (clientPlayNetworkHandler, minecraftClient) -> SWStateManager.INSTANCE.clearState());
        ClientTickEvents.END_CLIENT_TICK.register(Keybindings::handle);
        Keybindings.handle(MinecraftClient.getInstance()); // Force Keybindings class to be loaded
    }

    public static
    void sendPacket(SWPacket packet)
    {
        try
        {
            ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

            if (handler == null)
            {
                return;
            }

            handler.sendPacket(
                ClientPlayNetworking.createC2SPacket(sWIdentifier, SharedWaypointsClient.packetToByteBuf(packet)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static
    PacketByteBuf packetToByteBuf(SWPacket packet) throws IOException
    {
        ByteArrayOutputStream baos         = new ByteArrayOutputStream();
        DataOutputStream      das          = new DataOutputStream(baos);
        PacketByteBuf         packetBuffer = PacketByteBufs.create();

        das.writeByte(packet.getID());
        packet.toBytes(das);
        das.close();
        packetBuffer.writeBytes(baos.toByteArray());

        return packetBuffer;
    }

}
