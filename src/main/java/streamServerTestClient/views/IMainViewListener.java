package streamServerTestClient.views;

import streamServerTestClient.Models.CheckParameters;

public interface IMainViewListener {
	void startCheck(CheckParameters parameters);
	void stop();
}
