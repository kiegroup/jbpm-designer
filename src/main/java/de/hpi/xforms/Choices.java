package de.hpi.xforms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Choices extends ListUICommon implements ListUICommonContainer, LabelContainer {
	
	protected List<ListUICommon> listUICommons;
	protected Label label;

	public Choices() {
		super();
	}
	
	public List<ListUICommon> getListUICommons() {
		if(listUICommons==null)
			listUICommons = new ArrayList<ListUICommon>();
		Collections.sort(listUICommons, new ListUICommonComparator());
		return listUICommons;
	}
	
	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}
	
	@Override
	public String getStencilId() {
		return "Choices";
	}

	@Override
	public String getTagName() {
		return "choices";
	}

}
