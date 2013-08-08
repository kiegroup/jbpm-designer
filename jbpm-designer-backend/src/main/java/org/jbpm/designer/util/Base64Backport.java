package org.jbpm.designer.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Backports the commons-codec isBase64(String) method from commons-codec 1.5
 * because EAP requires us to use commons-codec 1.4.
 */
// TODO Remove this class when we upgrade to commons-codec 1.5.
public class Base64Backport extends Base64 {

    public static boolean isBase64(final String base64) {
        return base64 == null ? false : isBase64(StringUtils.getBytesUtf8(base64));
    }

    private static boolean isBase64(final byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; i++) {
            if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
                return false;
            }
            }
        return true;
    }

    private static boolean isWhiteSpace(byte byteToCheck) {
        switch (byteToCheck) {
            case ' ' :
            case '\n' :
            case '\r' :
            case '\t' :
                return true;
            default :
                return false;
        }
    }

}
