package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Alert extends UICommon implements PCDataContainer {
	
	protected String content;
	
	public Alert() {
		super();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String getStencilId() {
		return "Alert";
	}
	
	@Override
	public String getTagName() {
		return "alert";
	}

}
