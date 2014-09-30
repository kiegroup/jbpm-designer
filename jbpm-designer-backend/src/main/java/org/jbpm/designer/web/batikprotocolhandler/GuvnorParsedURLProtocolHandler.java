package org.jbpm.designer.web.batikprotocolhandler;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLProtocolHandler;
import org.jbpm.designer.web.profile.IDiagramProfile;


public class GuvnorParsedURLProtocolHandler implements ParsedURLProtocolHandler {
    private IDiagramProfile profile;
    
    public GuvnorParsedURLProtocolHandler() {}
    
    public GuvnorParsedURLProtocolHandler(IDiagramProfile profile) {
        this.profile = profile;
    }
    
    public String getProtocolHandled() {
        return "http";
    }

    public GuvnorParsedURLData parseURL(ParsedURL basepurl, String urlStr) {
        return parseURL(urlStr);
    }

    public GuvnorParsedURLData parseURL(String urlStr) {
        return new GuvnorParsedURLData(profile, urlStr);
    }
}
