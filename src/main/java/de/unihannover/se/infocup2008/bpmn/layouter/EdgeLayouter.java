/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.unihannover.se.infocup2008.bpmn.layouter;

import java.util.Map;
import java.util.Random;

import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.grid.Grid.Cell;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingDockers;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

/**
 * This class layouts the edges. All work is done in the constructor.
 * <code>calculateGlobals()</code> calculates some global variables, which are
 * needed in many functions. <code>pickLayout*()</code> decides which type of
 * edge to use. <code>set*()</code> actually calculate one specific edge.
 * 
 * @author Team Royal Fawn
 * 
 */
public class EdgeLayouter {

	private Map<BPMNElement, Grid<BPMNElement>> grids;
	private BPMNElement edge;

	private BPMNElement source;
	private BPMNElement target;
	private LayoutingBounds sourceGeometry;
	private LayoutingBounds targetGeometry;

	// Relative coordinates
	private double sourceRelativCenterX;
	private double sourceRelativCenterY;
	private double targetRelativCenterX;
	private double targetRelativCenterY;

	// absolute coordinates
	private double sourceAbsoluteCenterX;
	private double sourceAbsoluteCenterY;
	private double sourceAbsoluteX;
	private double sourceAbsoluteY;
	private double sourceAbsoluteX2;
	private double sourceAbsoluteY2;
	private double targetAbsoluteCenterX;
	private double targetAbsoluteCenterY;
	private double targetAbsoluteX;
	private double targetAbsoluteY;
	private double targetAbsoluteY2;

	// layout hints
	private boolean sourceJoin;
	private boolean sourceSplit;
	private boolean targetJoin;
	private boolean backwards;

	public EdgeLayouter(BPMNElement edge) {
		this(null, edge);
	}

	public EdgeLayouter(Map<BPMNElement, Grid<BPMNElement>> grids,
			BPMNElement edge) {
		// if one end is not connected do nothing
		if(edge.getIncomingLinks().isEmpty() || edge.getOutgoingLinks().isEmpty()){
			return;
		}
		this.edge = edge;
		this.grids = grids;
		calculateGlobals();
		pickLayoutForEdge();
		this.edge.updateDataModel();
	}

	private void calculateGlobals() {
		// should both be only one !
		this.source = (BPMNElement) edge.getIncomingLinks().get(0);
		this.target = (BPMNElement) edge.getOutgoingLinks().get(0);

		this.sourceGeometry = source.getGeometry();
		this.targetGeometry = target.getGeometry();

		// get relative centers of elements
		this.sourceRelativCenterX = this.sourceGeometry.getWidth() / 2;
		this.sourceRelativCenterY = this.sourceGeometry.getHeight() / 2;
		this.targetRelativCenterX = this.targetGeometry.getWidth() / 2;
		this.targetRelativCenterY = this.targetGeometry.getHeight() / 2;

		// get parent adjustments
		double sourceParentAdjustmentX = 0;
		double sourceParentAdjustmentY = 0;
		BPMNElement parent = (BPMNElement) this.source.getParent();
		while (parent != null) {
			if (parent.getType().equals(BPMNType.Lane)) {
				sourceParentAdjustmentX += LeftToRightGridLayouter.LANE_HEAD_WIDTH;
			}
			sourceParentAdjustmentX += parent.getGeometry().getX();
			sourceParentAdjustmentY += parent.getGeometry().getY();
			parent = (BPMNElement) parent.getParent();
		}

		double targetParentAdjustmentX = 0;
		double targetParentAdjustmentY = 0;
		parent = (BPMNElement) this.target.getParent();
		while (parent != null) {
			if (parent.getType().equals(BPMNType.Lane)) {
				targetParentAdjustmentX += LeftToRightGridLayouter.LANE_HEAD_WIDTH;
			}
			targetParentAdjustmentX += parent.getGeometry().getX();
			targetParentAdjustmentY += parent.getGeometry().getY();
			parent = (BPMNElement) parent.getParent();
		}

		// get absolute coordinates
		this.sourceAbsoluteX = this.sourceGeometry.getX()
				+ sourceParentAdjustmentX;
		this.sourceAbsoluteY = this.sourceGeometry.getY()
				+ sourceParentAdjustmentY;
		this.sourceAbsoluteX2 = this.sourceGeometry.getX2()
				+ sourceParentAdjustmentX;
		this.sourceAbsoluteY2 = this.sourceGeometry.getY2()
				+ sourceParentAdjustmentY;

		this.targetAbsoluteX = this.targetGeometry.getX()
				+ targetParentAdjustmentX;
		this.targetAbsoluteY = this.targetGeometry.getY()
				+ targetParentAdjustmentY;

		this.targetAbsoluteY2 = this.targetGeometry.getY2()
				+ targetParentAdjustmentY;

		this.sourceAbsoluteCenterX = this.sourceAbsoluteX
				+ this.sourceRelativCenterX;
		this.sourceAbsoluteCenterY = this.sourceAbsoluteY
				+ this.sourceRelativCenterY;
		this.targetAbsoluteCenterX = this.targetAbsoluteX
				+ this.targetRelativCenterX;
		this.targetAbsoluteCenterY = this.targetAbsoluteY
				+ this.targetRelativCenterY;

		// layout hints
		this.sourceJoin = this.source.isJoin();
		this.sourceSplit = this.source.isSplit();
		this.targetJoin = this.target.isJoin();
		this.target.isSplit();
		this.backwards = this.sourceAbsoluteCenterX > this.targetAbsoluteCenterX;
	}

	private void pickLayoutForEdge() {
		if (BPMNType.SequenceFlow.equals(this.edge.getType())) {
			pickLayoutForSequenceFlow();
		} else {
			// is a message flow or association
			pickLayoutForOtherConnection();
		}
	}

	private void pickLayoutForOtherConnection() {
		// should go out an in diagonal at the corners to not interference with
		// the sequence flows

		if (source.getType().equals(BPMNType.CollapsedPool)
				|| target.getType().equals(BPMNType.CollapsedPool)) {
			setEdgeDirectVertical();
			return;
		}

		if (BPMNType.isASwimlane(source.getType())
				|| BPMNType.isASwimlane(target.getType())) {
			setEdgeDirectCenter();
			return;
		}

		setEdgeDirectCenter();

	}

	private void pickLayoutForSequenceFlow() {
		if (source.isADockedIntermediateEvent()) {
			if (backwards) {
				setEdgeAroundTheCorner(true);
			} else {
				setEdge90DegreeRightUnderAntiClockwise();
			}
			return;
		}

		// if on the same x or y and nothing between -> make direct connection
		// something between -> up corner
		if (sourceAbsoluteCenterX == targetAbsoluteCenterX) {
			setEdgeDirectCenter();
			return;
		} else if (sourceAbsoluteCenterY == targetAbsoluteCenterY) {
			if (areCellsHorizontalFree()) {
				setEdgeDirectCenter();
			} else {
				setEdgeAroundTheCorner(true);
			}
			return;
		}

		if (sourceAbsoluteCenterX <= targetAbsoluteCenterX
				&& sourceAbsoluteCenterY <= targetAbsoluteCenterY) {
			// target is right under
			if (sourceJoin && sourceSplit) {
				setEdgeStepRight();
				return;
			} else if (sourceSplit) {
				setEdge90DegreeRightUnderAntiClockwise();
				return;
			} else if (targetJoin) {
				setEdge90DegreeRightUnderClockwise();
				return;
			}

		} else if (sourceAbsoluteCenterX <= targetAbsoluteCenterX
				&& sourceAbsoluteCenterY > targetAbsoluteCenterY) {
			// target is right above
			if (sourceJoin && sourceSplit) {
				setEdgeStepRight();
				return;
			} else if (sourceSplit) {
				setEdge90DegreeRightAboveClockwise();
				return;
			} else if (targetJoin) {
				setEdge90DegreeRightAboveAntiClockwise();
				return;
			}
		}

		if (sourceJoin && sourceSplit && (!backwards)) {
			setEdgeStepRight();
			return;
		}

		if (sourceSplit && targetJoin) {
			setEdgeAroundTheCorner(true);
			return;
		}

		setEdgeDirectCenter();
	}

	private boolean areCellsHorizontalFree() {
		if (this.grids == null || source.getParent() != target.getParent()) {
			return (Math.abs(sourceAbsoluteCenterX - targetAbsoluteCenterX) < 210);
		}

		Grid<BPMNElement> grid = this.grids.get(source.getParent());

		Cell<BPMNElement> fromCell;
		Cell<BPMNElement> toCell;

		if (sourceAbsoluteCenterX < targetAbsoluteCenterX) {
			fromCell = grid.getCellOfItem(source);
			toCell = grid.getCellOfItem(target);
		} else {
			fromCell = grid.getCellOfItem(target);
			toCell = grid.getCellOfItem(source);
		}

		fromCell = fromCell.getNextCell();
		while (fromCell != toCell) {
			if (fromCell == null || fromCell.isFilled()) {
				return false;
			}
			fromCell = fromCell.getNextCell();
		}

		return true;
	}

	private void setEdgeDirectCenter() {

		// make bounding box
		double boundsMinX = Math.min(sourceAbsoluteCenterX,
				targetAbsoluteCenterX);
		double boundsMinY = Math.min(sourceAbsoluteCenterY,
				targetAbsoluteCenterY);
		double boundsMaxX = Math.max(sourceAbsoluteCenterX,
				targetAbsoluteCenterX);
		double boundsMaxY = Math.max(sourceAbsoluteCenterY,
				targetAbsoluteCenterY);

		// set bounds
		edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX
				- boundsMinX, boundsMaxY - boundsMinY));

		// set dockers - direct connection
		LayoutingDockers dockers = edge.getDockers();

		if (source.getType().equals(BPMNType.TextAnnotation)) {
			// TextAnnotation has its docker at the left
			dockers.setPoints(0, sourceRelativCenterY);
		} else {
			dockers.setPoints(sourceRelativCenterX, sourceRelativCenterY);
		}
		
		if (target.getType().equals(BPMNType.TextAnnotation)) {
			// TextAnnotation has its docker at the left
			dockers.addPoint(0, targetRelativCenterY);
		} else {
			dockers.addPoint(targetRelativCenterX, targetRelativCenterY);
		}

	}

	private void setEdgeDirectVertical() {
		// make bounding box
		double boundsX = 0;
		double boundsMinY = Math
				.min(this.sourceAbsoluteY, this.targetAbsoluteY);
		double boundsMaxY = Math.max(this.sourceAbsoluteY2,
				this.targetAbsoluteY2);

		LayoutingDockers dockers = edge.getDockers();
		if (source.getType().equals(BPMNType.CollapsedPool)) {
			double displacement = 30;// + (Math.abs(targetAbsoluteCenterY -
			// sourceAbsoluteCenterY) *
			// displacementFactor);
			// take middle of target
			double startPoint = (targetAbsoluteCenterX - displacement);
			dockers.setPoints(startPoint, 70); // pools
			// start at
			// x=0
			boundsX = startPoint;
		} else {
			double displacement = 10;
			// take source middle
			boundsX = (sourceAbsoluteCenterX + displacement);
			dockers.setPoints(boundsX, sourceRelativCenterY);
		}

		if (target.getType().equals(BPMNType.CollapsedPool)) {
			double displacement = 30;// + (Math.abs(targetAbsoluteCenterY -
			// sourceAbsoluteCenterY) *
			// displacementFactor);
			// take middle of source
			dockers.addPoint(sourceAbsoluteCenterX + displacement, 70);// pools
			// start at
			// x=0
		} else {
			double displacement = 10;
			// take target middle
			dockers.addPoint(targetRelativCenterX - displacement,
					targetRelativCenterY);
		}

		// set bounds
		edge.setGeometry(new LayoutingBoundsImpl(boundsX, boundsMinY, 0, boundsMaxY
				- boundsMinY));
	}

	private void setEdge90DegreeRightAboveAntiClockwise() {
		double boundsMinX = sourceAbsoluteX2;
		double boundsMinY = targetAbsoluteY2;
		double boundsMaxX = targetAbsoluteCenterX;
		double boundsMaxY = sourceAbsoluteCenterY;
		double cornerDockerX = boundsMaxX;
		double cornerDockerY = boundsMaxY;
		set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY,
				cornerDockerX, cornerDockerY);
	}

	private void setEdge90DegreeRightAboveClockwise() {
		double boundsMinX = sourceAbsoluteCenterX;
		double boundsMinY = targetAbsoluteCenterY;
		double boundsMaxX = targetAbsoluteX;
		double boundsMaxY = sourceAbsoluteY;
		double cornerDockerX = boundsMinX;
		double cornerDockerY = boundsMinY;
		set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY,
				cornerDockerX, cornerDockerY);
	}

	private void setEdge90DegreeRightUnderAntiClockwise() {
		double boundsMinX = sourceAbsoluteCenterX;
		double boundsMinY = sourceAbsoluteY2;
		double boundsMaxX = targetAbsoluteX;
		double boundsMaxY = targetAbsoluteCenterY;
		double cornerDockerX = boundsMinX;
		double cornerDockerY = boundsMaxY;
		set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY,
				cornerDockerX, cornerDockerY);
	}

	private void setEdge90DegreeRightUnderClockwise() {
		double boundsMinX = sourceAbsoluteX2;
		double boundsMinY = sourceAbsoluteCenterY;
		double boundsMaxX = targetAbsoluteCenterX;
		double boundsMaxY = targetAbsoluteY;
		double cornerDockerX = boundsMaxX;
		double cornerDockerY = boundsMinY;
		set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY,
				cornerDockerX, cornerDockerY);
	}

	private void set90DegreeEdgeGeometry(double boundsMinX, double boundsMinY,
			double boundsMaxX, double boundsMaxY, double cornerDockerX,
			double cornerDockerY) {
		// set bounds
		edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX
				- boundsMinX, boundsMaxY - boundsMinY));

		// set dockers
		edge.getDockers().setPoints(sourceRelativCenterX, sourceRelativCenterY,
				cornerDockerX, cornerDockerY, targetRelativCenterX,
				targetRelativCenterY);
	}

	private void setEdgeAroundTheCorner(boolean down) {
		int angleDistance = 15;
		double height = Math.max(sourceGeometry.getHeight() / 2, targetGeometry
				.getHeight() / 2) + 20;
		height += new Random().nextInt(5) * 3;

		// make bounding box
		double boundsMinX = Math.min(sourceAbsoluteCenterX,
				targetAbsoluteCenterX);
		double boundsMinY = Math.min(sourceAbsoluteCenterY,
				targetAbsoluteCenterY);
		double boundsMaxX = Math.max(sourceAbsoluteCenterX,
				targetAbsoluteCenterX);
		double boundsMaxY = Math.max(sourceAbsoluteCenterY,
				targetAbsoluteCenterY);

		if (down) {
			boundsMaxY += height;
		} else {
			boundsMinY -= height;
		}

		// set bounds
		edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX
				- boundsMinX, boundsMaxY - boundsMinY));

		// set dockers
		double docker1X = sourceAbsoluteCenterX;
		double docker2X = targetAbsoluteCenterX;
		if (backwards) {
			docker1X += angleDistance;
			docker2X -= angleDistance;
		} else {
			docker1X += angleDistance;
			docker2X -= angleDistance;
		}

		double docker1Y = 0;
		double docker2Y = 0;
		if (down) {
			docker1Y = boundsMaxY;
			docker2Y = boundsMaxY;
		} else {
			docker1Y = boundsMinY;
			docker2Y = boundsMinY;
		}

		edge.getDockers().setPoints(sourceRelativCenterX, sourceRelativCenterY,
				docker1X, docker1Y, docker2X, docker2Y, targetRelativCenterX,
				targetRelativCenterY);

	}

	private void setEdgeStepRight() {

		// make bounding box
		double boundsMinX = Math.min(sourceAbsoluteCenterX,
				targetAbsoluteCenterX);
		double boundsMinY = Math.min(sourceAbsoluteCenterY,
				targetAbsoluteCenterY);
		double boundsMaxX = Math.max(sourceAbsoluteCenterX,
				targetAbsoluteCenterX);
		double boundsMaxY = Math.max(sourceAbsoluteCenterY,
				targetAbsoluteCenterY);

		// set bounds
		edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX
				- boundsMinX, boundsMaxY - boundsMinY));

		// set dockers
		// double halfBoundsX = sourceGeometry.getX2()
		// + ((targetGeometry.getX() - sourceGeometry.getX2()) / 2);
		double halfBoundsX = sourceAbsoluteX2 + 15;

		edge.getDockers().setPoints(sourceRelativCenterX, sourceRelativCenterY,
				halfBoundsX, sourceAbsoluteCenterY, halfBoundsX,
				targetAbsoluteCenterY, targetRelativCenterX,
				targetRelativCenterY);

	}
}
