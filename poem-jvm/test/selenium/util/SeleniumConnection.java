package util;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

public class SeleniumConnection {
    private static SeleniumConnection instance = new SeleniumConnection();

	protected HttpCommandProcessor proc;
	protected Selenium selenium;
    
    private SeleniumConnection() {
		String seleniumServerHost = "localhost";
		int seleniumServerPort = 4444;
		String browser = "*chrome";
		String url = "http://localhost:8080";

        this.proc = new HttpCommandProcessor(seleniumServerHost, seleniumServerPort, browser, url);
        selenium = new DefaultSelenium(this.proc);
        //selenium.start();
    }

    public static SeleniumConnection getInstance() {
        return instance;
    }

	public HttpCommandProcessor getProc() {
		return proc;
	}

	public Selenium getSelenium() {
		return selenium;
	}

}
