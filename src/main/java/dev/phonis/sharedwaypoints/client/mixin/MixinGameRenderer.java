package dev.phonis.sharedwaypoints.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.render.WaypointRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.dimension.DimensionType;
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
            target = "Lnet/minecraft/client/renderer/LevelRenderer;render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V", shift = At.Shift.BEFORE
        )
    )
    public void onWorldRender(DeltaTracker renderTickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f matrix4f)
    {
        CameraRenderState cameraRendererState = Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
        Matrix4f projectionMatrix = matrix4f;
        Matrix4f positionMatrix = cameraRendererState.viewRotationMatrix;
        Matrix4f modelViewMatrix = RenderSystem.getModelViewStack();

        // Clear any hudRenderTasks from last tick.
        WaypointRenderer.hudRenderTasks.clear();
        Minecraft minecraftClient = Minecraft.getInstance();
        Camera camera = minecraftClient.gameRenderer.mainCamera();

        if (minecraftClient.level == null)
        {
            return;
        }

        DimensionType.Skybox currentDimension = minecraftClient.level.dimensionType().skybox();

        if (SWConfig.INSTANCE.renderWaypoints)
        {
            WaypointRenderer.renderWaypoints(currentDimension, projectionMatrix, positionMatrix, modelViewMatrix, camera);
        }
    }

}
