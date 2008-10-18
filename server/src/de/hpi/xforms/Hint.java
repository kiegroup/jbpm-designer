package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Hint extends UICommon implements PCDataContainer {
	
	protected String content;

	public Hint() {
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
		return "Hint";
	}
	
	@Override
	public String getTagName() {
		return "hint";
	}

}
