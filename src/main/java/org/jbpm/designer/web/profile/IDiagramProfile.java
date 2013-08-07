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
package org.jbpm.designer.web.profile;

import java.util.Collection;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A profile for the editor to choose which stencilset 
 * and which plugins should be loaded.
 * 
 * @author Antoine Toulme
 *
 */
public interface IDiagramProfile {
    
    /**
     * @return the name of the profile
     * it will be passed by the user when opening the editor.
     */
    public String getName();
    /**
     * @return the title of the profile.
     */
    public String getTitle();

    /**
     * @return the stencil set used by the profile.
     */
    public String getStencilSet();
    
    /**
     * @return the stencil set extensions used by the profile
     */
    public Collection<String> getStencilSetExtensions();
    
    public String getSerializedModelExtension();
    
    /**
     * @return the stencil url used by the profile.
     */
    public String getStencilSetURL();
    
    /**
     * @return stencil namespace url.
     */
    public String getStencilSetNamespaceURL();
    
    /**
     * @return stencil set extension url used by the profile.
     */
    public String getStencilSetExtensionURL();
    
    /**
     * @return the plugins to load for the profile.
     */
    public Collection<String> getPlugins();
    
    /**
     * @return a marshaller to transform the json into the final model.
     */
    public IDiagramMarshaller createMarshaller();
    
    /**
     * @return a unmarshaller to transform the final model into json.
     */
    public IDiagramUnmarshaller createUnmarshaller();
    
    /**
     * @return the load url protocol for external resource loading.
     */
    public String getExternalLoadURLProtocol();
    
    /**
     * @return the load url hostname for external resource loading.
     */
    public String getExternalLoadURLHostname();
    
    /**
     * @return the load url subdomain for external resource loading.
     */
    public String getExternalLoadURLSubdomain();
    
    /**
     * @return the user for external resource.
     */
    public String getUsr();
    
    /**
     * @return the pwd for external resource.
     */
    public String getPwd();

    /**
     * @return the local history enabled.
     */
    public String getLocalHistoryEnabled();

    /**
     * @return the local history timeout.
     */
    public String getLocalHistoryTimeout();

    /**
     * @return the timeout used for http connections.
     */
    public Integer getConnectionTimeout();
    
    /**
     * @return the pwd enc resource.
     */
    public String getPwdEnc();
    /**
     * Parser to produce the final model to be saved.
     * @author Antoine Toulme
     *
     */
    public interface IDiagramMarshaller {
        
        /**
         * @param jsonModel the model
         * @return the string representation of the serialized model.
         */
        public String parseModel(String jsonModel, String preProcessingData);
        public Definitions getDefinitions(String jsonModel, String preProcessingData); 
        public Resource getResource(String jsonModel, String preProcessingData);
    }
    
    /**
     * Parser to produce the final model to be saved.
     * @author Tihomir Surdilovic
     *
     */
    public interface IDiagramUnmarshaller {
        
        /**
         * @param bpmn2 xml model
         * @param profile process profile.
         * @return the json model
         */
        public String parseModel(String xmlModel, IDiagramProfile profile, String preProcessingData);
    }
}
