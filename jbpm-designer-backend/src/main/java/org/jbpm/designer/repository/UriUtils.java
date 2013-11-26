package org.jbpm.designer.repository;

import org.apache.commons.httpclient.URIException;

public class UriUtils {


    public static String encode(String value) {
        try {
            return org.apache.commons.httpclient.util.URIUtil.encodePath(value);
        } catch (URIException e) {
            throw new IllegalArgumentException("Invalid value " + value + " given, error: " + e.getMessage(), e);
        }
    }

    public static String decode(String value) {

        try {
            return org.apache.commons.httpclient.util.URIUtil.decode(value);
        } catch (URIException e) {
            throw new IllegalArgumentException("Invalid value " + value + " given, error: " + e.getMessage(), e);
        }

    }
}
