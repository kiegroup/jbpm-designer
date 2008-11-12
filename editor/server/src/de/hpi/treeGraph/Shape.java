
package de.hpi.treeGraph;

import java.util.ArrayList;
import java.util.List;

public class Shape {
	
	protected String id;
	
	protected Shape ingoingShape = null;
	protected List<Shape> outgoingShapes = new ArrayList<Shape>();
	
	public Shape(String id) {
		this.id = id;
	}
	
	public Shape getIngoingShape() {
		return ingoingShape;
	}

	public void setIngoingShape(Shape ingoingShape) {
		this.ingoingShape = ingoingShape;
	}

	public List<Shape> getOutgoingShapes() {
		return outgoingShapes;
	}

	public String getId() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Shape) {
			return ((Shape)o).getId().equals(this.id);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	public void attachShape(Shape shape) throws Exception {
		this.outgoingShapes.add(shape);
		shape.setIngoingShape(this);
	}
	
}
