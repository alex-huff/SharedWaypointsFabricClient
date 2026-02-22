package dev.phonis.sharedwaypoints.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.render.WaypointRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{

    @Inject(
        method = "renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V", shift = At.Shift.BEFORE
        )
    )
    public void onWorldRender(RenderTickCounter renderTickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f matrix4f,
                              @Local(ordinal = 1) Matrix4f matrix4f2)
    {
        Matrix4f projectionMatrix = matrix4f;
        Matrix4f positionMatrix = matrix4f2;
        Matrix4f modelViewMatrix = RenderSystem.getModelViewMatrix();

        // Clear any hudRenderTasks from last tick.
        WaypointRenderer.hudRenderTasks.clear();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Camera camera = minecraftClient.gameRenderer.getCamera();

        if (minecraftClient.world == null)
        {
            return;
        }

        DimensionEffects.SkyType currentDimension = minecraftClient.world.getDimensionEffects().getSkyType();

        if (SWConfig.INSTANCE.renderWaypoints)
        {
            WaypointRenderer.renderWaypoints(currentDimension, projectionMatrix, positionMatrix, modelViewMatrix, camera);
        }
    }

}
