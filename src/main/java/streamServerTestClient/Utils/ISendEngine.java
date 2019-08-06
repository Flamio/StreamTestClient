package streamServerTestClient.Utils;

import streamServerTestClient.Models.CheckParameters;

public interface ISendEngine {
	void send(CheckParameters parameters, byte[] data);
	byte[] receive();
	void init();
	void setErrorsListener(INetErrorsListener errorsListener);
	void stop();
}
