package de.hpi.xforms;

import java.util.Comparator;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class ListUICommonComparator implements Comparator<ListUICommon> {

	public int compare(ListUICommon element1, ListUICommon element2) {
		return (element1.getYPosition() - element2.getYPosition());
	}

}
