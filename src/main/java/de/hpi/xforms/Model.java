package de.hpi.xforms;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Model extends XFormsElement implements ActionContainer {
	
	protected Instance instance;
	protected List<Submission> submissions;
	protected List<AbstractAction> actions;
	protected List<Bind> binds;
	
	public Instance getInstance() {
		return instance;
	}
	
	public void setInstance(Instance instance) {
		this.instance = instance;
	}
	
	public List<Submission> getSubmissions() {
		if(submissions==null)
			submissions = new ArrayList<Submission>();
		return submissions;
	}
	
	public List<AbstractAction> getActions() {
		if(actions==null)
			actions = new ArrayList<AbstractAction>();
		return actions;
	}
	
	public List<Bind> getBinds() {
		if(binds==null)
			binds = new ArrayList<Bind>();
		return binds;
	}
	
	@Override
	public String getTagName() {
		return "model";
	}

}
