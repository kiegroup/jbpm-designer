package org.jbpm.designer.util;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.repository.UriUtils;

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
}