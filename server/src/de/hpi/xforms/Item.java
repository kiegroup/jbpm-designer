package de.hpi.xforms;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Item extends ListUICommon implements UICommonContainer, LabelContainer {
	
	protected Help help;
	protected Hint hint;
	protected Alert alert;
	protected List<AbstractAction> actions;
	protected Label label;
	protected Value value;

	public Item() {
		super();
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
	
	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
	
	@Override
	public String getStencilId() {
		return "Item";
	}
	
	@Override
	public String getTagName() {
		return "item";
	}

}
