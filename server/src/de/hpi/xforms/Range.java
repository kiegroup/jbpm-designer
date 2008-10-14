package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Range extends FormControl {

	public Range() {
		super();
		attributes.put("start", null);
		attributes.put("end", null);
		attributes.put("step", null);
		attributes.put("incremental", null);
	}
	
	@Override
	public String getStencilId() {
		return "Range";
	}
	
	@Override
	public String getTagName() {
		return "range";
	}
	
}
