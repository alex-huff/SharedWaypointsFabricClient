package dev.phonis.sharedwaypoints.client.math;

import org.joml.Vector3f;

public interface Projector
{

    Vector3f projectNoZDivide(float x, float y, float z, int[] viewport, Vector3f winCoordsDest);

}
