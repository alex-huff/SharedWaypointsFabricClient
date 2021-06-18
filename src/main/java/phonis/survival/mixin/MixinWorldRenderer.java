package phonis.survival.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import phonis.survival.State;
import phonis.survival.networking.RTDimension;
import phonis.survival.networking.RTWaypoint;

import java.util.Collections;
import java.util.List;

// adapted from Masa's malilib

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",
        at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V"))
    private void onRenderWorldLastNormal(
        MatrixStack matrices,
        float tickDelta, long limitTime, boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightmapTextureManager lightmapTextureManager,
        Matrix4f projMatrix,
        CallbackInfo ci)
    {
        this.onRenderWorldLast();
    }

    @Inject(method = "render",
        slice = @Slice(from = @At(value = "FIELD", ordinal = 1, // start from the endDrawing() call
            target = "Lnet/minecraft/client/render/RenderPhase;WEATHER_TARGET:Lnet/minecraft/client/render/RenderPhase$Target;"),
            to = @At(value = "INVOKE", ordinal = 1, // end at the second renderWeather call
                target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V")),
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gl/ShaderEffect;render(F)V"))
    private void onRenderWorldLastFabulous(
        MatrixStack matrices,
        float tickDelta, long limitTime, boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightmapTextureManager lightmapTextureManager,
        Matrix4f projMatrix,
        CallbackInfo ci)
    {
        this.onRenderWorldLast();
    }

    private void onRenderWorldLast() {
        MinecraftClient mc = this.client;

        if (mc.world == null) return;

        DimensionType currentDimension = mc.world.getDimension();

        if (State.waypointState == null) return;

        Framebuffer fb = MinecraftClient.isFabulousGraphicsOrBetter() ? mc.worldRenderer.getTranslucentFramebuffer() : null;

        if (fb != null)
        {
            fb.beginWrite(false);
        }

        MixinWorldRenderer.renderWaypoints(currentDimension);

        if (fb != null)
        {
            mc.getFramebuffer().beginWrite(false);
        }
    }

    private static boolean compareDimension(RTDimension dimension, DimensionType currentDimension) {
        return (dimension == RTDimension.OVERWORLD && currentDimension.getSkyProperties().equals(DimensionType.OVERWORLD_ID))
            || (dimension == RTDimension.NETHER && currentDimension.getSkyProperties().equals(DimensionType.THE_NETHER_ID))
            || (dimension == RTDimension.END && currentDimension.getSkyProperties().equals(DimensionType.THE_END_ID));
    }

    private static void renderWaypoints(DimensionType currentDimension) {
        for (RTWaypoint waypoint : State.waypointState) {
            if (MixinWorldRenderer.compareDimension(waypoint.dimension, currentDimension))
                MixinWorldRenderer.drawTextPlate(Collections.singletonList(waypoint.name), waypoint.x, waypoint.y, waypoint.z);
            else if (waypoint.dimension == RTDimension.OVERWORLD && currentDimension.getSkyProperties().equals(DimensionType.THE_NETHER_ID))
                MixinWorldRenderer.drawTextPlate(Collections.singletonList(waypoint.name), waypoint.x / 8.0, 128, waypoint.z / 8.0);
        }
    }

    private static void setupBlend()
    {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }

    private static void color(float r, float g, float b, float a)
    {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    private static void drawTextPlate(List<String> text, double x, double y, double z)
    {
        Entity entity = MinecraftClient.getInstance().getCameraEntity();

        if (entity != null)
        {
            MixinWorldRenderer.drawTextPlate(text, x, y, z, entity.getYaw(), entity.getPitch(), 0xFFFFFFFF, 0x40000000);
        }
    }

    private static void drawTextPlate(List<String> text, double x, double y, double z, float yaw, float pitch,
                                     int textColor, int bgColor)
    {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        double cx = cameraPos.x;
        double cy = cameraPos.y;
        double cz = cameraPos.z;
        double dx = x - cx;
        double dy = y - cy;
        double dz = z - cz;
        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        float scale;
        double realX;
        double realY;
        double realZ;
        double maxDistance = 10;
        float targetScale = .04f;

        if (distance > maxDistance) {
            Vec3d direction = new Vec3d(dx, dy, dz).normalize().multiply(maxDistance);
            realX = cx + direction.x;
            realY = cy + direction.y;
            realZ = cz + direction.z;
            scale = targetScale;
        } else {
            realX = x;
            realY = y;
            realZ = z;
            scale = (float) (distance * targetScale / maxDistance);
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        MatrixStack globalStack = RenderSystem.getModelViewStack();
        globalStack.push();
        globalStack.translate(realX - cx, realY - cy, realZ - cz);

        Quaternion rot = Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw);
        rot.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        globalStack.multiply(rot);

        globalStack.scale(-scale, -scale, scale);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableCull();

        MixinWorldRenderer.setupBlend();
        RenderSystem.disableTexture();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int maxLineLen = 0;

        for (String line : text)
        {
            maxLineLen = Math.max(maxLineLen, textRenderer.getWidth(line));
        }

        int strLenHalf = maxLineLen / 2;
        int textHeight = textRenderer.fontHeight * text.size() - 1;
        int bga = ((bgColor >>> 24) & 0xFF);
        int bgr = ((bgColor >>> 16) & 0xFF);
        int bgg = ((bgColor >>>  8) & 0xFF);
        int bgb = (bgColor          & 0xFF);

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(-strLenHalf - 1,          -1, 0.0D).color(bgr, bgg, bgb, bga).next();
        buffer.vertex(-strLenHalf - 1,  textHeight, 0.0D).color(bgr, bgg, bgb, bga).next();
        buffer.vertex( strLenHalf    ,  textHeight, 0.0D).color(bgr, bgg, bgb, bga).next();
        buffer.vertex( strLenHalf    ,          -1, 0.0D).color(bgr, bgg, bgb, bga).next();
        tessellator.draw();

        RenderSystem.enableTexture();
        int textY = 0;
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.loadIdentity();

        for (String line : text)
        {
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);
            textRenderer.draw(line, -strLenHalf, textY, 0x20000000 | (textColor & 0xFFFFFF), false, modelMatrix, immediate, true, 0, 15728880);
            immediate.draw();

            immediate = VertexConsumerProvider.immediate(buffer);
            textRenderer.draw(line, -strLenHalf, textY, textColor, false, modelMatrix, immediate, true, 0, 15728880);
            immediate.draw();
            textY += textRenderer.fontHeight;
        }

        MixinWorldRenderer.color(1f, 1f, 1f, 1f);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        globalStack.pop();
    }

}