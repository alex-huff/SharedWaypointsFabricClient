package dev.phonis.sharedwaypoints.client.mixin.math;

import dev.phonis.sharedwaypoints.client.math.Projector;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract
class MixinMatrix4f implements Projector
{

    @Shadow
    protected float m00;
    @Shadow
    protected float m01;
    @Shadow
    protected float m02;
    @Shadow
    protected float m03;
    @Shadow
    protected float m10;
    @Shadow
    protected float m11;
    @Shadow
    protected float m12;
    @Shadow
    protected float m13;
    @Shadow
    protected float m20;
    @Shadow
    protected float m21;
    @Shadow
    protected float m22;
    @Shadow
    protected float m23;
    @Shadow
    protected float m30;
    @Shadow
    protected float m31;
    @Shadow
    protected float m32;
    @Shadow
    protected float m33;

    @Override
    public
    Vec3d projectNonClampZ(float x, float y, float z, int[] viewport)
    {
        float inverseW = 1F / Math.fma(this.m03, x, Math.fma(this.m13, y, Math.fma(this.m23, z, this.m33)));
        float nx       = Math.fma(this.m00, x, Math.fma(this.m10, y, Math.fma(this.m20, z, this.m30))) * inverseW;
        float ny       = Math.fma(this.m01, x, Math.fma(this.m11, y, Math.fma(this.m21, z, this.m31))) * inverseW;
        float nz       = Math.fma(this.m02, x, Math.fma(this.m12, y, Math.fma(this.m22, z, this.m32)));
        return new Vec3d(Math.fma(Math.fma(nx, 0.5F, 0.5F), viewport[2], viewport[0]),
            Math.fma(Math.fma(ny, 0.5F, 0.5F), viewport[3], viewport[1]), nz);
    }

}