package dev.phonis.sharedwaypoints.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{

    @Shadow
    public abstract MinecraftClient getClient();

    @Final
    @Shadow
    private MinecraftClient client;

// USING MALILIB FOR THIS NOW, might switch back later
//    @Inject(
//        at = @At(
//            value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
//    void onWorldRender(RenderTickCounter renderTickCounter, CallbackInfo ci)
//    {
//        // Clear any hudRenderTasks from last tick.
//        WaypointRenderer.hudRenderTasks.clear();

//        if (this.client.world == null)
//        {
//            return;
//        }

//        DimensionEffects.SkyType currentDimension = this.client.world.getDimensionEffects().getSkyType();

//        if (SWConfig.INSTANCE.renderWaypoints)
//        {
//            WaypointRenderer.renderWaypoints(currentDimension);
//        }
//    }

//    @Inject(
//        at = @At(
//            value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderFloatingItem(Lnet/minecraft/client/gui/DrawContext;F)V", shift = At.Shift.AFTER), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
//    void onHudRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, Profiler profiler, boolean bl, int i,
//                     int j, Window window, Matrix4f matrix4f, Matrix4fStack matrix4fStack, DrawContext drawContext)
//    {
//        WaypointRenderer.hudRenderTasks.forEach(consumer -> consumer.accept(drawContext));
//    }

}
