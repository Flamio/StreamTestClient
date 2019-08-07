package streamServerTestClient.views;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.springframework.stereotype.Component;

import streamServerTestClient.Models.CheckParameters;

@Component
public class MainDialog extends JFrame implements IMainView {

	JButton beginCheckButton = new JButton("Начать проверку");
	JButton clearButton = new JButton("Очистить лог");
	
	JTextField address = new JTextField("192.168.123.25",10);
	JTextField mtu = new JTextField("1500",10);
	JTextField port = new JTextField("5001",10);
	JTextField fps = new JTextField("100",10);

	JLabel addressLabel = new JLabel("Адрес");
	JLabel portLabel = new JLabel("Порт");
	JLabel mtuLabel = new JLabel("Размер пакета");
	JLabel fpsLabel = new JLabel("<html>Частота отправки <br/> пакетов в секунду</html>");
	JTextArea textArea = new JTextArea(10,10);
	
	JScrollPane scroll;

	IMainViewListener listener;

	private boolean isRunning = false;

	public MainDialog()
	{
		Container pane = getContentPane();
		pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT); 

		GridBagLayout layout = new GridBagLayout();		
		pane.setLayout(layout);


		pane.add(addressLabel, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(15,15,0,0), 0,0));
		pane.add(portLabel, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(15,15,0,0), 0,0));
		pane.add(mtuLabel, new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(15,15,0,0), 0,0));
		pane.add(fpsLabel, new GridBagConstraints(3,0,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE,new Insets(15,15,0,10), 0,0));

		pane.add(address, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(5,15,0,0), 0,0));
		pane.add(port, new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(5,15,0,0), 0,0));
		pane.add(mtu, new GridBagConstraints(2,1,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(5,15,0,0), 0,0));
		pane.add(fps, new GridBagConstraints(3,1,1,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(5,15,0,10), 0,0));
		
		pane.add(beginCheckButton, new GridBagConstraints(1,2,2,1,1,0,GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 10, 5), 0,0));
		
		pane.add(clearButton, new GridBagConstraints(2,2,2,1,1,0,GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(10, 5, 10, 5), 0,0));

		textArea.setEditable(false);
		scroll = new JScrollPane ( textArea );
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );		
		pane.add(scroll, new GridBagConstraints(0,3,4,5,1,0.9,GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0,0));


		setSize(640, 480);
		beginCheckButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (listener == null)
					return;

				if (isRunning)
				{
					beginCheckButton.setText("Начать проверку");
					listener.stop();
				}
				else
				{
					CheckParameters parameters = new CheckParameters();
					parameters.setFps(Integer.parseInt(fps.getText()));
					parameters.setMtu(Integer.parseInt(mtu.getText()));
					parameters.setPort(Integer.parseInt(port.getText()));
					parameters.setAddress(address.getText());
					listener.startCheck(parameters);
					beginCheckButton.setText("Стоп");
				}
				isRunning = !isRunning;
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setListener(IMainViewListener listener) {
		this.listener = listener;
	}

	public void setLogString(String log) {
		textArea.append(log + "\n");
		JScrollBar bar = scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
		
	}
}

