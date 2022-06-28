package dev.phonis.sharedwaypoints.client.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public
class SWRegister implements SWPacket
{

	public final int protocolVersion;

	public
	SWRegister(int protocolVersion)
	{
		this.protocolVersion = protocolVersion;
	}

	@Override
	public
	byte getID()
	{
		return Packets.Out.SWRegisterID;
	}

	@Override
	public
	void toBytes(DataOutputStream dos) throws IOException
	{
		dos.writeInt(this.protocolVersion);
	}

	public static
	SWRegister fromBytes(DataInputStream dis) throws IOException
	{
		return new SWRegister(dis.readInt());
	}

}
