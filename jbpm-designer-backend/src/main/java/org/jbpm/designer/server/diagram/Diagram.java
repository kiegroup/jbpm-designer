/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.server.diagram;

import java.util.ArrayList;

public class Diagram extends Shape {

    StencilSet stencilset;
    ArrayList<String> ssextensions;
    ArrayList<Shape> shapes;

    /**
     * Normal shape constructor with additional stencilset
     * 
     * @param resourceId
     *            resourceId of the diagram shape
     * @param stencil
     *            stencil usually Diagram
     * @param stencilset
     *            StencilSet with url and namespace
     */
    public Diagram(String resourceId, StencilType stencil, StencilSet stencilset) {
        super(resourceId, stencil);
        this.stencilset = stencilset;
    }

    /**
     * @param resourceId
     * @param stencil
     */
    public Diagram(String resourceId, StencilType stencil) {
        super(resourceId, stencil);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param resourceId
     */
    public Diagram(String resourceId) {
        super(resourceId);
        // TODO Auto-generated constructor stub
    }

    /**
     * Gives the stenilset of a diagram
     * 
     * @return the stencilset
     */
    public StencilSet getStencilset() {
        return stencilset;
    }

    /**
     * Set a new StencilSet
     * 
     * @param stencilset
     *            the stencilset to set type StencilSet
     */
    public void setStencilset(StencilSet stencilset) {
        this.stencilset = stencilset;
    }

    /**
     * Gives an ArrayList<String> which contains all StencilSet- Extension
     * identifier
     * 
     * @return the ssextensions
     */
    public ArrayList<String> getSsextensions() {
        if (ssextensions == null) {
            ssextensions = new ArrayList<String>();
        }

        return ssextensions;
    }

    /**
     * set a new StencilSet-Extension ArrayList<String>
     * 
     * @param ssextensions
     *            the ssextensions to set
     */
    public void setSsextensions(ArrayList<String> ssextensions) {
        this.ssextensions = ssextensions;
    }

    /**
     * Add an additional SSExtension
     * 
     * @param ssExt
     *            the ssextension to set
     */
    public boolean addSsextension(String ssExt) {
        if (this.ssextensions == null)
            this.ssextensions = new ArrayList<String>();
        return this.ssextensions.add(ssExt);
    }

    /**
     * returns all shapes of a diagram
     * 
     * @return the shapes
     */
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /**
     * set a new ArrayList<Shape>
     * 
     * @param shapes
     *            the shapes to set
     */
    public void setShapes(ArrayList<Shape> shapes) {
        this.shapes = shapes;
    }

    /**
     * Add an additional shape to the diagram
     * 
     * @param shape
     *            the shape to set
     */
    public boolean addShapes(Shape shape) {
        return this.shapes.add(shape);
    }

    public Shape getParent() {
        return null;
    }

    // TODO: implement
    public String getJSON() {
        return "not yet implemented";
    }

}
