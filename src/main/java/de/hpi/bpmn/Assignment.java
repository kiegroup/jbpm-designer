package de.hpi.bpmn;

/**
 * Copyright (c) 2009
 * 
 * @author Armin Zamani
 *
 */
public class Assignment {

	public enum AssignTime {
		Start, End
	}
	
	//"to" should be of type Property instead of String, 
	//but the editor does not support the selection of defined properties
	private String to = "";
	private String from = "";
	private AssignTime assignTime = AssignTime.Start;
	
	/**
	 * constructor of class 
	 */
	public Assignment(String newTo, String newFrom, AssignTime newAssignTime){
		setTo(newTo);
		setFrom(newFrom);
		setAssignTime(newAssignTime);
	}
	
	/**
	 * the to setter
	 * @param newTo
	 */
	public void setTo(String newTo){
		to = newTo;
	}
	
	/**
	 * the to getter
	 * @return to
	 */
	public String getTo(){
		return to;
	}
	
	/**
	 * the from setter
	 * @param newFrom
	 */
	public void setFrom(String newFrom){
		from = newFrom;
	}
	
	/**
	 * the from getter
	 * @return from
	 */
	public String getFrom(){
		return from;
	}
	
	/**
	 * the assignTime setter
	 * @param newAssignTime
	 */
	public void setAssignTime(AssignTime newAssignTime){
		assignTime = newAssignTime;
	}
	
	/**
	 * the assignTime getter
	 * @return assignTime
	 */
	public AssignTime getAssignTime(){
		return assignTime;
	}
}
