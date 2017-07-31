/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
