package dev.phonis.sharedwaypoints.client.math;

import net.minecraft.util.math.Vec3d;

public
interface Projector
{

    Vec3d project(float x, float y, float z, int[] viewport);

}
