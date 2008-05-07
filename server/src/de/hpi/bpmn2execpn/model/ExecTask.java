package de.hpi.bpmn2execpn.model;

import de.hpi.bpmn.Task;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;


// ****************************
// helper class for tasks
// *****************************
public class ExecTask extends Task {

	public Transition tr_enable, tr_allocate, tr_submit, tr_suspend, tr_resume, tr_skip, tr_review, tr_delegate, tr_done, tr_finish;
	public Place pl_ready, pl_running, pl_suspended, pl_deciding, pl_complete, pl_context;

}

