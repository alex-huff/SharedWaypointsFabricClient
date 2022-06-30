package dev.phonis.sharedwaypoints.client.mixin.math;

import dev.phonis.sharedwaypoints.client.math.Projector;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract
class MixinMatrix4f implements Projector
{

    @Shadow
    protected float a00;
    @Shadow
    protected float a01;
    @Shadow
    protected float a02;
    @Shadow
    protected float a03;
    @Shadow
    protected float a10;
    @Shadow
    protected float a11;
    @Shadow
    protected float a12;
    @Shadow
    protected float a13;
    @Shadow
    protected float a20;
    @Shadow
    protected float a21;
    @Shadow
    protected float a22;
    @Shadow
    protected float a23;
    @Shadow
    protected float a30;
    @Shadow
    protected float a31;
    @Shadow
    protected float a32;
    @Shadow
    protected float a33;

    @Override
    public
    Vec3d project(float x, float y, float z, int[] viewport)
    {
        float inverseW = 1F / Math.fma(this.a03, x, Math.fma(this.a13, y, Math.fma(this.a23, z, this.a33)));
        float nx       = Math.fma(this.a00, x, Math.fma(this.a10, y, Math.fma(this.a20, z, this.a30))) * inverseW;
        float ny       = Math.fma(this.a01, x, Math.fma(this.a11, y, Math.fma(this.a21, z, this.a31))) * inverseW;
        float nz       = Math.fma(this.a02, x, Math.fma(this.a12, y, Math.fma(this.a22, z, this.a32)));
        return new Vec3d(Math.fma(Math.fma(nx, 0.5F, 0.5F), viewport[2], viewport[0]),
            Math.fma(Math.fma(ny, 0.5F, 0.5F), viewport[3], viewport[1]), nz);
    }

}
