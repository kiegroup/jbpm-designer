package de.hpi.bpel4chor.transformation;

public class TransformationResult {
	public boolean success;
	public String result;
	
	public TransformationResult(boolean success, String result) {
		this.success = success;
		this.result = result;
	}
}
