package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class SWUnsupported implements SWPacket
{

	public final int protocolVersion;

	public
	SWUnsupported(int protocolVersion)
	{
		this.protocolVersion = protocolVersion;
	}

	@Override
	public
	byte getID()
	{
		return Packets.In.SWUnsupportedID;
	}

	@Override
	public
	void toBytes(DataOutputStream dos) throws IOException
	{
		dos.writeInt(this.protocolVersion);
	}

	public static
	SWUnsupported fromBytes(DataInputStream dis) throws IOException
	{
		return new SWUnsupported(dis.readInt());
	}

}
