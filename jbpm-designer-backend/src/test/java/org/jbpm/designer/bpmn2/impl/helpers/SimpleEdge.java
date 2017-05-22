package org.jbpm.designer.bpmn2.impl.helpers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class SimpleEdge {

    private String name;
    private List<Point> points = new ArrayList<Point>();

    private SimpleEdge(String name) {
        this.name = name;
    }

    public static SimpleEdge createEdge(String name) {
        return new SimpleEdge(name);
    }

    public SimpleEdge addPoint(float x,
                               float y) {
        points.add(new Point((int) x,
                             (int) y));
        return this;
    }

    public SimpleEdge addPoint(int x,
                               int y) {
        points.add(new Point(x,
                             y));
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        SimpleEdge se = (SimpleEdge) other;
        return this.name.equals(se.name) && this.points.equals(se.points);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        String result = name + " " + points.toString();
        return result.replace("java.awt.Point",
                              "").replace("\n",
                                          "\\n");
    }
}
