/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.util;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

public class Utils {

    public static String getUUID(HttpServletRequest request) {
        return getEncodedParam(request, "uuid");
    }

    public static String getEncodedParam(HttpServletRequest request, String paramName) {
        String uniqueId = request.getParameter(paramName);
        if (uniqueId != null && Base64Backport.isBase64(uniqueId)) {
            byte[] decoded = Base64.decodeBase64(uniqueId);
            try {
                uniqueId = new String(decoded, "UTF-8");

            } catch (UnsupportedEncodingException e) {

            }
        }

        return uniqueId;
    }

    /**
     * Converts a string to a valid BPMN Identifier,
     * replacing invalid characters, e.g. Unicode chars
     * with their URL encoded equivalents, without the '%'. For example
     * "BP日" -> "BPE697A5"
     * @param str - input string
     * @return - valid BPMN id created from input string
     */
    public static String toBPMNIdentifier(String str) {

        str = str.replace(" ", "");
        StringBuilder sb = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i == 0) {
                if (isNCNameStart(c)) {
                    sb.append(c);
                } else {
                    sb.append(convertNonNCNameChar(c));
                }
            } else {
                if (isNCNamePart(c)) {
                    sb.append(c);
                } else {
                    sb.append(convertNonNCNameChar(c));
                }
            }
        }
        return sb.toString();
    }

    /*
     * Tests whether a character is a valid start character in xsd:NCName
     */
    protected static boolean isNCNameStart(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_');
    }

    /*
     * Tests whether a character is a valid character in xsd:NCName
     */
    protected static boolean isNCNamePart(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || Character.isDigit(c) || c == '-' || c == '_' || c == '.');
    }

    /*
     * Convert character to hex representation by representing each byte's
     * value as upper-cased hex string, as in URL-encoding.
     */
    protected static String convertNonNCNameChar(char c) {
        String str = "" + c;
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(4);

        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%x", bytes[i]));
        }
        return sb.toString().toUpperCase();
    }
}