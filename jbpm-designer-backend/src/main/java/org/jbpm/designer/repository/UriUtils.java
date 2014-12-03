package org.jbpm.designer.repository;

import org.uberfire.java.nio.EncodingUtil;

public class UriUtils {

    private static final String URL_ENCODED_REGEX = ".*%\\w{1,}.*";

    public static String encode(String value) {
        if(value == null) {
            return value;
        }
        if (value.matches(URL_ENCODED_REGEX)) {
            return value;
        }
        return EncodingUtil.encodePath(value);
    }

    public static String decode(String value) {
        if(value == null) {
            return value;
        }
        return EncodingUtil.decode(value);

    }
}
