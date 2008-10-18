package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Submit extends FormControl {
	
	protected Submission submission;
	
	public Submit() {
		super();
		attributes.put("submission", null);
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
		this.attributes.put("submission", submission.getAttributes().get("id"));
	}
	
	@Override
	public String getStencilId() {
		return "Submit";
	}
	
	@Override
	public String getTagName() {
		return "submit";
	}

}
