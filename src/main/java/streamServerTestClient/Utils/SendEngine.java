package streamServerTestClient.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import streamServerTestClient.Models.CheckParameters;

@Component
public class SendEngine implements ISendEngine {

	private byte[] buffer = new byte[1024*1024]; 
	private INetErrorsListener errorsListener;
	private DatagramSocket socket;
	private DatagramPacket bufferPacket;
	private volatile boolean isInit = false;

	public SendEngine() {
		super();
		bufferPacket = new DatagramPacket(buffer, buffer.length);
	}

	public void send(CheckParameters parameters, byte[] data)
	{
		if (!isInit)
			return;
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName(parameters.getAddress());
		} catch (UnknownHostException e) {

			if (errorsListener != null) errorsListener.error("Неизвестный хост"); 
		}
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, inetAddress, parameters.getPort());
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			if (errorsListener != null) errorsListener.error("Ошибка отправки");
		}
	}

	public byte[] receive() 
	{
		if (!isInit)
			return null;
		try {
			socket.receive(bufferPacket);
		} catch (IOException e) {
			if (errorsListener != null) errorsListener.error("Ошибка получения");
		}		

		byte[] result = new byte[bufferPacket.getLength()];
		ByteBuffer.wrap(buffer).get(result, 0, bufferPacket.getLength());
		return  result;
	}

	public void setErrorsListener(INetErrorsListener errorsListener) {
		this.errorsListener = errorsListener;
	}

	public void init() {
		
		try 
		{
			socket = new DatagramSocket();
			isInit = true;
		} 
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
				
	}

	public void stop() {
		socket.close();
	}

}
