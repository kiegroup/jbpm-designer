package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Secret extends FormControl {

	public Secret() {
		super();
		attributes.put("inputmode", null);
		attributes.put("incremental", null);
	}
	
	@Override
	public String getStencilId() {
		return "Secret";
	}
	
	@Override
	public String getTagName() {
		return "secret";
	}
	
}
