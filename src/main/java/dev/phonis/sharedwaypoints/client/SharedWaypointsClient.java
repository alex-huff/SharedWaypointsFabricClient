package dev.phonis.sharedwaypoints.client;

import dev.phonis.sharedwaypoints.client.keybindings.Keybindings;
import dev.phonis.sharedwaypoints.client.networking.SWPacket;
import dev.phonis.sharedwaypoints.client.networking.SWRegister;
import dev.phonis.sharedwaypoints.client.networking.SWSurvivalReceiver;
import dev.phonis.sharedwaypoints.client.networking.payload.SWPayload;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SharedWaypointsClient implements ClientModInitializer
{

    public static final int protocolVersion = 1;

    @Override
    public void onInitializeClient()
    {
        PayloadTypeRegistry.playC2S().register(SWPayload.id, SWPayload.codec);
        PayloadTypeRegistry.playS2C().register(SWPayload.id, SWPayload.codec);
        ClientPlayNetworking.registerGlobalReceiver(SWPayload.id, SWSurvivalReceiver.INSTANCE);
        C2SPlayChannelEvents.REGISTER.register((clientPlayNetworkHandler, packetSender, minecraftClient, ids) ->
        {
            for (Identifier id : ids)
            {
                if (id.equals(SWPayload.id.id()))
                {
                    if (Thread.currentThread().getName().equals("Render thread"))
                    {
                        minecraftClient.send(() ->
                        {
                            try
                            {
                                clientPlayNetworkHandler.sendPacket(ClientPlayNetworking.createC2SPacket(new SWPayload(SharedWaypointsClient.packetToBytes(new SWRegister(SharedWaypointsClient.protocolVersion)))));
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        });
                    }
                    break;
                }
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> SWStateManager.INSTANCE.clearState());
        ClientTickEvents.END_CLIENT_TICK.register(Keybindings::handle);
        Keybindings.handle(MinecraftClient.getInstance()); // Force Keybindings class to be loaded
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    private static byte[] packetToBytes(SWPacket packet) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream das = new DataOutputStream(baos);

        das.writeByte(packet.getID());
        packet.toBytes(das);
        das.close();
        return baos.toByteArray();
    }

}
