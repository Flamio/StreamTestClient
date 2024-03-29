package streamServerTestClient.Presenters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import streamServerTestClient.Models.CheckParameters;
import streamServerTestClient.Utils.IPackageProvider;
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
	private IPackageProvider packageBuilder;

	private Thread senderThread;
	private Thread listenThread;
	private Thread connectionWatchThread;
	private CheckParameters checkParameters;
	private volatile boolean isFinish;
	private volatile int fps = 0;
	private volatile int packetCouneter = 0;
	private volatile int errors = 0;
	private volatile int loosesPackets = 0;

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
						errors = 0;
						fps = 0;
						packetCouneter = 0;
						packageBuilder.cleanCheck();
						isFinish = false;
						mainView.setLogString("Начало проверки");
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
							if (!packageBuilder.isRightHeader(receivedPacket))
								continue;
							packetCouneter++;
							boolean isRightPacket = packageBuilder.check(checkParameters, receivedPacket);
							if (!isRightPacket)
								errors++;
							loosesPackets = packageBuilder.getLoosesPackets();
						}
					}
				});

				listenThread.start();

				connectionWatchThread = new Thread(new Runnable() {

					public void run() {
						isFinish = false;
						try 
						{
							Thread.sleep(1000);
						} 
						catch (InterruptedException e1) {

							e1.printStackTrace();
						}
						while (!isFinish)
						{
							fps = packetCouneter/2;

							if (fps == 0)
								mainView.setLogString("Нет подключения к серверу...");
							else
								mainView.setLogString(String.format("Подключено, fps = %d, ошибки в пакетах = %d, кол-во потерянных пакетов = %d"
										, fps, errors, loosesPackets));

							packetCouneter=0;

							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});

				connectionWatchThread.start();
			}

			public void stop() {
				isFinish = true;
				sendEngine.stop();
				mainView.setLogString("Стоп");
			}
		});


	}
}
