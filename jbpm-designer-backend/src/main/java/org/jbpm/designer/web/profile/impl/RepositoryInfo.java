package org.jbpm.designer.web.profile.impl;

import org.jbpm.designer.web.profile.IDiagramProfile;

public class RepositoryInfo {

    private static final String REPOSITORY_PROTOCOL = "designer.repository.protocol";
    private static final String REPOSITORY_HOST = "designer.repository.host";
    private static final String REPOSITORY_SUBDOMAIN = "designer.repository.subdomain";
    private static final String REPOSITORY_USR = "designer.repository.usr";
    private static final String REPOSITORY_PWD = "designer.repository.pwd";


    public static String getRepositoryProtocol(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_PROTOCOL)) ? "http" : System.getProperty(REPOSITORY_PROTOCOL);
    }

    public static String getRepositoryHost(IDiagramProfile profile) {
        if(!isEmpty(System.getProperty(REPOSITORY_HOST))) {
            String retStr = System.getProperty(REPOSITORY_HOST);
            if(retStr.startsWith("/")){
                retStr = retStr.substring(1);
            }
            if(retStr.endsWith("/")) {
                retStr = retStr.substring(0,retStr.length() - 1);
            }
            return retStr;
        } else {
            return "localhost:8080";//profile.getRepositoryHost();
        }
    }

    public static String getRepositoryUsr(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_USR)) ? "admin" : System.getProperty(REPOSITORY_USR);
    }

    public static String getRepositoryPwd(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_PWD)) ? "admin" : System.getProperty(REPOSITORY_PWD);
    }

    public static String getRepositorySubdomain(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_SUBDOMAIN)) ? "drools-guvnor/org.drools.guvnor.Guvnor/oryxeditor" : System.getProperty(REPOSITORY_SUBDOMAIN);
    }

    private static boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }
        for ( int i = 0, length = str.length(); i < length; i++ ) {
            if ( str.charAt( i ) != ' ' )  {
                return false;
            }
        }
        return true;
    }
}
