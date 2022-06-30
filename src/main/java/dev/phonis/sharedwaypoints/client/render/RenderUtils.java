package dev.phonis.sharedwaypoints.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.math.Projector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.opengl.GL11;

public
class RenderUtils
{
    public static
    boolean coordinateOnScreen(Vec3d position)
    {
        return position != null && position.z > 0;
    }

    public static
    Vec3d worldSpaceToScreenSpace(Vec3d position, MatrixStack matrixStack)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Camera          camera          = minecraftClient.getEntityRenderDispatcher().camera;
        Matrix4f        positionMatrix  = matrixStack.peek().getPositionMatrix();
        double          dx              = position.x - camera.getPos().x;
        double          dy              = position.y - camera.getPos().y;
        double          dz              = position.z - camera.getPos().z;
        Vector4f        cameraDirection = new Vector4f((float) dx, (float) dy, (float) dz, 1.f);
        cameraDirection.transform(positionMatrix);
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix().copy();
        Matrix4f modelViewMatrix  = RenderSystem.getModelViewMatrix().copy();
        // Switch order
        projectionMatrix.transpose();
        modelViewMatrix.transpose();
        projectionMatrix.multiply(modelViewMatrix);
        Vec3d screenCoords = ((Projector) (Object) projectionMatrix).project(cameraDirection.getX(),
            cameraDirection.getY(), cameraDirection.getZ(), viewport);
        int displayHeight = minecraftClient.getWindow().getHeight();
        return new Vec3d(screenCoords.x / minecraftClient.getWindow().getScaleFactor(),
            (displayHeight - screenCoords.y) / minecraftClient.getWindow().getScaleFactor(), screenCoords.z);
    }

}
