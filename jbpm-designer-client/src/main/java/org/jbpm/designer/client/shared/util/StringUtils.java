/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.shared.util;

import com.google.gwt.http.client.URL;

/**
 * String utility functions
 */
public class StringUtils {

    /**
     * Puts strings inside quotes and numerics are left as they are.
     * @param str
     * @return
     */
    public static String createQuotedConstant(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return "\"" + str + "\"";
        }
        return str;
    }

    /**
     * Removes double-quotes from around a string
     * @param str
     * @return
     */
    public static String createUnquotedConstant(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0,
                                str.length() - 1);
        }
        return str;
    }

    /**
     * Returns true if string starts and ends with double-quote
     * @param str
     * @return
     */
    public static boolean isQuotedConstant(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return (str.startsWith("\"") && str.endsWith("\""));
    }

    /**
     * URLEncode a string
     * @param s
     * @return
     */
    public static String urlEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        return URL.encodeQueryString(s);
    }

    /**
     * URLDecode a string
     * @param s
     * @return
     */
    public static String urlDecode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return URL.decodeQueryString(s);
    }
}
