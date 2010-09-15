package de.hpi.diagram;

public class OryxUUID {
	public static String generate(){
		return "_" + java.util.UUID.randomUUID().toString();
	}
}