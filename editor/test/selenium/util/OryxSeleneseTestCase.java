package util;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.SeleneseTestCase;

public class OryxSeleneseTestCase extends SeleneseTestCase {
	protected ExtCommands ext;
	protected HttpCommandProcessor proc;
	
	public OryxSeleneseTestCase(){
		super();
	}
	
	public void setUp() throws Exception {
		String seleniumServerHost = "localhost";
		int seleniumServerPort = 4444;
		String browser = "*chrome";
		String url = "http://oryx-editor.org/";

        this.proc = new HttpCommandProcessor(seleniumServerHost, seleniumServerPort, browser, url);
        selenium = new DefaultSelenium(this.proc);
        selenium.start();
        selenium.setContext(this.getClass().getSimpleName() + "." + getName());
        this.ext = new ExtCommands(this.proc);
	}
}
