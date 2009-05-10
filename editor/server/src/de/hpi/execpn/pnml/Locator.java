package de.hpi.execpn.pnml;

public class Locator {
	private String name;
	private String datatype;
	private String xpath;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public Locator(String name, String datatype, String xpath) {
		this.name = name;
		this.datatype = datatype;
		this.xpath = xpath;
	}
	
}
