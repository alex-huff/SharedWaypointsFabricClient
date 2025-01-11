package dev.phonis.sharedwaypoints.client.mixin;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.render.WaypointRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{

    @Shadow
    public abstract MinecraftClient getClient();

    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(
        at = @At(
            value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void onWorldRender(RenderTickCounter renderTickCounter, CallbackInfo ci)
    {
        // Clear any hudRenderTasks from last tick.
        WaypointRenderer.hudRenderTasks.clear();

        if (this.client.world == null)
        {
            return;
        }

        DimensionEffects.SkyType currentDimension = this.client.world.getDimensionEffects().getSkyType();

        if (SWConfig.INSTANCE.renderWaypoints)
        {
            WaypointRenderer.renderWaypoints(currentDimension);
        }
    }

    @Inject(
        at = @At(
            value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderFloatingItem(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    void onHudRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, Profiler profiler, boolean bl, int i,
                     int j, Window window, Matrix4f matrix4f, Matrix4fStack matrix4fStack, DrawContext drawContext)
    {
        WaypointRenderer.hudRenderTasks.forEach(consumer -> consumer.accept(drawContext));
    }

}
