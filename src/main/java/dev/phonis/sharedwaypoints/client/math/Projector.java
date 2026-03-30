package dev.phonis.sharedwaypoints.client.math;

import net.minecraft.world.phys.Vec3;

public interface Projector
{

    Vec3 projectNonClampZ(float x, float y, float z, int[] viewport);

}
