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
