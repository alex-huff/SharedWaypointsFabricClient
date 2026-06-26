package dev.phonis.sharedwaypoints.client.mixin;

import dev.phonis.sharedwaypoints.client.math.Projector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static org.joml.Math.fma;

@Mixin(Matrix4f.class)
public abstract class MixinMatrix4f implements Projector
{

    @Shadow
    float m00;
    @Shadow
    float m01;
    @Shadow
    float m02;
    @Shadow
    float m03;
    @Shadow
    float m10;
    @Shadow
    float m11;
    @Shadow
    float m12;
    @Shadow
    float m13;
    @Shadow
    float m20;
    @Shadow
    float m21;
    @Shadow
    float m22;
    @Shadow
    float m23;
    @Shadow
    float m30;
    @Shadow
    float m31;
    @Shadow
    float m32;
    @Shadow
    float m33;

    @Override
    public Vector3f projectNoZDivide(float x, float y, float z, int[] viewport, Vector3f winCoordsDest)
    {
        float invW = 1.0f / fma(this.m03, x, fma(this.m13, y, fma(this.m23, z, this.m33)));
        float nx = fma(this.m00, x, fma(this.m10, y, fma(this.m20, z, this.m30))) * invW;
        float ny = fma(this.m01, x, fma(this.m11, y, fma(this.m21, z, this.m31))) * invW;
        float nz = fma(this.m02, x, fma(this.m12, y, fma(this.m22, z, this.m32)));
        winCoordsDest.x = fma(fma(nx, 0.5f, 0.5f), viewport[2], viewport[0]);
        winCoordsDest.y = fma(fma(ny, 0.5f, 0.5f), viewport[3], viewport[1]);
        winCoordsDest.z = nz;
        return winCoordsDest;
    }

}