package dev.phonis.sharedwaypoints.client.mixin;

import dev.phonis.sharedwaypoints.client.render.WaypointRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract
class MixinHudRender extends DrawableHelper
{

    @Inject(method = "render", at = @At("RETURN"))
    void onHudRender(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        WaypointRenderer.hudRenderTasks.forEach(Runnable::run);
    }

}
