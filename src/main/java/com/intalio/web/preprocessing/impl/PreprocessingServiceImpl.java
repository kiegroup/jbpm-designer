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
package com.intalio.web.preprocessing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.intalio.web.preprocessing.IDiagramPreprocessingService;
import com.intalio.web.preprocessing.IDiagramPreprocessingUnit;
import com.intalio.web.profile.IDiagramProfile;

/**
 * 
 * @author Tihomir Surdilovic
 */
public class PreprocessingServiceImpl implements IDiagramPreprocessingService {

    public static PreprocessingServiceImpl INSTANCE = new PreprocessingServiceImpl();
    private Map<String, IDiagramPreprocessingUnit> _registry = new HashMap<String, IDiagramPreprocessingUnit>();
    
    
    @Override
    public Collection<IDiagramPreprocessingUnit> getRegisteredPreprocessingUnits(
            HttpServletRequest request) {
        Map<String, IDiagramPreprocessingUnit> preprocessingUnits = new HashMap<String, IDiagramPreprocessingUnit>(_registry);
        return new ArrayList<IDiagramPreprocessingUnit>(preprocessingUnits.values());
    }
    
    @Override
    public IDiagramPreprocessingUnit findPreprocessingUnit(
            HttpServletRequest request, IDiagramProfile profile) {
        Map<String, IDiagramPreprocessingUnit> preprocessingUnits = new HashMap<String, IDiagramPreprocessingUnit>(_registry);
        return preprocessingUnits.get(profile.getName());
    }
    
    @Override
    public void init(ServletContext context) {
        _registry.put("default", new DefaultPreprocessingUnit(context));
        _registry.put("jbpm", new JbpmPreprocessingUnit(context));
    }
    
}
