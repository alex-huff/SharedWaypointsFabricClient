package dev.phonis.sharedwaypoints.client.render;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.math.Projector;
import dev.phonis.sharedwaypoints.client.networking.SWDimension;
import dev.phonis.sharedwaypoints.client.networking.SWWaypoint;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class WaypointRenderer
{

    public static boolean coordinateOnScreen(Vec3d position)
    {
        return position != null && position.z >= 0;
    }

    public static Vec3d worldSpaceToScreenSpace(Vec3d position, Matrix4f projectionMatrix, Matrix4f positionMatrix, Matrix4f modelViewMatrix, Camera camera)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double dx = position.x - camera.getPos().x;
        double dy = position.y - camera.getPos().y;
        double dz = position.z - camera.getPos().z;
        Vector4f cameraDirection = new Vector4f((float) dx, (float) dy, (float) dz, 1F);
        cameraDirection.mul(positionMatrix);
        int[] viewport = new int[]{ 0, 0, minecraftClient.getWindow().getFramebufferWidth(), minecraftClient.getWindow().getFramebufferHeight() };
        projectionMatrix = new Matrix4f(projectionMatrix);
        projectionMatrix.mul(modelViewMatrix);
        Vec3d screenCoords
            = ((Projector) projectionMatrix).projectNonClampZ(cameraDirection.x(), cameraDirection.y(), cameraDirection.z(), viewport);
        int displayHeight = minecraftClient.getWindow().getFramebufferHeight();
        double scaleFactor = minecraftClient.getWindow().getScaleFactor();
        return new Vec3d(screenCoords.x / scaleFactor, (displayHeight - screenCoords.y) / scaleFactor, screenCoords.z);
    }

    private record RenderContext3D(Vec3d realLocation, Vec3d screenCoordinates, SWWaypoint waypoint)
    {

    }

    private record RenderContext2D(int distance, Vec2f pixelCoordinates, SWWaypoint waypoint)
    {

    }

    public static final List<Consumer<DrawContext>> hudRenderTasks = new ArrayList<>();

    private static boolean shouldRender(SWWaypoint swWaypoint, DimensionEffects.SkyType currentDimension)
    {
        return (WaypointRenderer.compareDimension(swWaypoint.location.dimension, currentDimension)) ||
               (SWConfig.INSTANCE.crossDimensionalWaypoints &&
                (swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                 currentDimension.equals(DimensionEffects.SkyType.NONE)));
    }

    public static void renderWaypoints(DimensionEffects.SkyType currentDimension, Matrix4f projectionMatrix, Matrix4f positionMatrix, Matrix4f modelViewMatrix, Camera camera)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        int screenWidth = minecraftClient.getWindow().getScaledWidth();
        int screenHeight = minecraftClient.getWindow().getScaledHeight();
        Vec2f screenMiddle = new Vec2f(screenWidth / 2F, screenHeight / 2F);
        final List<WaypointRenderer.RenderContext2D> toRender = new ArrayList<>();
        SWStateManager.INSTANCE.withWaypoints((waypointState) -> waypointState.stream()
            .filter(waypoint -> WaypointRenderer.shouldRender(waypoint, currentDimension)).map(swWaypoint ->
            {
                boolean adjusted = swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                                   currentDimension.equals(DimensionEffects.SkyType.NONE);
                Vec3d adjustedLocation = new Vec3d(
                    adjusted ? swWaypoint.location.x / 8d : swWaypoint.location.x,
                    adjusted ? 128d : swWaypoint.location.y,
                    adjusted ? swWaypoint.location.z / 8d : swWaypoint.location.z);
                Vec3d pixelCoordinates
                    = WaypointRenderer.worldSpaceToScreenSpace(new Vec3d(adjustedLocation.x, adjustedLocation.y, adjustedLocation.z), projectionMatrix, positionMatrix, modelViewMatrix, camera);
                return new WaypointRenderer.RenderContext3D(adjustedLocation, pixelCoordinates, swWaypoint);
            }).filter(renderContext3D -> WaypointRenderer.coordinateOnScreen(renderContext3D.screenCoordinates))
            .map(renderContext3D ->
            {
                Vec2f pixelCoordinates
                    = new Vec2f((float) renderContext3D.screenCoordinates().x, (float) renderContext3D.screenCoordinates().y);
                int distance = (int) renderContext3D.realLocation()
                    .distanceTo(minecraftClient.gameRenderer.getCamera().getPos());
                return new WaypointRenderer.RenderContext2D(distance, pixelCoordinates, renderContext3D.waypoint());
            }).forEach(toRender::add));
        if (toRender.isEmpty())
        {
            return;
        }
        toRender.sort(Comparator.comparing(renderContext2D -> renderContext2D.pixelCoordinates()
            .distanceSquared(screenMiddle), Comparator.reverseOrder()));
        final WaypointRenderer.RenderContext2D closestWaypoint = toRender.getLast();
        WaypointRenderer.hudRenderTasks.add((drawContext) ->
        {
            SWStateManager.INSTANCE.setHoveredWaypoint(closestWaypoint.waypoint().name);
            IntStream.range(0, toRender.size() - 1)
                .forEach(i -> WaypointRenderer.drawWaypoint(drawContext, toRender.get(i), false));
            WaypointRenderer.drawWaypoint(drawContext, closestWaypoint, SWConfig.INSTANCE.highlightClosest);
        });
    }

    private static void drawWaypoint(DrawContext drawContext, RenderContext2D toRender, boolean highlighted)
    {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Vec2f position = toRender.pixelCoordinates();
        SWWaypoint waypoint = toRender.waypoint();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        String waypointLabel = (SWConfig.INSTANCE.fullWaypointNames || highlighted) ? waypoint.name
                                                                                    : waypoint.name.substring(0, 1)
                                   .toUpperCase(Locale.ROOT);
        float scale = SWConfig.INSTANCE.renderScale / 100F;
        // -1 on width and height to ignore shadow since it will not be used
        int waypointTextWidth = textRenderer.getWidth(waypointLabel) - 1;
        int waypointTextHeight = textRenderer.fontHeight - 1;
        int halfWaypointTextWidth = Math.round(waypointTextWidth / 2F);
        int halfWaypointTextHeight = Math.round(waypointTextHeight / 2F);
        int padding = Math.round(waypointTextHeight * .2F);
        RGBAColor textColor = SWConfig.INSTANCE.textColor;
        RGBAColor waypointColor = highlighted ? SWConfig.INSTANCE.fullBackground : SWConfig.INSTANCE.plateBackground;
        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().translate(position.x, position.y);
        drawContext.getMatrices().scale(scale, scale);
        drawContext.fill(-halfWaypointTextWidth - padding, -halfWaypointTextHeight - padding, halfWaypointTextWidth + padding, halfWaypointTextHeight + padding, waypointColor.toInt());
        drawContext.drawText(textRenderer, waypointLabel, -halfWaypointTextWidth, -halfWaypointTextHeight, textColor.toInt(), false);
        if (highlighted)
        {
            drawContext.getMatrices().translate(0, waypointTextHeight + padding * 2);
            String distanceLabel = toRender.distance() + "m";
            int distanceTextWidth = textRenderer.getWidth(distanceLabel) - 1;
            int distanceTextHeight = textRenderer.fontHeight - 1;
            int halfDistanceTextWidth = (int) Math.ceil(distanceTextWidth / 2F);
            int halfDistanceTextHeight = (int) Math.ceil(distanceTextHeight / 2F);
            RGBAColor distanceColor = SWConfig.INSTANCE.distanceBackground;
            drawContext.fill(-halfDistanceTextWidth - padding, -halfDistanceTextHeight - padding, halfDistanceTextWidth + padding, halfDistanceTextHeight + padding, distanceColor.toInt());
            drawContext.drawText(textRenderer, distanceLabel, -halfDistanceTextWidth, -halfDistanceTextHeight, textColor.toInt(), false);
        }
        drawContext.getMatrices().popMatrix();
    }

    private static boolean compareDimension(SWDimension dimension, DimensionEffects.SkyType currentDimension)
    {
        return (dimension == SWDimension.OVERWORLD && currentDimension.equals(DimensionEffects.SkyType.NORMAL)) ||
               (dimension == SWDimension.NETHER && currentDimension.equals(DimensionEffects.SkyType.NONE)) ||
               (dimension == SWDimension.END && currentDimension.equals(DimensionEffects.SkyType.END));
    }

}