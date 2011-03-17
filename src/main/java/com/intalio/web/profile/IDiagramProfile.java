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
package com.intalio.web.profile;

import java.util.Collection;

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
     * Parser to produce the final model to be saved.
     * @author Antoine Toulme
     *
     */
    public interface IDiagramMarshaller {
        
        /**
         * @param jsonModel the model
         * @return the string representation of the serialized model.
         */
        public String parseModel(String jsonModel);
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
        public String parseModel(String xmlModel, IDiagramProfile profile);
    }
}
