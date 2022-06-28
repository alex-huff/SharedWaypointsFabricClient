package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataOutputStream;
import java.io.IOException;

public
interface SWSerializable
{

    void toBytes(DataOutputStream dos) throws IOException;

}
