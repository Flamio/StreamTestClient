package streamServerTestClient.Presenters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import streamServerTestClient.Models.CheckParameters;
import streamServerTestClient.Utils.IPackageBuilder;
import streamServerTestClient.Utils.ISendEngine;
import streamServerTestClient.views.IMainView;
import streamServerTestClient.views.IMainViewListener;

@Component
public class MainPresenter {

	@Autowired
	private IMainView mainView;

	@Autowired
	private ISendEngine sendEngine;

	@Autowired
	private IPackageBuilder packageBuilder;

	private Thread senderThread;
	private Thread listenThread;
	private CheckParameters checkParameters;
	private volatile boolean isFinish;

	private static void burn(long nanos) {
		long deadline = System.nanoTime()+nanos;
		while(System.nanoTime()<deadline){};
	}

	public void init()
	{	
		mainView.setListener(new IMainViewListener() {

			public void startCheck(CheckParameters parameters) {
				checkParameters = parameters;
				senderThread = new Thread(new Runnable() {
					public void run() 
					{
						sendEngine.init();
						isFinish = false;
						while (!isFinish)
						{
							byte[] packet = packageBuilder.build(checkParameters);
							sendEngine.send(checkParameters, packet);

							float t = 1000.0f / checkParameters.getFps();
							burn((long)(t * 1000000));
						}
					}
				});

				senderThread.start();

				listenThread = new Thread(new Runnable() {

					public void run() 
					{
						isFinish = false;
						while (!isFinish)
						{
							byte[] receivedPacket = sendEngine.receive();
							boolean isRightPacket = packageBuilder.check(checkParameters, receivedPacket);
						}
					}
				});

				listenThread.start();
			}

			public void stop() {
				isFinish = true;
				sendEngine.stop();
			}
		});


	}
}
