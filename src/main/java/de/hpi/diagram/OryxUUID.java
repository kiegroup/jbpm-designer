package de.hpi.diagram;

public class OryxUUID {
	public static String generate(){
		return "oryx_" + java.util.UUID.randomUUID().toString();
	}
}