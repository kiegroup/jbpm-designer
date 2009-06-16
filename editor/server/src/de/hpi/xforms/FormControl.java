package de.hpi.xforms;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public abstract class FormControl extends XFormsUIElement implements ActionContainer, UICommonContainer, LabelContainer {
	
	protected Help help;
	protected Hint hint;
	protected Alert alert;
	protected List<AbstractAction> actions;
	protected Label label;
	
	public FormControl() {
		super();
		attributes.put("ref", null);
		attributes.put("bind", null);
		attributes.put("default", null);
	}

	public Help getHelp() {
		return help;
	}

	public void setHelp(Help help) {
		this.help = help;
	}

	public Hint getHint() {
		return hint;
	}

	public void setHint(Hint hint) {
		this.hint = hint;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

	public List<AbstractAction> getActions() {
		if(actions==null)
			actions = new ArrayList<AbstractAction>();
		return actions;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

}
