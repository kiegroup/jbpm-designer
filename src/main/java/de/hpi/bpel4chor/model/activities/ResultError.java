package de.hpi.bpel4chor.model.activities;

/**
 * The result error is used by error intermediate events and
 * defines the error that is thrown or caught. Attached events do
 * not need to specify an error code. In this case all errors are caught.
 */
public class ResultError extends Trigger {
	
	private String errorCode = null;
	
	/**
	 * Constructor. Initializes the result.
	 */
	public ResultError() {}

	/**
	 * @return The name of the error that is caught or thrown.
	 */
	public String getErrorCode() {
		return this.errorCode;
	}

	/**
	 * Sets the name of the error that is caught or thrown.
	 *  
	 * @param errorCode The new error code.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
