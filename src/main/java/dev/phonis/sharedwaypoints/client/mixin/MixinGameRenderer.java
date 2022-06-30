package dev.phonis.sharedwaypoints.client.mixin;

import dev.phonis.sharedwaypoints.client.config.SWConfig;
import dev.phonis.sharedwaypoints.client.render.WaypointRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract
class MixinGameRenderer
{

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
                     opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void onWorldRender(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo ci)
    {
        // Clear any hudRenderTasks from last tick.
        WaypointRenderer.hudRenderTasks.clear();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient.world == null)
        {
            return;
        }

        DimensionEffects.SkyType currentDimension = minecraftClient.world.getDimensionEffects().getSkyType();

        if (SWConfig.INSTANCE.renderWaypoints)
        {
            WaypointRenderer.renderWaypoints(matrixStack, currentDimension);
        }
    }

}
