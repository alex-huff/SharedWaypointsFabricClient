package dev.phonis.sharedwaypoints.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.render.WaypointRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{

    @Inject(
        method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V", shift = At.Shift.BEFORE
        )
    )
    public void onWorldRender(DeltaTracker renderTickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f matrix4f,
                              @Local(ordinal = 1) Matrix4f matrix4f2)
    {
        Matrix4f projectionMatrix = matrix4f;
        Matrix4f positionMatrix = matrix4f2;
        Matrix4f modelViewMatrix = RenderSystem.getModelViewMatrix();

        // Clear any hudRenderTasks from last tick.
        WaypointRenderer.hudRenderTasks.clear();
        Minecraft minecraftClient = Minecraft.getInstance();
        Camera camera = minecraftClient.gameRenderer.getMainCamera();

        if (minecraftClient.level == null)
        {
            return;
        }

        DimensionSpecialEffects.SkyType currentDimension = minecraftClient.level.effects().skyType();

        if (SWConfig.INSTANCE.renderWaypoints)
        {
            WaypointRenderer.renderWaypoints(currentDimension, projectionMatrix, positionMatrix, modelViewMatrix, camera);
        }
    }

}
