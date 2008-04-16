package de.hpi.bpmn2execpn.model;

import de.hpi.bpmn.Task;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;


// ****************************
// helper class for tasks
// *****************************
public class ExecTask extends Task {

	public Transition allocate, submit, suspend, resume, skip, review, delegate, autofinish;
	public Place running, suspended, finish;

}

