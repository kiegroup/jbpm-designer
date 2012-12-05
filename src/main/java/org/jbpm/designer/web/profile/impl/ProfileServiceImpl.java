/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.web.profile.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.repository.guvnor.GuvnorRepository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileFactory;
import org.jbpm.designer.web.profile.IDiagramProfileService;


/**
 * a service to register profiles.
 * 
 * @author Antoine Toulme
 * @author Tihomir Surdilovic
 * 
 */
public class ProfileServiceImpl implements IDiagramProfileService {
	
	public static ProfileServiceImpl INSTANCE = new ProfileServiceImpl();

    private Map<String, IDiagramProfile> _registry = 
        new HashMap<String, IDiagramProfile>();
    private Set<IDiagramProfileFactory> _factories = 
        new HashSet<IDiagramProfileFactory>();
    private IDiagramProfile userProfile;

    /**
     * Initialize the service with a context
     * @param context the servlet context to initialize the profile.
     */
    public void init(ServletContext context) {
        _registry.put("default", new DefaultProfileImpl(context));
        _registry.put("jbpm", new JbpmProfileImpl(context));
        _registry.put("drools", new JbpmProfileImpl(context));
        _registry.put("epn", new EpnProfileImpl(context));
    }
    
    private Map<String, IDiagramProfile> assembleProfiles(HttpServletRequest request) {
        Map<String, IDiagramProfile> profiles = new HashMap<String, IDiagramProfile>(_registry);
        if (request != null) {
            for (IDiagramProfileFactory factory : _factories) {
                for (IDiagramProfile  p : factory.getProfiles(request)) {
                    profiles.put(p.getName(), p);
                }
            }
        }
        return profiles;
    }
    
    public IDiagramProfile findProfile(HttpServletRequest request, String name) {
        userProfile =  assembleProfiles(request).get(name);
        return userProfile;
    }

    public Collection<IDiagramProfile> getProfiles(HttpServletRequest request) {
        return assembleProfiles(request).values();
    }

    public Set<IDiagramProfileFactory> getFactories() {
    	return _factories;
    }

}
