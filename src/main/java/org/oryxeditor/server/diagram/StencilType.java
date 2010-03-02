package org.oryxeditor.server.diagram;

/**
 * @author Philipp
 * Represents a Stencil of an shape with all
 * attributes of the JSONObject stencil which is
 *  associated with a shape
 */
public class StencilType {
		String id;

		/** Construct a new stencilType with a unique id
		 * @param id
		 */
		public StencilType(String id) {
			super();
			this.id = id;
		}

		/** Gives the unique id of a stencil
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/** set a new unique id for a stencilType
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return id.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StencilType other = (StencilType) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
}
