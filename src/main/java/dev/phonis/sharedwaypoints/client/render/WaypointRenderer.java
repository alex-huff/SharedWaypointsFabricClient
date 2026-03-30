package dev.phonis.sharedwaypoints.client.render;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.math.Projector;
import dev.phonis.sharedwaypoints.client.networking.SWDimension;
import dev.phonis.sharedwaypoints.client.networking.SWWaypoint;
import dev.phonis.sharedwaypoints.client.state.SWStateManager;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WaypointRenderer
{

    public static boolean coordinateOnScreen(Vec3 position)
    {
        return position != null && position.z >= 0;
    }

    public static Vec3 worldSpaceToScreenSpace(Vec3 position, Matrix4f projectionMatrix, Matrix4f positionMatrix, Matrix4f modelViewMatrix, Camera camera)
    {
        Minecraft minecraftClient = Minecraft.getInstance();
        double dx = position.x - camera.getPosition().x;
        double dy = position.y - camera.getPosition().y;
        double dz = position.z - camera.getPosition().z;
        Vector4f cameraDirection = new Vector4f((float) dx, (float) dy, (float) dz, 1F);
        cameraDirection.mul(positionMatrix);
        int[] viewport = new int[]{ 0, 0, minecraftClient.getWindow().getWidth(), minecraftClient.getWindow().getHeight() };
        projectionMatrix = new Matrix4f(projectionMatrix);
        projectionMatrix.mul(modelViewMatrix);
        Vec3 screenCoords
            = ((Projector) projectionMatrix).projectNonClampZ(cameraDirection.x(), cameraDirection.y(), cameraDirection.z(), viewport);
        int displayHeight = minecraftClient.getWindow().getHeight();
        double scaleFactor = minecraftClient.getWindow().getGuiScale();
        return new Vec3(screenCoords.x / scaleFactor, (displayHeight - screenCoords.y) / scaleFactor, screenCoords.z);
    }

    private record RenderContext3D(Vec3 realLocation, Vec3 screenCoordinates, SWWaypoint waypoint)
    {

    }

    private record RenderContext2D(int distance, Vec2 pixelCoordinates, SWWaypoint waypoint)
    {

    }

    public static final List<Consumer<GuiGraphics>> hudRenderTasks = new ArrayList<>();

    private static boolean shouldRender(SWWaypoint swWaypoint, DimensionSpecialEffects.SkyType currentDimension)
    {
        return (WaypointRenderer.compareDimension(swWaypoint.location.dimension, currentDimension)) ||
               (SWConfig.INSTANCE.crossDimensionalWaypoints &&
                (swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                 currentDimension.equals(DimensionSpecialEffects.SkyType.NONE)));
    }

    public static void renderWaypoints(DimensionSpecialEffects.SkyType currentDimension, Matrix4f projectionMatrix, Matrix4f positionMatrix, Matrix4f modelViewMatrix, Camera camera)
    {
        Minecraft minecraftClient = Minecraft.getInstance();
        int screenWidth = minecraftClient.getWindow().getGuiScaledWidth();
        int screenHeight = minecraftClient.getWindow().getGuiScaledHeight();
        Vec2 screenMiddle = new Vec2(screenWidth / 2F, screenHeight / 2F);
        final List<WaypointRenderer.RenderContext2D> toRender = new ArrayList<>();
        SWStateManager.INSTANCE.withWaypoints((waypointState) -> waypointState.stream()
            .filter(waypoint -> WaypointRenderer.shouldRender(waypoint, currentDimension)).map(swWaypoint ->
            {
                boolean adjusted = swWaypoint.location.dimension == SWDimension.OVERWORLD &&
                                   currentDimension.equals(DimensionSpecialEffects.SkyType.NONE);
                Vec3 adjustedLocation = new Vec3(
                    adjusted ? swWaypoint.location.x / 8d : swWaypoint.location.x,
                    adjusted ? 128d : swWaypoint.location.y,
                    adjusted ? swWaypoint.location.z / 8d : swWaypoint.location.z);
                Vec3 pixelCoordinates
                    = WaypointRenderer.worldSpaceToScreenSpace(new Vec3(adjustedLocation.x, adjustedLocation.y, adjustedLocation.z), projectionMatrix, positionMatrix, modelViewMatrix, camera);
                return new WaypointRenderer.RenderContext3D(adjustedLocation, pixelCoordinates, swWaypoint);
            }).filter(renderContext3D -> WaypointRenderer.coordinateOnScreen(renderContext3D.screenCoordinates))
            .map(renderContext3D ->
            {
                Vec2 pixelCoordinates
                    = new Vec2((float) renderContext3D.screenCoordinates().x, (float) renderContext3D.screenCoordinates().y);
                int distance = (int) renderContext3D.realLocation()
                    .distanceTo(minecraftClient.gameRenderer.getMainCamera().getPosition());
                return new WaypointRenderer.RenderContext2D(distance, pixelCoordinates, renderContext3D.waypoint());
            }).forEach(toRender::add));
        if (toRender.isEmpty())
        {
            return;
        }
        toRender.sort(Comparator.comparing(renderContext2D -> renderContext2D.pixelCoordinates()
            .distanceToSqr(screenMiddle), Comparator.reverseOrder()));
        final WaypointRenderer.RenderContext2D closestWaypoint = toRender.getLast();
        WaypointRenderer.hudRenderTasks.add((drawContext) ->
        {
            SWStateManager.INSTANCE.setHoveredWaypoint(closestWaypoint.waypoint().name);
            IntStream.range(0, toRender.size() - 1)
                .forEach(i -> WaypointRenderer.drawWaypoint(drawContext, toRender.get(i), false));
            WaypointRenderer.drawWaypoint(drawContext, closestWaypoint, SWConfig.INSTANCE.highlightClosest);
        });
    }

    private static void drawWaypoint(GuiGraphics drawContext, RenderContext2D toRender, boolean highlighted)
    {
        Minecraft minecraftClient = Minecraft.getInstance();
        Vec2 position = toRender.pixelCoordinates();
        SWWaypoint waypoint = toRender.waypoint();
        Font textRenderer = minecraftClient.font;
        String waypointLabel = (SWConfig.INSTANCE.fullWaypointNames || highlighted) ? waypoint.name
                                                                                    : waypoint.name.substring(0, 1)
                                   .toUpperCase(Locale.ROOT);
        float scale = SWConfig.INSTANCE.renderScale / 100F;
        // -1 on width and height to ignore shadow since it will not be used
        int waypointTextWidth = textRenderer.width(waypointLabel) - 1;
        int waypointTextHeight = textRenderer.lineHeight - 1;
        int halfWaypointTextWidth = Math.round(waypointTextWidth / 2F);
        int halfWaypointTextHeight = Math.round(waypointTextHeight / 2F);
        int padding = Math.round(waypointTextHeight * .2F);
        RGBAColor textColor = SWConfig.INSTANCE.textColor;
        RGBAColor waypointColor = highlighted ? SWConfig.INSTANCE.fullBackground : SWConfig.INSTANCE.plateBackground;
        drawContext.pose().pushMatrix();
        drawContext.pose().translate(position.x, position.y);
        drawContext.pose().scale(scale, scale);
        drawContext.fill(-halfWaypointTextWidth - padding, -halfWaypointTextHeight - padding, halfWaypointTextWidth + padding, halfWaypointTextHeight + padding, waypointColor.toInt());
        drawContext.drawString(textRenderer, waypointLabel, -halfWaypointTextWidth, -halfWaypointTextHeight, textColor.toInt(), false);
        if (highlighted)
        {
            drawContext.pose().translate(0, waypointTextHeight + padding * 2);
            String distanceLabel = toRender.distance() + "m";
            int distanceTextWidth = textRenderer.width(distanceLabel) - 1;
            int distanceTextHeight = textRenderer.lineHeight - 1;
            int halfDistanceTextWidth = (int) Math.ceil(distanceTextWidth / 2F);
            int halfDistanceTextHeight = (int) Math.ceil(distanceTextHeight / 2F);
            RGBAColor distanceColor = SWConfig.INSTANCE.distanceBackground;
            drawContext.fill(-halfDistanceTextWidth - padding, -halfDistanceTextHeight - padding, halfDistanceTextWidth + padding, halfDistanceTextHeight + padding, distanceColor.toInt());
            drawContext.drawString(textRenderer, distanceLabel, -halfDistanceTextWidth, -halfDistanceTextHeight, textColor.toInt(), false);
        }
        drawContext.pose().popMatrix();
    }

    private static boolean compareDimension(SWDimension dimension, DimensionSpecialEffects.SkyType currentDimension)
    {
        return (dimension == SWDimension.OVERWORLD && currentDimension.equals(DimensionSpecialEffects.SkyType.OVERWORLD)) ||
               (dimension == SWDimension.NETHER && currentDimension.equals(DimensionSpecialEffects.SkyType.NONE)) ||
               (dimension == SWDimension.END && currentDimension.equals(DimensionSpecialEffects.SkyType.END));
    }

}