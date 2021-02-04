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
package org.jbpm.designer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xml.utils.XMLChar;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testToBPMNIdentifierNoPackage() {
        // these values should NOT experience
        // changes when converted to id strting
        List<String> testIdShouldNotChange = Arrays.asList("myprocess",
                                                           "process11",
                                                           "pro2cess1",
                                                           "process",
                                                           "my11process1");
        List<String> toIdentifierShouldNotChange = new ArrayList<>();

        for (String id : testIdShouldNotChange) {
            toIdentifierShouldNotChange.add(Utils.toBPMNIdentifier(id));
        }

        Assert.assertEquals(testIdShouldNotChange,
                            toIdentifierShouldNotChange);

        Assert.assertTrue(areValidNcNames(testIdShouldNotChange));

        // these values SHOULD experience
        // changes when converted to id strting
        List<String> testIdShouldChange = Arrays.asList("1my=process",
                                                        "111@process~",
                                                        "!1process1@!",
                                                        "22^^process*",
                                                        "22my%11proces%s1()");

        List<String> toIdentifierShouldChange = new ArrayList<>();

        for (String id : testIdShouldChange) {
            toIdentifierShouldChange.add(Utils.toBPMNIdentifier(id));
        }

        Assert.assertNotEquals(testIdShouldChange,
                               toIdentifierShouldChange);

        Assert.assertTrue(toIdentifierShouldChange.containsAll(
                Arrays.asList("my3Dprocess",
                              "process7E",
                              "process14021",
                              "E5Eprocess2A",
                              "my2511proces25s12829")
        ));

        Assert.assertTrue(areValidNcNames(toIdentifierShouldChange));
    }

    @Test
    public void testToBPMNIdentifierWithPackage() {
        // these values should NOT experience
        // changes when converted to id strting
        List<String> testIdShouldNotChange = Arrays.asList("mypackage.myprocess",
                                                           "mypackage.process1",
                                                           "mypackage.pro2cess1",
                                                           "mypackage.process",
                                                           "mypackage.my11process1");
        List<String> toIdentifierShouldNotChange = new ArrayList<>();

        for (String id : testIdShouldNotChange) {
            toIdentifierShouldNotChange.add(Utils.toBPMNIdentifier(id));
        }

        Assert.assertEquals(testIdShouldNotChange,
                            toIdentifierShouldNotChange);

        Assert.assertTrue(areValidNcNames(testIdShouldNotChange));

        // these values SHOULD experience
        // changes when converted to id strting
        List<String> testIdShouldChange = Arrays.asList("1mypackage.1my=process",
                                                        "111==mypackage.111@process~",
                                                        "@!mypackge112.!1process1@!",
                                                        "^^&mypa11ckage.22^^process*",
                                                        "()my$$packa@@ge**^.22my%11proces%s1()");

        List<String> toIdentifierShouldChange = new ArrayList<>();

        for (String id : testIdShouldChange) {
            toIdentifierShouldChange.add(Utils.toBPMNIdentifier(id));
        }

        Assert.assertNotEquals(testIdShouldChange,
                               toIdentifierShouldChange);

        Assert.assertTrue(toIdentifierShouldChange.containsAll(
                Arrays.asList("mypackage.1my3Dprocess",
                              "D3Dmypackage.11140process7E",
                              "mypackge112.211process14021",
                              "E5E26mypa11ckage.225E5Eprocess2A",
                              "my2424packa4040ge2A2A5E.22my2511proces25s12829")
        ));

        Assert.assertTrue(areValidNcNames(toIdentifierShouldChange));
    }

    public boolean areValidNcNames(List<String> names) {
        for (String id : names) {
            if (!XMLChar.isValidNCName(id)) {
                return false;
            }
        }
        return true;
    }
}
