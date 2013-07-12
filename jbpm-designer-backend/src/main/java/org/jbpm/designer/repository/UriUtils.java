package org.jbpm.designer.repository;

import org.yaml.snakeyaml.util.UriEncoder;

public class UriUtils {


    public static String encode(String value) {

        return UriEncoder.encode(value);
    }

    public static String decode(String value) {

        return UriEncoder.decode(value);

    }
}
