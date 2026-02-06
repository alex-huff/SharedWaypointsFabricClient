package dev.phonis.sharedwaypoints.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public class RenderHandler implements IRenderer
{

    private static final RenderHandler INSTANCE = new RenderHandler();

    public static RenderHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient minecraftClient)
    {
        WaypointRenderer.hudRenderTasks.forEach(consumer -> consumer.accept(drawContext));
    }

    @Override
    public void onRenderWorldLastAdvanced(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler)
    {
        // THIS IS NOT A TYPO
        // MALILIB IS PASSING THE PROJECTION MATRIX AS 'posMatrix' FOR SOME REASON!!!!!!!!!!!!!!!
        // :(
        // THE ACTUAL POSITION MATRIX ISN'T EVEN PASSED IN
        // lovely.
        Matrix4f projectionMatrix = posMatrix;

        // manually calculate the position matrix :(
        Quaternionf quaternionf = camera.getRotation().conjugate(new Quaternionf());
        Matrix4f positionMatrix = (new Matrix4f()).rotation(quaternionf);

        // WorldRenderer's render method modifies the modelViewStack but we want the matrix before it was modified
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.popMatrix();
        Matrix4f modelViewMatrix = new Matrix4f(modelViewStack);
        modelViewStack.pushMatrix();
        modelViewStack.mul(positionMatrix);

        // Clear any hudRenderTasks from last tick.
        WaypointRenderer.hudRenderTasks.clear();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

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
