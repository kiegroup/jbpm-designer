/***************************************
 * Copyright (c) Intalio, Inc 2010
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
****************************************/
package com.intalio.web.repository;

import javax.servlet.http.HttpServlet;

/**
 * @author Antoine Toulme
 * 
 * This interface defines the way the models should be saved and loaded.
 *
 */
public interface IUUIDBasedRepository {
    
    /**
     * Configure the repository in the context of the servlet
     * @param servlet the servlet which will use this repository.
     */
    public void configure(HttpServlet servlet);
    
    /**
     * @param uuid the id of the model.
     * @return the model as a set of bytes.
     */
    public byte[] load(String uuid);
    
    /**
     * Saves the model inside the repository.
     * @param uuid the id of the model
     * @param json the json model
     * @param svg the svg representation of the model
     * @param bpmn the bpmn model
     */
    public void save(String uuid, String json, String svg, String bpmn);

}
