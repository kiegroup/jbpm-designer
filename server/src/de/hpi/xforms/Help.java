package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Help extends UICommon implements PCDataContainer {
	
	protected String content;

	public Help() {
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
		return "Help";
	}
	
	@Override
	public String getTagName() {
		return "help";
	}

}
