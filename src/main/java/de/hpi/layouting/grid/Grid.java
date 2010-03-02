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
package de.hpi.layouting.grid;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * A 2d-datastructure holding <tt>Cell</tt>'s in <tt>Row</tt>'s and Columns.
 * A <tt>Cell</tt> holds a value of type <tt><T></tt>. You can insert
 * <tt>Row</tt>'s and columns into the grid, without affecting the
 * <tt>cell</tt>s relationship to each other. Eg two <tt>cell</tt>'s stay
 * always verticaly adjacent if you insert a column and they will always
 * stay horizontal adjacent if you insert a <tt>Row</tt>
 * 
 * @author Team Royal Fawn
 * 
 * @param <T>
 *            The type of the values in the cells
 */
public class Grid<T> implements Iterable<Grid.Row<T>> {

	
	/**
	 * A <tt>Row</tt> in the <tt>Grid</tt>. Holds a set of <tt>Cell</tt>s
	 * @author Team Royal Fawn
	 *
	 * @param <T>
	 */
	public static class Row<T> implements Iterable<Grid.Cell<T>> {

		private Grid<T> parent;
		private Cell<T> firstCell;
		private Cell<T> lastCell;
		private Row<T> prevRow;
		private Row<T> nextRow;

		/**
		 * @param parent
		 * @param prevRow
		 * @param nextRow
		 */
		private Row(Grid<T> parent, Row<T> prevRow, Row<T> nextRow) {
			super();
			this.parent = parent;
			this.prevRow = prevRow;
			this.nextRow = nextRow;
			firstCell = new Cell<T>(this, null, null);
			lastCell = firstCell;
			for (int i = 1; i < parent.width; i++) {
				lastCell._insertCellAfter();
			}
		}

		/**
		 * @return the parent
		 */
		public Grid<T> getParent() {
			return parent;
		}

		/**
		 * @return the prevRow
		 */
		public Row<T> getPrevRow() {
			return prevRow;
		}

		/**
		 * @return the nextRow
		 */
		public Row<T> getNextRow() {
			return nextRow;
		}

		public Row<T> insertRowAbove() {
			Row<T> newRow = new Row<T>(parent, prevRow, this);
			if (prevRow == null) {
				parent.firstRow = newRow;
			} else {
				getPrevRow().nextRow = newRow;
			}
			parent.height++;
			prevRow = newRow;
			return newRow;
		}

		public Row<T> insertRowBeneath() {
			Row<T> newRow = new Row<T>(parent, this, nextRow);
			if (nextRow == null) {
				parent.lastRow = newRow;
			} else {
				getNextRow().prevRow = newRow;
			}
			parent.height++;
			nextRow = newRow;
			return newRow;
		}

		/**
		 * Will create a new row above <code>this</code> if not existant
		 * 
		 * @return the row above <code>this</code>
		 */
		public Row<T> above() {
			Row<T> result;
			result = getPrevRow();
			if (result == null) {
				return insertRowAbove();
			}
			return result;
		}

		/**
		 * Will create a new row beneath <code>this</code> if not existant
		 * 
		 * @return the row beneath <code>this</code>
		 */
		public Row<T> beneath() {
			Row<T> result;
			result = getNextRow();
			if (result == null) {
				return insertRowBeneath();
			}
			return result;
		}

		public boolean isInterleaveableWith(Row<T> other) {
			if (other == null || other == this) {
				return false;
			} else if (other.getNextRow() != this && other.getPrevRow() != this) {
				return false;
			}
			Iterator<Cell<T>> oIt = other.iterator();
			for (Cell<T> c : this) {
				if (oIt.next().isUnpackable() && c.isUnpackable()) {
					return false;
				}
			}
			return true;
		}

		public boolean tryInterleaveWith(Row<T> other) {
			//System.out.println("Try to interleave " + this);
			//System.out.print("             with " + other);
			if (!isInterleaveableWith(other)) {
				//System.out.println(": failed");
				return false;
			}

			Iterator<Cell<T>> oIt = other.iterator();
			for (Cell<T> c : this) {
				Cell<T> oC = oIt.next();
				if (c.isFilled()) {
					if (oC.prevCell == null) {
						oC.parent.firstCell = c;
					} else {
						oC.prevCell.nextCell = c;
					}
					if (oC.nextCell == null) {
						oC.parent.lastCell = c;
					} else {
						oC.nextCell.prevCell = c;
					}

					c.prevCell = oC.prevCell;
					c.nextCell = oC.nextCell;
					c.parent = oC.parent;
					oC.nextCell = null;
					oC.prevCell = null;
					oC.parent = null;
				} else if (c.isUnpackable()) {
					oC.setPackable(false);
				}
			}
			this._remove();
			//System.out.println(": done");
			return true;
		}

		private void _remove() {
			this.parent.height--;
			if (prevRow == null) {
				parent.firstRow = nextRow;
			} else {
				this.prevRow.nextRow = nextRow;
			}
			if (nextRow == null) {
				parent.lastRow = prevRow;
			} else {
				this.nextRow.prevRow = prevRow;
			}

			this.firstCell = null;
			this.lastCell = null;
			this.prevRow = null;
			this.nextRow = null;
			this.parent = null;
		}

		/**
		 * @return
		 * 
		 */
		public boolean isFilled() {
			for (Cell<T> c : this) {
				if (c.isFilled()) {
					return true;
				}
			}
			return false;
		}

		public int find(Cell<T> target) {
			int i = 0;
			for (Cell<T> c : this) {
				if (c == target) {
					return i;
				}
				i++;
			}
			return -1;
		}

		public Cell<T> get(int i) {
			for (Cell<T> c : this) {
				if (i-- == 0) {
					return c;
				}
			}
			return null;
		}

		public Iterator<Cell<T>> iterator() {
			return new Iterator<Cell<T>>() {
				private Cell<T> next = firstCell;

				public boolean hasNext() {
					return next != null;
				}

				public Cell<T> next() {
					Cell<T> result = next;
					next = next.getNextCell();
					return result;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

		/**
		 * @return the firstCell
		 */
		public Cell<T> getFirstCell() {
			return firstCell;
		}

		/**
		 * @return the lastCell
		 */
		public Cell<T> getLastCell() {
			return lastCell;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[ ");
			Iterator<Cell<T>> it = this.iterator();
			if (it.hasNext()) {
				sb.append(it.next().toString());
			}
			while (it.hasNext()) {
				sb.append(", ");
				sb.append(it.next().toString());
			}
			sb.append("]");
			return sb.toString();
		}
	}

	/**
	 * A <tt>Cell</tt> encapsules an object (the value) of type <tt>T</tt> in the <tt>Grid</tt>.
	 * @author Team Royal Fawn
	 *
	 * @param <T>
	 */
	public static class Cell<T> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			// return isFilled() ? value.toString() : isUnpackable() ?
			// "<no pack>" : "  <empty>";
			return isFilled() ? "   <full>" : isUnpackable() ? "<no pack>"
					: "  <empty>";
		}

		private Row<T> parent;
		private Cell<T> prevCell;
		private Cell<T> nextCell;
		private T value;
		private boolean packable;

		/**
		 * @param parent
		 * @param prevCell
		 * @param nextCell
		 * @param value
		 */
		private Cell(Row<T> parent, Cell<T> prevCell, Cell<T> nextCell) {
			super();
			this.parent = parent;
			this.prevCell = prevCell;
			this.nextCell = nextCell;
			this.value = null;
			this.packable = true;
		}

		/**
		 * @return the value
		 */
		public T getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(T value) {
			if (this.value != null) {
				parent.getParent().removeCellOfItem(value);
			}
			this.value = value;
			parent.getParent().setCellOfItem(value, this);
		}

		public boolean isFilled() {
			return getValue() != null;
		}

		public void setPackable(boolean packable) {
			this.packable = packable;
		}

		public boolean isUnpackable() {
			return !packable || isFilled();
		}

		/**
		 * @return the parent
		 */
		public Row<T> getParent() {
			return parent;
		}

		/**
		 * @return the prevCell
		 */
		public Cell<T> getPrevCell() {
			return prevCell;
		}

		/**
		 * @return the nextCell
		 */
		public Cell<T> getNextCell() {
			return nextCell;
		}

		private Cell<T> _insertCellBefore() {
			Cell<T> newCell = new Cell<T>(parent, prevCell, this);
			if (prevCell == null) {
				parent.firstCell = newCell;
			} else {
				prevCell.nextCell = newCell;
			}
			prevCell = newCell;
			return newCell;
		}

		private Cell<T> _insertCellAfter() {
			Cell<T> newCell = new Cell<T>(parent, this, nextCell);
			if (nextCell == null) {
				parent.lastCell = newCell;
			} else {
				nextCell.prevCell = newCell;
			}
			nextCell = newCell;
			return newCell;
		}

		public Cell<T> insertCellBefore() {
			// Make sure first cell is empty
			if (parent.firstCell.isFilled()) {
				parent.parent.addFirstColumn();
			}
			_insertCellBefore();
			// Trim beginning of row
			parent.firstCell.getNextCell().prevCell = null;
			parent.firstCell = parent.firstCell.getNextCell();
			return getPrevCell();
		}

		public Cell<T> insertCellAfter() {
			// Make sure last cell is empty
			if (parent.lastCell.isFilled()) {
				parent.parent.addLastColumn();
			}
			_insertCellAfter();
			// Trim end of row
			parent.lastCell.getPrevCell().nextCell = null;
			parent.lastCell = parent.lastCell.getPrevCell();
			return getNextCell();
		}

		/**
		 * will create a new column before <code>this</code>
		 * 
		 * @param value
		 *            the value of the new cell
		 * @return the new cell before <code>this</code>
		 */
		public Cell<T> insertColumnBefore() {
			int i = parent.find(this);
			parent.parent.insertColumnBefore(i);
			return prevCell;
		}

		/**
		 * will create a new column after <code>this</code>
		 * 
		 * @return the new cell after <code>this</code>
		 */
		public Cell<T> insertColumnAfter() {
			int i = parent.find(this);
			parent.parent.insertColumnAfter(i);
			return nextCell;
		}

		/**
		 * Always returns the Cell before <code>this</code>. Will create a new
		 * Column if <code>this</code> is the first cell.
		 * 
		 * @return the Cell after this Cell.
		 */
		public Cell<T> before() {
			Cell<T> result = getPrevCell();
			if (result == null) {
				return insertColumnBefore();
			}
			return result;
		}

		/**
		 * Always returns the Cell after <code>this</code>. Will create a new
		 * Column if <code>this</code> is the last cell.
		 * 
		 * @return the Cell after this Cell.
		 */
		public Cell<T> after() {
			Cell<T> result = getNextCell();
			if (result == null) {
				return insertColumnAfter();
			}
			return result;
		}

		public Cell<T> above() {
			return parent.above().get(parent.find(this));
		}

		public Cell<T> beneath() {
			return parent.beneath().get(parent.find(this));
		}
	}

	private SuperGrid<T> parent;
	private Row<T> firstRow;
	private Row<T> lastRow;
	private int width;
	private int height;
	private Map<T, Cell<T>> cellOfItem;

	/**
	 * Creates an empty 1x1 Grid without parent
	 */
	public Grid() {
		super();
		width = 1;
		height = 1;
		cellOfItem = new HashMap<T, Cell<T>>();
		firstRow = new Row<T>(this, null, null);
		lastRow = firstRow;
	}

	public Grid(SuperGrid<T> parent) {
		this();
		this.setParent(parent);
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the firstRow
	 */
	public Row<T> getFirstRow() {
		return firstRow;
	}

	/**
	 * @return the lastRow
	 */
	public Row<T> getLastRow() {
		return lastRow;
	}

	/**
	 * @return the parent
	 */
	public SuperGrid<T> getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(SuperGrid<T> parent) {
		if (this.parent != null) {
			this.parent.remove(this);
		}
		if (parent != null) {
			parent.add(this);
		}
	}

	/**
	 * Setter must not be called from anyone but SuperGrid
	 * 
	 * @param parent
	 */
	void _setParent(SuperGrid<T> parent) {
		this.parent = parent;
	}

	public int find(Row<T> target) {
		int i = 0;
		for (Row<T> row : this) {
			if (row == target) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public Point find(Cell<T> target) {
		if (target == null) {
			return null;
		}
		int i = 0;
		for (Row<T> row : this) {
			if (row == target.getParent()) {
				return new Point(row.find(target), i);
			}
			i++;
		}
		return null;
	}

	public Row<T> get(int i) {
		for (Row<T> row : this) {
			if (i-- == 0) {
				return row;
			}
		}
		return null;
	}

	public void insertColumnBefore(int col) {
		if (col < 0 || col > width) {
			throw new IllegalArgumentException("Column #" + col
					+ " does not exist");
		}
		if (col == width) {
			for (Row<T> row : this) {
				row.lastCell._insertCellAfter();
			}
		} else {
			for (Row<T> row : this) {
				row.get(col)._insertCellBefore();
			}
		}
		width++;
		if (parent != null) {
			parent._insertColumnBefore(col, width);
		}
	}

	public void insertColumnAfter(int col) {
		insertColumnBefore(col + 1);
	}

	public void addFirstColumn() {
		insertColumnBefore(0);
	}

	public void addLastColumn() {
		insertColumnBefore(width);
	}

	public Row<T> addFirstRow() {
		return insertRowAbove(firstRow);
	}

	public Row<T> addLastRow() {
		return insertRowBeneath(lastRow);
	}

	public Row<T> insertRowAbove(Row<T> row) {
		return row.insertRowAbove();
	}

	public Row<T> insertRowBeneath(Row<T> row) {
		return row.insertRowBeneath();
	}

	/**
	 * Returns the Cell of the Item. The cell must not necessarily have the item
	 * as value.
	 * 
	 * @param item
	 *            the item to get the cell from
	 * @return the cell
	 */
	public Cell<T> getCellOfItem(T item) {
		return this.cellOfItem.get(item);
	}

	/**
	 * Sets the cell of the item. The cell must not necessarily have the item as
	 * value.
	 * 
	 * @param item
	 * @param cell
	 */
	public void setCellOfItem(T item, Cell<T> cell) {
		this.cellOfItem.put(item, cell);
	}

	/**
	 * Removes the cell of the item. The cell must not necessarily have the item
	 * as value.
	 * 
	 * @param item
	 * @param cell
	 */
	protected void removeCellOfItem(T item) {
		this.cellOfItem.remove(item);
	}

	public Iterator<Row<T>> iterator() {
		return new Iterator<Row<T>>() {
			private Row<T> next = firstRow;

			public boolean hasNext() {
				return next != null;
			}

			public Row<T> next() {
				Row<T> result = next;
				next = next.getNextRow();
				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(super.toString());
		sb.append("\n");
		for (Row<T> row : this) {
			sb.append("\t");
			sb.append(row.toString());
			sb.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	public void pack() {
		boolean changed;
		do {
			changed = false;
			for (Row<T> r : this) {
				changed |= r.tryInterleaveWith(r.getPrevRow());
			}
			//System.out.println();
			for (Row<T> r : this) {
				changed |= r.tryInterleaveWith(r.getNextRow());
			}
			//System.out.println();
		} while (changed);
	}
}
