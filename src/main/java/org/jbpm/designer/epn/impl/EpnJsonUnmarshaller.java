package org.jbpm.designer.epn.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.jbpm.designer.epn.EpnMarshallerHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;



/**
 * @author Tihomir Surdilovic
 * 
 *         an unmarshaller to transform JSON into EPN elements.
 * 
 */
public class EpnJsonUnmarshaller {
    private Map<Object, String> _objMap = new HashMap<Object, String>();
    private Map<String, Object> _idMap = new HashMap<String, Object>();
    
    private List<EpnMarshallerHelper> _helpers;
    
    public EpnJsonUnmarshaller() {
        _helpers = new ArrayList<EpnMarshallerHelper>();
        // load the helpers to place them in field
        if (getClass().getClassLoader() instanceof BundleReference) {
            BundleContext context = ((BundleReference) getClass().getClassLoader()).
                getBundle().getBundleContext();
            try {
                ServiceReference[] refs = context.getAllServiceReferences(
                        EpnMarshallerHelper.class.getName(), null);
                for (ServiceReference ref : refs) {
                    EpnMarshallerHelper helper = (EpnMarshallerHelper) context.getService(ref);
                    _helpers.add(helper);
                }
            } catch (InvalidSyntaxException e) {
            }
        }
    }
    
    public Object unmarshall(String json) throws JsonParseException, IOException {
        return ""; //TODO empty for now until we finish the epn ecore model
    }
}
