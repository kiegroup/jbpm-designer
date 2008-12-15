package de.hpi.bpel4chor.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpel4chor.model.activities.Activity;

/**
 * This class provides helper methods for the handling of lists.
 */
public abstract class ListUtil {
	
	/**
	 * Calculates the intersection of the elements in the two lists.
	 * 
	 * @param list1 The first list
	 * @param list2 The second list
	 * 
	 * @return The activities that are contained in both lists. If the lists,
	 * do not contain equals elements, the result is an empty list.
	 */
	public static List<Activity> intersect(
			List<Activity> list1, List<Activity> list2) {
		List<Activity> result = new ArrayList<Activity>();
		for (Iterator<Activity> it = list1.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (list2.contains(act)) {
				result.add(act);
			}
		}
		
		for (Iterator<Activity> it = list2.iterator(); it.hasNext();) {
			Activity act = it.next();
			if (list1.contains(act) && !result.contains(act)) {
				result.add(act);
			}
		}
		return result;
	}
	
	/**
	 * Checks if the given list are equals, this means that they contain the
	 * same elements.
	 * 
	 * @param list1 The first list.
	 * @param list2 The second list.
	 * 
	 * @return True, if both lists contain the same elements, false otherwise.
	 */
	public static boolean isEqual(List list1, List list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		
		for (Iterator it = list1.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (!list2.contains(obj)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Builds a string containing the names of each list element separated
	 * by comma.
	 * 
	 * @param list The list containing the elements.
	 * 
	 * @return The string containing the names of each list element separated
	 * by comma.
	 */
	public static String toString(List list) {
		String result = "";
		for (Iterator it = list.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (it.hasNext()) {
				result += obj.toString() + ", ";
			} else {
				result += obj.toString();
			}
		}
		return result;
	}
}
