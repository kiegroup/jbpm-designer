package org.jbpm.designer.repository;

import org.apache.commons.httpclient.URIException;

public class UriUtils {

    private static final String URL_ENCODED_REGEX = ".*%\\w{1,}.*";

    public static String encode(String value) {
        if(value == null) {
            return value;
        }
        if (value.matches(URL_ENCODED_REGEX)) {
            return value;
        }
        try {
            return org.apache.commons.httpclient.util.URIUtil.encodePath(value);
        } catch (URIException e) {
            throw new IllegalArgumentException("Invalid value " + value + " given, error: " + e.getMessage(), e);
        }
    }

    public static String decode(String value) {
        if(value == null) {
            return value;
        }
        try {
            return org.apache.commons.httpclient.util.URIUtil.decode(value);
        } catch (URIException e) {
            throw new IllegalArgumentException("Invalid value " + value + " given, error: " + e.getMessage(), e);
        }

    }
}
