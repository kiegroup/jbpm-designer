package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Message extends AbstractAction implements PCDataContainer {
	
	protected String content;

	public Message() {
		super();
		attributes.put("ref", null);
		attributes.put("level", null);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String getStencilId() {
		return "Message";
	}
	
	@Override
	public String getTagName() {
		return "message";
	}

}
