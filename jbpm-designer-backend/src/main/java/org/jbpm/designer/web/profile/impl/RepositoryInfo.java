package org.jbpm.designer.web.profile.impl;

import org.jbpm.designer.web.profile.IDiagramProfile;

public class RepositoryInfo {
    private static final String REPOSITORY_ID = "designer.repository.id";
    private static final String REPOSITORY_ROOT = "designer.repository.root";
    private static final String REPOSITORY_PROTOCOL = "designer.repository.protocol";
    private static final String REPOSITORY_HOST = "designer.repository.host";
    private static final String REPOSITORY_SUBDOMAIN = "designer.repository.subdomain";
    private static final String REPOSITORY_USR = "designer.repository.usr";
    private static final String REPOSITORY_PWD = "designer.repository.pwd";

    public static String getRepositoryId(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_ID)) ? profile.getRepositoryId() : System.getProperty(REPOSITORY_ID);
    }

    public static String getRepositoryRoot(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_ROOT)) ? profile.getRepositoryRoot() : System.getProperty(REPOSITORY_ROOT);
    }

    public static String getRepositoryProtocol(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_PROTOCOL)) ? profile.getRepositoryProtocol() : System.getProperty(REPOSITORY_PROTOCOL);
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
            return profile.getRepositoryHost();
        }
    }

    public static String getRepositoryUsr(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_USR)) ? profile.getRepositoryUsr() : System.getProperty(REPOSITORY_USR);
    }

    public static String getRepositoryPwd(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_PWD)) ? profile.getRepositoryPwd() : System.getProperty(REPOSITORY_PWD);
    }

    public static String getRepositorySubdomain(IDiagramProfile profile) {
        return isEmpty(System.getProperty(REPOSITORY_SUBDOMAIN)) ? profile.getRepositorySubdomain() : System.getProperty(REPOSITORY_SUBDOMAIN);
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
