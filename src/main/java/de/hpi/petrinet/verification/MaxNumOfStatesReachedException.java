package de.hpi.petrinet.verification;

public class MaxNumOfStatesReachedException extends RuntimeException {
	private static final long serialVersionUID = -5215914013092527806L;

	public MaxNumOfStatesReachedException(){
	}
	public MaxNumOfStatesReachedException(String s){
		super(s);
	}
}
