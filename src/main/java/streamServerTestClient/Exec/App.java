package streamServerTestClient.Exec;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import streamServerTestClient.Presenters.MainPresenter;
import streamServerTestClient.views.MainDialog;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException 
	{

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		MainPresenter presenter = context.getBean("mainPresenter", MainPresenter.class);
		presenter.init();
		context.close();
	}
}
