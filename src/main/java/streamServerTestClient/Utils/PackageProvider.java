package streamServerTestClient.Utils;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import streamServerTestClient.Models.CheckParameters;

@Component
public class PackageProvider implements IPackageProvider {
	
	private byte[] buffer = null;

	public byte[] build(CheckParameters parameters) {

		if (buffer != null && buffer.length == parameters.getMtu())
			return buffer;
		
		buffer = new byte[parameters.getMtu()];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.putShort((short)0xFACE);
		byteBuffer.putInt(2,parameters.getMtu());
		
		return buffer;
	}

	public boolean check(CheckParameters parameters, byte[] data) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		
		if (data.length != byteBuffer.getInt(2))
			return false;
		
		return true;
	}

	public boolean isRightHeader(byte[] data) {
		if (data == null)
			return false;
		
		if (data.length == 0)
			return false;
		
		return ByteBuffer.wrap(data).getShort() == (short) 0xcefa;
	}

}
