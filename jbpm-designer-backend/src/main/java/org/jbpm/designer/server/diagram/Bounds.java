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

package org.jbpm.designer.server.diagram;

/**
 * @author Philipp Bounds represent the svg bound of a shape
 */
public class Bounds {

    Point lowerRight;
    Point upperLeft;

    /**
     * Constructs a Bounds with initial lowerRight and upperleft
     * @param lowerRight
     * @param upperLeft
     */
    public Bounds(Point lowerRight,
                  Point upperLeft) {
        super();
        this.lowerRight = lowerRight;
        this.upperLeft = upperLeft;
    }

    /**
     * @return the lowerRight of a Bounds
     */
    public Point getLowerRight() {
        return lowerRight;
    }

    /**
     * @param lowerRight the lowerRight to set
     */
    public void setLowerRight(Point lowerRight) {
        this.lowerRight = lowerRight;
    }

    /**
     * @return the upperLeft of a Bounds
     */
    public Point getUpperLeft() {
        return upperLeft;
    }

    /**
     * @param upperLeft the upperLeft to set
     */
    public void setUpperLeft(Point upperLeft) {
        this.upperLeft = upperLeft;
    }
}
