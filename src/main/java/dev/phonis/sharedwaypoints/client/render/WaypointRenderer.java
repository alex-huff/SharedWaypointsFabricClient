package dev.phonis.sharedwaypoints.client.render;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.networking.SWDimension;
import dev.phonis.sharedwaypoints.client.networking.SWWaypoint;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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

    public static final List<Runnable> hudRenderTasks = new ArrayList<>();

    public static
    void renderWaypoints(MatrixStack matrixStack, DimensionEffects.SkyType currentDimension)
    {
        MinecraftClient                              minecraftClient = MinecraftClient.getInstance();
        int                                          screenWidth     = minecraftClient.getWindow().getScaledWidth();
        int                                          screenHeight    = minecraftClient.getWindow().getScaledHeight();
        Vec2f                                        screenMiddle    = new Vec2f(screenWidth / 2F, screenHeight / 2F);
        final List<WaypointRenderer.RenderContext2D> toRender        = new ArrayList<>();
        SWStateManager.INSTANCE.withWaypoints((waypointState) -> waypointState.stream().sequential().filter(
                waypoint -> (WaypointRenderer.compareDimension(waypoint.location.dimension, currentDimension)) ||
                            (SWConfig.INSTANCE.crossDimensionalWaypoints &&
                             (waypoint.location.dimension == SWDimension.OVERWORLD &&
                              currentDimension.equals(DimensionEffects.SkyType.NONE)))).map(swWaypoint ->
            {
                boolean adjusted = swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                                   currentDimension.equals(DimensionEffects.SkyType.NONE);
                Vec3d adjustedLocation = new Vec3d(adjusted ? swWaypoint.location.x / 8d : swWaypoint.location.x,
                    adjusted ? 128d : swWaypoint.location.y, adjusted ? swWaypoint.location.z / 8d : swWaypoint.location.z);
                Vec3d pixelCoordinates = RenderUtils.worldSpaceToScreenSpace(
                    new Vec3d(adjustedLocation.x, adjustedLocation.y, adjustedLocation.z), matrixStack);
                return new WaypointRenderer.RenderContext3D(adjustedLocation, pixelCoordinates, swWaypoint);
            }).filter(renderContext3D -> RenderUtils.coordinateOnScreen(renderContext3D.screenCoordinates))
            .map(renderContext3D ->
            {
                Vec2f pixelCoordinates = new Vec2f((float) renderContext3D.screenCoordinates.x,
                    (float) renderContext3D.screenCoordinates.y);
                int distance = (int) Math.round(
                    renderContext3D.realLocation.distanceTo(minecraftClient.gameRenderer.getCamera().getPos()));
                return new WaypointRenderer.RenderContext2D(distance, pixelCoordinates, renderContext3D.waypoint);
            }).forEach(toRender::add));
        if (toRender.size() == 0)
        {
            return;
        }
        toRender.sort(
            Comparator.comparing(renderContext2D -> renderContext2D.pixelCoordinates.distanceSquared(screenMiddle),
                Comparator.reverseOrder()));
        final WaypointRenderer.RenderContext2D closestWaypoint = toRender.get(toRender.size() - 1);
        WaypointRenderer.hudRenderTasks.add(() ->
        {
            MatrixStack hudMatrixStack = new MatrixStack();
            SWStateManager.INSTANCE.setHoveredWaypoint(closestWaypoint.waypoint.name);
            IntStream.range(0, toRender.size() - 1)
                .forEach(i -> WaypointRenderer.drawWaypoint(hudMatrixStack, toRender.get(i), false));
            WaypointRenderer.drawWaypoint(hudMatrixStack, closestWaypoint, SWConfig.INSTANCE.highlightClosest);
        });
    }

    private static
    void drawWaypoint(MatrixStack matrixStack, RenderContext2D toRender, boolean full)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Vec2f           position        = toRender.pixelCoordinates;
        SWWaypoint      waypoint        = toRender.waypoint;
        TextRenderer    textRenderer    = minecraftClient.textRenderer;
        String waypointLabel = (SWConfig.INSTANCE.fullWaypointNames || full) ? waypoint.name
                                                                             : waypoint.name.substring(0, 1)
                                   .toUpperCase(Locale.ROOT);
        float scale      = SWConfig.INSTANCE.scale / 100F;
        float textWidth  = textRenderer.getWidth(waypointLabel) * scale;
        float textHeight = textRenderer.fontHeight * scale;
        float labelX     = position.x - textWidth / 2F;
        float labelY     = position.y - textHeight / 2F;
        matrixStack.push();
        matrixStack.translate(labelX, labelY, 0);
        matrixStack.scale(scale, scale, 0);
        textRenderer.draw(matrixStack, waypointLabel, 0, 0, 0xFFFFFFFF);
        matrixStack.pop();
    }

    private static
    boolean compareDimension(SWDimension dimension, DimensionEffects.SkyType currentDimension)
    {
        return (dimension == SWDimension.OVERWORLD && currentDimension.equals(DimensionEffects.SkyType.NORMAL)) ||
               (dimension == SWDimension.NETHER && currentDimension.equals(DimensionEffects.SkyType.NONE)) ||
               (dimension == SWDimension.END && currentDimension.equals(DimensionEffects.SkyType.END));
    }

}