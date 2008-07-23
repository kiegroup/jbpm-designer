/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.ArrayList;
import java.util.List;

public class InterconnectionModel extends NuNet {

	private List<ProcessModel> processModels;

	public List getProcessModels() {
		if (processModels == null)
			processModels = new ArrayList();
		return processModels;
	}

} // InterconnectionModel