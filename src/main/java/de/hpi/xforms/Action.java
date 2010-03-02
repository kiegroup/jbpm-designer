package de.hpi.xforms;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Action extends AbstractAction implements ActionContainer {
	
	protected List<AbstractAction> actions;

	public List<AbstractAction> getActions() {
		if (actions == null)
			actions = new ArrayList<AbstractAction>();
		return actions;
	}
	
	@Override
	public String getStencilId() {
		return "Action";
	}
	
	@Override
	public String getTagName() {
		return "action";
	}

}
