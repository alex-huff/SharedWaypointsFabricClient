package dev.phonis.sharedwaypoints.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.math.Projector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
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

    private static
    void renderRoundedBox(Matrix4f matrixStack, float red, float green, float blue, float alpha, double startX,
                          double startY, double endX, double endY, double radiusCorner1, double radiusCorner2,
                          double radiusCorner3, double radiusCorner4, int samples)
    {
        double[][] corners = new double[][]{ new double[]{ endX - radiusCorner4, endY - radiusCorner4, radiusCorner4 },
                                             new double[]{ endX - radiusCorner2, startY + radiusCorner2,
                                                           radiusCorner2 },
                                             new double[]{ startX + radiusCorner1, startY + radiusCorner1,
                                                           radiusCorner1 },
                                             new double[]{ startX + radiusCorner3, endY - radiusCorner3,
                                                           radiusCorner3 } };
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < 4; i++)
        {
            double[] currentCorner = corners[i];
            for (double degrees = i * 90; degrees < (90 + i * 90); degrees += (90D / samples))
            {
                double radius  = currentCorner[2];
                float  radians = (float) Math.toRadians(degrees);
                float  dy      = (float) (Math.sin(radians) * radius);
                float  dx      = (float) (Math.cos(radians) * radius);
                bufferBuilder.vertex(matrixStack, (float) currentCorner[0] + dy, (float) currentCorner[1] + dx, 0.0F)
                    .color(red, green, blue, alpha).next();
            }
        }
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }

    public static
    void renderRoundedBox(MatrixStack matrixStack, RGBAColor rgbaColor, double xStart, double yStart, double xEnd,
                          double yEnd, double radiusCorner1, double radiusCorner2, double radiusCorner3,
                          double radiusCorner4, int samples)
    {
        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        int      color          = rgbaColor.toInt();
        float    a              = (float) (color >> 24 & 255) / 255.0F;
        float    r              = (float) (color >> 16 & 255) / 255.0F;
        float    g              = (float) (color >> 8 & 255) / 255.0F;
        float    b              = (float) (color & 255) / 255.0F;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderUtils.renderRoundedBox(positionMatrix, r, g, b, a, xStart, yStart, xEnd, yEnd, radiusCorner1,
            radiusCorner2, radiusCorner3, radiusCorner4, samples);
    }

    public static
    void renderRoundedBox(MatrixStack matrixStack, RGBAColor rgbaColor, double xStart, double yStart, double xEnd,
                          double yEnd, double radius, int samples)
    {
        RenderUtils.renderRoundedBox(matrixStack, rgbaColor, xStart, yStart, xEnd, yEnd, radius, radius, radius, radius,
            samples);
    }

}
