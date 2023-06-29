package dev.phonis.sharedwaypoints.client.render;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.networking.SWDimension;
import dev.phonis.sharedwaypoints.client.networking.SWWaypoint;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public
class WaypointRenderer
{

    private
    record RenderContext3D(Vec3d realLocation, Vec3d screenCoordinates, SWWaypoint waypoint)
    {
    }

    private
    record RenderContext2D(int distance, Vec2f pixelCoordinates, SWWaypoint waypoint)
    {
    }

    public static final List<Consumer<DrawContext>> hudRenderTasks = new ArrayList<>();

    private static
    boolean shouldRender(SWWaypoint swWaypoint, DimensionEffects.SkyType currentDimension)
    {
        return (WaypointRenderer.compareDimension(swWaypoint.location.dimension, currentDimension)) ||
               (SWConfig.INSTANCE.crossDimensionalWaypoints &&
                (swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                 currentDimension.equals(DimensionEffects.SkyType.NONE)));
    }

    public static
    void renderWaypoints(MatrixStack matrixStack, DimensionEffects.SkyType currentDimension)
    {
        MinecraftClient                              minecraftClient = MinecraftClient.getInstance();
        int                                          screenWidth     = minecraftClient.getWindow().getScaledWidth();
        int                                          screenHeight    = minecraftClient.getWindow().getScaledHeight();
        Vec2f                                        screenMiddle    = new Vec2f(screenWidth / 2F, screenHeight / 2F);
        final List<WaypointRenderer.RenderContext2D> toRender        = new ArrayList<>();
        SWStateManager.INSTANCE.withWaypoints((waypointState) -> waypointState.stream()
            .filter(waypoint -> WaypointRenderer.shouldRender(waypoint, currentDimension)).map(swWaypoint ->
            {
                boolean adjusted = swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                                   currentDimension.equals(DimensionEffects.SkyType.NONE);
                Vec3d adjustedLocation = new Vec3d(adjusted ? swWaypoint.location.x / 8d : swWaypoint.location.x,
                    adjusted ? 128d : swWaypoint.location.y,
                    adjusted ? swWaypoint.location.z / 8d : swWaypoint.location.z);
                Vec3d pixelCoordinates = RenderUtils.worldSpaceToScreenSpace(
                    new Vec3d(adjustedLocation.x, adjustedLocation.y, adjustedLocation.z), matrixStack);
                if (swWaypoint.name.equals("IndustrialDistrict"))
                {
                    System.out.println(pixelCoordinates.z);
                }
                return new WaypointRenderer.RenderContext3D(adjustedLocation, pixelCoordinates, swWaypoint);
            }).filter(renderContext3D -> RenderUtils.coordinateOnScreen(renderContext3D.screenCoordinates))
            .map(renderContext3D ->
            {
                Vec2f pixelCoordinates = new Vec2f((float) renderContext3D.screenCoordinates().x,
                    (float) renderContext3D.screenCoordinates().y);
                int distance = (int) renderContext3D.realLocation()
                    .distanceTo(minecraftClient.gameRenderer.getCamera().getPos());
                return new WaypointRenderer.RenderContext2D(distance, pixelCoordinates, renderContext3D.waypoint());
            }).forEach(toRender::add));
        if (toRender.size() == 0)
        {
            return;
        }
        toRender.sort(
            Comparator.comparing(renderContext2D -> renderContext2D.pixelCoordinates().distanceSquared(screenMiddle),
                Comparator.reverseOrder()));
        final WaypointRenderer.RenderContext2D closestWaypoint = toRender.get(toRender.size() - 1);
        WaypointRenderer.hudRenderTasks.add((drawContext) ->
        {
            SWStateManager.INSTANCE.setHoveredWaypoint(closestWaypoint.waypoint().name);
            IntStream.range(0, toRender.size() - 1)
                .forEach(i -> WaypointRenderer.drawWaypoint(drawContext, toRender.get(i), false));
            WaypointRenderer.drawWaypoint(drawContext, closestWaypoint, SWConfig.INSTANCE.highlightClosest);
        });
    }

    private static
    void drawWaypoint(DrawContext drawContext, RenderContext2D toRender, boolean highlighted)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Vec2f           position        = toRender.pixelCoordinates();
        SWWaypoint      waypoint        = toRender.waypoint();
        TextRenderer    textRenderer    = minecraftClient.textRenderer;
        String waypointLabel = (SWConfig.INSTANCE.fullWaypointNames || highlighted) ? waypoint.name
                                                                                    : waypoint.name.substring(0, 1)
                                   .toUpperCase(Locale.ROOT);
        float scale = SWConfig.INSTANCE.renderScale / 100F;
        // -1 on width and height to ignore shadow since it will not be used
        float     waypointTextWidth  = textRenderer.getWidth(waypointLabel) - 1;
        float     waypointTextHeight = textRenderer.fontHeight - 1;
        float     padding            = waypointTextHeight * .2F;
        RGBAColor textColor          = SWConfig.INSTANCE.textColor;
        RGBAColor waypointColor = highlighted ? SWConfig.INSTANCE.fullBackground : SWConfig.INSTANCE.plateBackground;
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(position.x, position.y, 0);
        drawContext.getMatrices().scale(scale, scale, 0);
        RenderUtils.renderRoundedBox(drawContext.getMatrices(), waypointColor, -waypointTextWidth / 2F - padding,
            -waypointTextHeight / 2F - padding, waypointTextWidth / 2F + padding, waypointTextHeight / 2F + padding, 3,
            25);
        textRenderer.draw(waypointLabel, -waypointTextWidth / 2F, -waypointTextHeight / 2F, textColor.toInt(), false,
            drawContext.getMatrices().peek().getPositionMatrix(), drawContext.getVertexConsumers(),
            TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        drawContext.draw();
        if (highlighted)
        {
            drawContext.getMatrices().translate(0, waypointTextHeight + padding * 2, 0);
            String    distanceLabel      = toRender.distance() + "m";
            float     distanceTextWidth  = textRenderer.getWidth(distanceLabel) - 1;
            float     distanceTextHeight = textRenderer.fontHeight - 1;
            RGBAColor distanceColor      = SWConfig.INSTANCE.distanceBackground;
            RenderUtils.renderRoundedBox(drawContext.getMatrices(), distanceColor, -distanceTextWidth / 2F - padding,
                -distanceTextHeight / 2F - padding, distanceTextWidth / 2F + padding, distanceTextHeight / 2F + padding,
                3, 25);
            textRenderer.draw(distanceLabel, -distanceTextWidth / 2F, -distanceTextHeight / 2F, textColor.toInt(), false,
                drawContext.getMatrices().peek().getPositionMatrix(), drawContext.getVertexConsumers(),
                TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            drawContext.draw();
        }
        drawContext.getMatrices().pop();
    }

    private static
    boolean compareDimension(SWDimension dimension, DimensionEffects.SkyType currentDimension)
    {
        return (dimension == SWDimension.OVERWORLD && currentDimension.equals(DimensionEffects.SkyType.NORMAL)) ||
               (dimension == SWDimension.NETHER && currentDimension.equals(DimensionEffects.SkyType.NONE)) ||
               (dimension == SWDimension.END && currentDimension.equals(DimensionEffects.SkyType.END));
    }

}