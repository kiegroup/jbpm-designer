package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public abstract class AbstractAction extends XFormsElement {
	
	public AbstractAction() {
		super();
		attributes.put("ifExpression", null);
		attributes.put("whileExpression", null);
		attributes.put("ev:event", null);
	}
	
}
