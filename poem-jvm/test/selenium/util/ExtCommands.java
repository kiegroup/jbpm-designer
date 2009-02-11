package util;

import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

public class ExtCommands {
	HttpCommandProcessor commandProc;
	Selenium selenium;
	
	public ExtCommands(HttpCommandProcessor commandProc, Selenium selenium){
		this.commandProc = commandProc;
		this.selenium = selenium;
	}
	
	public void clickButtonByImage(String image){
		selenium.click("css=button[style*='"+image+"']");
	}
}