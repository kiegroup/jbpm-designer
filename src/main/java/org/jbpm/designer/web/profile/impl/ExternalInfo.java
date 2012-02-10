package org.jbpm.designer.web.profile.impl;

import org.jbpm.designer.web.profile.IDiagramProfile;

public class ExternalInfo {
    private static final String EXTERNAL_PROTOCOL = "oryx.external.protocol";
    private static final String EXTERNAL_HOST = "oryx.external.host";
    private static final String EXTERNAL_USR = "oryx.external.usr";
    private static final String EXTERNAL_PWD = "oryx.external.pwd";
    
    public static String getExternalProtocol(IDiagramProfile profile) {
        return isEmpty(System.getProperty(EXTERNAL_PROTOCOL)) ? profile.getExternalLoadURLProtocol() : System.getProperty(EXTERNAL_PROTOCOL);
    }
    
    public static String getExternalHost(IDiagramProfile profile) {
        if(!isEmpty(System.getProperty(EXTERNAL_HOST))) {
            String retStr = System.getProperty(EXTERNAL_HOST);
            if(retStr.startsWith("/")){
                retStr = retStr.substring(1);
            }
            if(retStr.endsWith("/")) {
                retStr = retStr.substring(0,retStr.length() - 1);
            }
            return retStr;
        } else {
            return profile.getExternalLoadURLHostname();
        }
        
    }
    
    public static String getExternalUsr(IDiagramProfile profile) {
        return isEmpty(System.getProperty(EXTERNAL_USR)) ? profile.getUsr() : System.getProperty(EXTERNAL_USR);
    }
    
    public static String getExternalPwd(IDiagramProfile profile) {
        return isEmpty(System.getProperty(EXTERNAL_PWD)) ? profile.getPwd() : System.getProperty(EXTERNAL_PWD);
    }
    
   private static boolean isEmpty(final CharSequence str) {
       if ( str == null || str.length() == 0 ) {
           return true;
       }
       for ( int i = 0, length = str.length(); i < length; i++ ){
           if ( str.charAt( i ) != ' ' )  {
               return false;
           }
       }
       return true;
   }
}
