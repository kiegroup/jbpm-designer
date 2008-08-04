package de.hpi.xforms;

import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public interface UICommonContainer {
	
	public Help getHelp();
	public void setHelp(Help help);
	
	public Hint getHint();
	public void setHint(Hint hint);
	
	public Alert getAlert();
	public void setAlert(Alert alert);
	
	public List<AbstractAction> getActions();

}
