package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.SeleneseTestCase;

public class OryxSeleneseTestCase extends SeleneseTestCase {
	protected ExtCommands ext;
	protected HttpCommandProcessor proc;
	
	public OryxSeleneseTestCase(){
		super();
	}
	
	public void setUp() throws Exception {
		this.setupSelenium();
		this.setupDatabaseConnection();
	}
	
    public void tearDown() throws Exception {
    	// Copied from SeleneTestCase#tearDown
    	try {
    		checkForVerificationErrors();
    	} finally {
    		selenium.stop();
    	}
    }
	
	private void setupDatabaseConnection() {
		SessionFactory sessionFactory;
		
        try {
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		//TODO close connection in teardown!!
		//TODO connection singleton???
	}

	public void setupSelenium(){
		proc = SeleniumConnection.getInstance().getProc();
		selenium = SeleniumConnection.getInstance().getSelenium();
		selenium.start();
        selenium.setContext(this.getClass().getSimpleName() + "." + getName());
        this.ext = new ExtCommands(this.proc, selenium);
	}
	
	public void createModelRepository(String fileName, boolean publicUser){
		// "Create New Model" button
		ext.clickButtonByImage("shape_square_add.png");
		// Model type under "Create New Model" button
		selenium.click("xpath=//img[contains(@src, 'bpmn1.1.png')]/ancestor::a");
		if(publicUser){
			selenium.click("xpath=//em/button[contains(text(), 'Yes')]");
		}
		this.createModelEditor(fileName);
	}
	
	public void createModelEditor(String fileName){
		ext.clickButtonByImage("erdf_export_icon.png");
	}
}
