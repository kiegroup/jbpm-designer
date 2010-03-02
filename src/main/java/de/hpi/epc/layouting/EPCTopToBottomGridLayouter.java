/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig, Matthias Weidlich
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
package de.hpi.epc.layouting;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.grid.SuperGrid;
import de.hpi.layouting.grid.Grid.Cell;
import de.hpi.layouting.grid.Grid.Row;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingDiagram;
import de.hpi.layouting.model.LayoutingElement;


/**
 * Simple top to bottom Layouter based on the LeftToRightGridLayouter
 * of Team Royal Fawn. This simple version only deals with a flat model,
 * everything that has to do with nesting has been removed. Further on, 
 * this layouter is not BPMN specific, but uses the generic layouting
 * classes. In addition, the left to right layout is changed into a top to
 * bottom layout, when the geometry values are calculated.
 * 
 * @author matthias.weidlich
 *
 */
public class EPCTopToBottomGridLayouter {

		private static final int CELL_MARGIN = 20; // calc biggest elements in row /
		// column
		private static final int CELL_HEIGHT = 0;
		private static final int CELL_WIDTH = 0;

		/**
		 * Compares the distance of two elements relative to a third <tt>center</tt>
		 * element
		 * 
		 * @author Team Royal Fawn
		 * 
		 */
		private static class BackwardDistanceComperator implements
				Comparator<LayoutingElement> {

			private LayoutingElement ce;

			/**
			 * @param center
			 */
			private BackwardDistanceComperator(LayoutingElement center) {
				super();
				this.ce = center;
			}

			public int compare(LayoutingElement o1, LayoutingElement o2) {
				return ce.backwardDistanceTo(o1) - ce.backwardDistanceTo(o2);

			}

		}

		private static class GridContext {
			private Grid<LayoutingElement> grid;
			private Cell<LayoutingElement> startCell;
		}

		private List<String> orderedIds;
		private LayoutingDiagram diagram;
		private SuperGrid<LayoutingElement> superGrid;
		private double[] heightOfRow;
		private double heightOfSuperGrid = 0;
		private double[] widthOfColumn;
		private double widthOfSuperGrid = 0;

		private GridContext context;

		public EPCTopToBottomGridLayouter(List<String> orderedIds) {
			this.orderedIds = orderedIds;
			context = new GridContext();
			context.grid = new Grid<LayoutingElement>();
			context.startCell = context.grid.getFirstRow().getFirstCell();
			superGrid = new SuperGrid<LayoutingElement>();
			superGrid.add(context.grid);
		}

		public void doLayout() {
			layoutElements();
			calcGeometry(superGrid);
			writeGeometry(superGrid);
			
		}

		private GridContext getContextByElement(LayoutingElement el) {
			return context;
		}

		private void layoutElements() {

			for (String id : this.orderedIds) {
				//System.out.println(id);
				LayoutingElement currentElement = this.diagram.getElement(id);
				List<LayoutingElement> precedingElements = currentElement
						.getPrecedingElements();
				GridContext context = getContextByElement(currentElement);
				Cell<LayoutingElement> cellOfElement = null;
				cellOfElement = placeElement(currentElement, precedingElements, context);

				boolean comesFromOtherGrid = precedingElements.size() == 1
						&& precedingElements.get(0).getParent() != currentElement
								.getParent();
				if (!currentElement.isJoin() && !comesFromOtherGrid
						&& cellOfElement.getPrevCell() != null) {
					// there is an edge hitting us left, so lets forbid
					// interleaving to use the left cell, if it's empty
					cellOfElement.getPrevCell().setPackable(false);
				}

				if (currentElement.isSplit()) {
					prelayoutSuccessors(currentElement, context, cellOfElement);
				}
			}
		}

		/**
		 * @param currentElement
		 * @param context
		 * @param cellOfElement
		 */
		private void prelayoutSuccessors(LayoutingElement currentElement,
				GridContext context, Cell<LayoutingElement> cellOfElement) {
			// preLayout following Elements
			Cell<LayoutingElement> baseCell = cellOfElement.after();
			Cell<LayoutingElement> topCell = baseCell;
			List<LayoutingElement> followingElements = currentElement
					.getFollowingElements();

			// heuristic for direct connection to join
			LayoutingElement directJoin = null;
			for (LayoutingElement possibleJoin : followingElements) {
				if (possibleJoin.isJoin()) {
					directJoin = (LayoutingElement) possibleJoin;
				}
			}
			if (directJoin != null) {
				// put in the middle
				followingElements.remove(directJoin);
				int position = (followingElements.size() / 2);
				followingElements.add(position, directJoin);
			}

			// normal preLayout following Elements
			int follow = 0;
			for (LayoutingElement newElem : followingElements) {
				if (newElem.getParent() == currentElement.getParent()) {
					follow++;
				}
			}
			for (int i = 0; i < follow / 2; i++) {
				topCell.getParent().insertRowAbove();
				baseCell.getParent().insertRowBeneath();
				topCell = topCell.above();
			}

			for (LayoutingElement newElem : followingElements) {
				if (newElem.getParent() != currentElement.getParent()) {
					continue;
				}
				context.grid.setCellOfItem((LayoutingElement)newElem, topCell); // prelayout
				topCell = topCell.beneath();
				if (topCell == baseCell && follow % 2 == 0) {
					// skip baseCell if an even amount of elements is
					// following
					topCell = topCell.beneath();
				}
			}
		}

		/**
		 * @param currentElement
		 * @param precedingElements
		 * @param context
		 * @return cellOfElement
		 */
		private Cell<LayoutingElement> placeElement(LayoutingElement currentElement,
				List<LayoutingElement> precedingElements, GridContext context) {
			Cell<LayoutingElement> newCell;
			if (precedingElements.isEmpty()) {
				// StartEvents
				context.startCell.setValue(currentElement);
				newCell = context.startCell;
				context.startCell = context.startCell.beneath();
			} else {
				Cell<LayoutingElement> leftCell;
				newCell = context.grid.getCellOfItem(currentElement); // not
				// null
				// if
				// join
				if (currentElement.isJoin()) {

					Point tmp;
					boolean splitFound = false;
					LayoutingElement split = (LayoutingElement) currentElement.prevSplit();
					if (split != null) {
						// get all close splits
						Queue<LayoutingElement> splits = new PriorityQueue<LayoutingElement>(
								precedingElements.size() / 2, // should be a
								// good rule of
								// thumb
								new BackwardDistanceComperator(currentElement));
						splits.add(split);
						for (LayoutingElement elem : precedingElements) {
							split = (LayoutingElement) elem.prevSplit();
							if (split != null && !splits.contains(split)) {
								splits.add(split);
							}
						}
						split = null;
						// get split with most connections
						int maxCon = 0;
						for (LayoutingElement target : splits) {
							if (target == currentElement) {
								// beeing my own splits only makes trouble
								continue;
							} else if (target.getParent() != currentElement
									.getParent()) {

								continue;
							}
							int curCon = 0;
							for (LayoutingElement elem : precedingElements) {
								if (elem.backwardDistanceTo(target) < Integer.MAX_VALUE) {
									curCon++;
								}
							}
							if (curCon > maxCon) {
								maxCon = curCon;
								split = target;
							}
						}
						splitFound = split != null;
					}

					int x = 0;
					int yAcc = 0;
					int yCnt = 0;
					for (LayoutingElement el : precedingElements) {
						LayoutingElement elem = (LayoutingElement)el;
						tmp = context.grid.find(context.grid.getCellOfItem(elem));
						if (tmp == null) {
							Grid<LayoutingElement> preGrid = getContextByElement(elem).grid;
							tmp = preGrid.find(preGrid.getCellOfItem(elem));
							if (tmp == null) {
								tmp = new Point(0, 0);
							}
						} else {
							yAcc += tmp.y;
							yCnt++;
						}
						x = Math.max(x, tmp.x);
					}
					if (splitFound) {

						leftCell = context.grid.getCellOfItem(split).getParent()
								.get(x);
						// set path to split unpackable
						for (Cell<LayoutingElement> cCell = leftCell; cCell.getValue() != split; cCell = cCell
								.getPrevCell()) {
							cCell.setPackable(false);
						}

					} else {
						if (yCnt == 0) {
							leftCell = context.grid.getFirstRow().above().get(x);
						} else {
							leftCell = context.grid.get(yAcc / yCnt).get(x);
						}
					}
					if (newCell != null && newCell.getValue() == currentElement) {
						newCell.setValue(null);
					}
					newCell = leftCell.after();

					// set all incoming pathes unpackable
					for (LayoutingElement e : precedingElements) {
						LayoutingElement el = (LayoutingElement)e;
						Cell<LayoutingElement> target = context.grid.getCellOfItem(el);
						if (target == null) {
							// don't set unpackable in other grids (other edge
							// layout)
							continue;
						}
						Cell<LayoutingElement> start = target.getParent().get(x + 1);
						for (Cell<LayoutingElement> cCell = start; cCell != target; cCell = cCell
								.getPrevCell()) {
							cCell.setPackable(false);
						}
					}

					// if not prelayouted
				} else if (newCell == null) {
					LayoutingElement preElem = (LayoutingElement) precedingElements.get(0);
					leftCell = context.grid.getCellOfItem(preElem);
					if (leftCell == null) {
						Grid<LayoutingElement> preGrid = getContextByElement(preElem).grid;
						Cell<LayoutingElement> preCell = preGrid.getCellOfItem(preElem);
						if (preCell == null) {
							System.err.println("Cannot find Cell for " + preElem);
						}

						List<Grid<LayoutingElement>> grids = superGrid.getGrids();
						Row<LayoutingElement> newRow = null;
						if (grids.indexOf(preGrid) < grids.indexOf(context.grid)) {
							newRow = context.grid.addFirstRow();
						} else {
							newRow = context.grid.addLastRow();
						}
						leftCell = newRow.get(Math.max(0, preCell.getParent().find(
								preCell)));
					}
					newCell = leftCell.after();
				}
				if (newCell.isFilled()
						&& !newCell.getValue().equals(currentElement)) {
					newCell.getParent().insertRowBeneath();
					newCell = newCell.beneath();
				}
				newCell.setValue(currentElement);
			}
			return newCell;
		}

		/**
		 * @param grids
		 */
		private void calcGeometry(SuperGrid<LayoutingElement> grids) {
			grids.pack();
			heightOfRow = new double[grids.getHeight()];
			widthOfColumn = new double[grids.getWidth()];
			// initialize with standard values
			Arrays.fill(heightOfRow, CELL_HEIGHT);
			Arrays.fill(widthOfColumn, CELL_WIDTH);
			// find biggest
			int row = 0;
			int column = 0;
			for (Row<LayoutingElement> r : grids) {
				column = 0;
				for (Cell<LayoutingElement> c : r) {
					if (c.isFilled()) {
						LayoutingElement elem = c.getValue();
						LayoutingBounds geom = elem.getGeometry();
						widthOfColumn[column] = Math.max(widthOfColumn[column],
								geom.getHeight() + CELL_MARGIN);
						heightOfRow[row] = Math.max(heightOfRow[row], geom
								.getWidth()
								+ CELL_MARGIN);
					}
					column++;
				}
				row++;
			}

			// calc width / height
			widthOfSuperGrid = 0;
			for (double columnWidth : widthOfColumn) {
				widthOfSuperGrid += columnWidth;
			}
			heightOfSuperGrid = 0;
			for (double rowHeight : heightOfRow) {
				heightOfSuperGrid += rowHeight;
			}
		}

		/**
		 * @param grids
		 */
		private void writeGeometry(SuperGrid<LayoutingElement> grids) {
			// write cells
			double x = 0;
			double y = 0;
			int row = 0;
			int column = 0;
			
			// reverse the rows in order to mirror the grid
//			List<Row<LayoutingElement>> rows = new ArrayList<Row<LayoutingElement>>();
//			for (Row<LayoutingElement> r : grids) {
//				rows.add(r);
//			}
//			Collections.reverse(rows);

			for (Row<LayoutingElement> r : grids) {
				column = 0;
				double cellWidth = heightOfRow[row];
				for (Cell<LayoutingElement> c : r) {
					double cellHeight = widthOfColumn[column];
					if (c.isFilled()) {
						LayoutingElement elem = c.getValue();
						LayoutingBounds geom = elem.getGeometry();
						double newX = x + (cellWidth / 2.0)
								- (geom.getWidth() / 2.0);
						double newY = y + (cellHeight / 2.0)
								- (geom.getHeight() / 2.0);

						elem.setGeometry(new LayoutingBoundsImpl(newX, newY, geom
								.getWidth(), geom.getHeight()));
					}
					y += cellHeight;
					column++;
				}
				y = 0;
				x += cellWidth;
				row++;
			}
		}

		public void setDiagram(LayoutingDiagram diagram) {
			this.diagram = diagram;
		}


		/**
		 * @return the heightOfDiagramm
		 */
		public double getHeightOfDiagramm() {
			return heightOfSuperGrid;
		}

		/**
		 * @return the widthOfDiagramm
		 */
		public double getWidthOfDiagramm() {
			return widthOfSuperGrid;
		}
		
		public Grid<LayoutingElement> getGrid() {
			return context.grid;
		}
	}

