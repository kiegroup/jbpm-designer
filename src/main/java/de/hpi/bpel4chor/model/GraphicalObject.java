package de.hpi.bpel4chor.model;

import de.hpi.bpel4chor.util.Output;
import de.hpi.diagram.OryxUUID;

/**
 * A graphical object is an object with a graphical representation that can
 * occur in the diagram. Each graphical has a unique id.
 */
public class GraphicalObject {

    private String id = null;

    /**
     * Constructor. Initializes the graphical object and generates a unique id.
     * 
     * @param output
     *            The output to print errors to.
     */
    protected GraphicalObject(Output output) {
        this.id = OryxUUID.generate();
    }

    /**
     * Constructor. Initializes the graphical object and sets the given id.
     * 
     * @param id
     *            The id of the graphical object.
     */
    protected GraphicalObject(String id) {
        this.id = id;
    }

    /**
     * @return The id of the graphical object.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id of the graphical object.
     * 
     * @param id
     *            The new id.
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GraphicalObject) {
            if (((GraphicalObject) obj).getId().equals(this.id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
