package streamServerTestClient.Utils;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.springframework.stereotype.Component;

import streamServerTestClient.Models.CheckParameters;

@Component
public class PackageProvider implements IPackageProvider {
	
	private byte[] buffer = null;
	private long counter = 0;
	
	private int loosesPackets = 0;
	
	private long previousCounter = 0;
	
	public void cleanCheck()
	{
		counter = 0;
		previousCounter = 0;
		loosesPackets = 0;
	}

	public byte[] build(CheckParameters parameters) {
		
		buffer = new byte[parameters.getMtu()];
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.putShort((short)0xFACE);
		byteBuffer.putInt(2,parameters.getMtu());
		byteBuffer.putLong(6,counter);
		counter++;
		
		Checksum crc = new CRC32();
		crc.update(buffer, 2, buffer.length-6);
		
		int crc32 = (int)crc.getValue();		
		byteBuffer.putInt(buffer.length - 4,crc32);
		
		return buffer;
	}

	public boolean check(CheckParameters parameters, byte[] data) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		
		if (data.length != byteBuffer.getInt(2))
			return false;
		
		Checksum crc = new CRC32();
		crc.update(data, 2 ,data.length-6);
		int crc32 = (int)crc.getValue();
		int packetCrc = byteBuffer.getInt(data.length - 4);
		if (crc32 != packetCrc)
			return false;
		
		long counter = byteBuffer.getLong(6);
		
		if (previousCounter +1 != counter && previousCounter !=0 && counter !=0)
		{
			loosesPackets += counter - previousCounter;
			previousCounter = counter;
			return true;
		}
		
		previousCounter = counter;
		
		return true;
	}

	public int getLoosesPackets() {
		return loosesPackets;
	}

	public boolean isRightHeader(byte[] data) {
		if (data == null)
			return false;
		
		if (data.length == 0)
			return false;
		
		return ByteBuffer.wrap(data).getShort() == (short) 0xcefa;
	}

}
