/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet.impl;

import java.util.ArrayList;
import java.util.List;

import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.ProcessModel;

public class InterconnectionModelImpl extends NuNetImpl implements InterconnectionModel {
	
	private List<ProcessModel> processModels;

	public List getProcessModels() {
		if (processModels == null)
			processModels = new ArrayList();
		return processModels;
	}

} // InterconnectionModel