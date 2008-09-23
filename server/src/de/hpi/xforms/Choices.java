package de.hpi.xforms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Choices extends ListUICommon implements ListUICommonContainer {
	
	protected List<ListUICommon> listUICommons;

	public Choices() {
		super();
	}
	
	public List<ListUICommon> getListUICommons() {
		if(listUICommons==null)
			listUICommons = new ArrayList<ListUICommon>();
		Collections.sort(listUICommons, new ListUICommonComparator());
		return listUICommons;
	}

	@Override
	public String getTagName() {
		return "choices";
	}

}
