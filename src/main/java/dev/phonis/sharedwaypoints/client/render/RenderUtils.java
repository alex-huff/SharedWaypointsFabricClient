package dev.phonis.sharedwaypoints.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.networking.SWDimension;
import dev.phonis.sharedwaypoints.client.networking.SWWaypoint;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Comparator;

// adapted from Masa's malilib

public
class RenderUtils
{

    private static final RGBAColor red    = new RGBAColor(255, 0, 0, 255);
    private static final RGBAColor blue   = new RGBAColor(0, 0, 255, 255);
    private static final RGBAColor yellow = new RGBAColor(255, 255, 0, 255);
    private static final RGBAColor white  = new RGBAColor(255, 255, 255, 255);
    // private static final RGBAColor distanceBackground = new RGBAColor(0x40000000);

    private static
    boolean compareDimension(SWDimension dimension, DimensionEffects.SkyType currentDimension)
    {
        return (dimension == SWDimension.OVERWORLD && currentDimension.equals(DimensionEffects.SkyType.NORMAL)) ||
               (dimension == SWDimension.NETHER && currentDimension.equals(DimensionEffects.SkyType.NONE)) ||
               (dimension == SWDimension.END && currentDimension.equals(DimensionEffects.SkyType.END));
    }

    public static
    void renderWaypoints(DimensionEffects.SkyType currentDimension)
    {
        SWStateManager.INSTANCE.withWaypoints((waypointState) ->
        {
            if (waypointState == null || waypointState.isEmpty())
            {
                return;
            }

            Camera camera    = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vec3d  cameraPos = camera.getPos();
            double cx        = cameraPos.x;
            double cy        = cameraPos.y;
            double cz        = cameraPos.z;

            SWWaypoint closest = waypointState.stream().filter(
                waypoint -> (RenderUtils.compareDimension(waypoint.location.dimension, currentDimension)) || (
                    SWConfig.INSTANCE.crossDimensionalWaypoints && (
                        waypoint.location.dimension == SWDimension.OVERWORLD &&
                        currentDimension.equals(DimensionEffects.SkyType.NONE)
                    )
                )).min(Comparator.comparingDouble(waypoint ->
            {
                boolean adjusted = waypoint.location.dimension == SWDimension.OVERWORLD &&
                                   currentDimension.equals(DimensionEffects.SkyType.NONE);
                double dx                = adjusted ? waypoint.location.x / 8d - cx : waypoint.location.x - cx;
                double dy                = adjusted ? 128d - cy : waypoint.location.y - cy;
                double dz                = adjusted ? waypoint.location.z / 8d - cz : waypoint.location.z - cz;
                Vec3d  waypointDirection = new Vec3d(dx, dy, dz).normalize();
                double rotX              = camera.getYaw();
                double rotY              = camera.getPitch();
                double xz                = Math.cos(Math.toRadians(rotY));
                double cxD               = -xz * Math.sin(Math.toRadians(rotX));
                double cyD               = -Math.sin(Math.toRadians(rotY));
                double czD               = xz * Math.cos(Math.toRadians(rotX));
                Vec3d  cameraDirection   = new Vec3d(cxD, cyD, czD).normalize();

                return cameraDirection.distanceTo(waypointDirection);
            })).orElse(null);

            if (closest == null)
            {
                return;
            }

            SWStateManager.INSTANCE.setHoveredWaypoint(closest.name);
            waypointState.stream().filter(waypoint -> !closest.name.equals(waypoint.name))
                         .forEach(waypoint -> RenderUtils.drawWaypoint(currentDimension, waypoint, false));
            RenderUtils.drawWaypoint(currentDimension, closest, SWConfig.INSTANCE.highlightClosest);
        });
    }

    private static
    void drawWaypoint(DimensionEffects.SkyType currentDimension, SWWaypoint waypoint, boolean full)
    {
        if (RenderUtils.compareDimension(waypoint.location.dimension, currentDimension))
        {
            RenderUtils.drawTextPlate(waypoint.name, waypoint.location.x, waypoint.location.y, waypoint.location.z,
                full);
        }
        else if (SWConfig.INSTANCE.crossDimensionalWaypoints && waypoint.location.dimension == SWDimension.OVERWORLD &&
                 currentDimension.equals(DimensionEffects.SkyType.NONE))
        {
            RenderUtils.drawTextPlate(waypoint.name, waypoint.location.x / 8d, 128d, waypoint.location.z / 8d, full);
        }
    }

    private static
    void setupBlend()
    {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }

    private static
    void color(float r, float g, float b, float a)
    {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    private static
    void drawTextPlate(String text, double x, double y, double z, boolean full)
    {
        Entity entity = MinecraftClient.getInstance().getCameraEntity();

        if (entity != null)
        {
            RenderUtils.drawTextPlate(text, x, y, z, entity.getYaw(), entity.getPitch(), RenderUtils.white, full);
        }
    }

    private static
    void drawTextPlate(String text, double x, double y, double z, float yaw, float pitch, RGBAColor textColor,
                       boolean full)
    {
        Camera camera      = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d  cameraPos   = camera.getPos();
        double cx          = cameraPos.x;
        double cy          = cameraPos.y;
        double cz          = cameraPos.z;
        double dx          = x - cx;
        double dy          = y - cy;
        double dz          = z - cz;
        double distance    = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        float  scale;
        double realX;
        double realY;
        double realZ;
        double maxDistance = 10;
        float  targetScale = SWConfig.INSTANCE.scale / 1000f;

        if (distance > maxDistance)
        {
            Vec3d direction = new Vec3d(dx, dy, dz).normalize().multiply(maxDistance);
            realX = cx + direction.x;
            realY = cy + direction.y;
            realZ = cz + direction.z;
            scale = targetScale;
        }
        else
        {
            realX = x;
            realY = y;
            realZ = z;
            scale = (float) (distance * targetScale / maxDistance);
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        MatrixStack  globalStack  = RenderSystem.getModelViewStack();

        globalStack.push();
        globalStack.translate(realX - cx, realY - cy, realZ - cz);

        Quaternion rot = Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw);
        rot.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        globalStack.multiply(rot);

        globalStack.scale(-scale, -scale, scale);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableCull();

        RenderUtils.setupBlend();
        RenderSystem.disableTexture();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator   tessellator = Tessellator.getInstance();
        BufferBuilder buffer      = tessellator.getBuffer();

        if (text.length() == 0)
        {
            return;
        }

        String adjustedText = (full || SWConfig.INSTANCE.fullWaypointNames) ? text : text.substring(0, 1).toUpperCase();
        String distanceStr = (int) distance + "m";
        String[] fullText = full ? new String[]{ adjustedText, (int) distance + "m" } : new String[]{ adjustedText };
        int       lineLen    = textRenderer.getWidth(adjustedText);
        int       strLenHalf = lineLen / 2;
        int       textHeight = textRenderer.fontHeight - 1;
        RGBAColor background = full ? SWConfig.INSTANCE.fullBackground : SWConfig.INSTANCE.plateBackground;

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(-strLenHalf - 1, -1, 0.0D).color(background.r, background.g, background.b, background.a).next();
        buffer.vertex(-strLenHalf - 1, textHeight, 0.0D).color(background.r, background.g, background.b, background.a)
              .next();
        buffer.vertex(strLenHalf, textHeight, 0.0D).color(background.r, background.g, background.b, background.a)
              .next();
        buffer.vertex(strLenHalf, -1, 0.0D).color(background.r, background.g, background.b, background.a).next();
        tessellator.draw();

        if (full)
        {
            lineLen    = textRenderer.getWidth(distanceStr);
            strLenHalf = lineLen / 2;

            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(-strLenHalf - 1, textRenderer.fontHeight - 1, 0.0D)
                  .color(SWConfig.INSTANCE.distanceBackground.r, SWConfig.INSTANCE.distanceBackground.g,
                      SWConfig.INSTANCE.distanceBackground.b, SWConfig.INSTANCE.distanceBackground.a).next();
            buffer.vertex(-strLenHalf - 1, textHeight + textRenderer.fontHeight, 0.0D)
                  .color(SWConfig.INSTANCE.distanceBackground.r, SWConfig.INSTANCE.distanceBackground.g,
                      SWConfig.INSTANCE.distanceBackground.b, SWConfig.INSTANCE.distanceBackground.a).next();
            buffer.vertex(strLenHalf, textHeight + textRenderer.fontHeight, 0.0D)
                  .color(SWConfig.INSTANCE.distanceBackground.r, SWConfig.INSTANCE.distanceBackground.g,
                      SWConfig.INSTANCE.distanceBackground.b, SWConfig.INSTANCE.distanceBackground.a).next();
            buffer.vertex(strLenHalf, textRenderer.fontHeight - 1, 0.0D)
                  .color(SWConfig.INSTANCE.distanceBackground.r, SWConfig.INSTANCE.distanceBackground.g,
                      SWConfig.INSTANCE.distanceBackground.b, SWConfig.INSTANCE.distanceBackground.a).next();
            tessellator.draw();
        }

        RenderSystem.enableTexture();
        int      textY       = 0;
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.loadIdentity();

        for (String line : fullText)
        {
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);
            textRenderer.draw(line, -(textRenderer.getWidth(line) / 2f), textY,
                0x20000000 | (textColor.toInt() & 0xFFFFFF), false, modelMatrix, immediate, true, 0, 15728880);
            immediate.draw();

            immediate = VertexConsumerProvider.immediate(buffer);
            textRenderer.draw(line, -(textRenderer.getWidth(line) / 2f), textY, textColor.toInt(), false, modelMatrix,
                immediate, true, 0, 15728880);
            immediate.draw();
            textY += textRenderer.fontHeight;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        globalStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

}
