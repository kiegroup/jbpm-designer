/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.shared;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.AbstractNullableMarshaller;

@ClientMarshaller(AssignmentData.class)
@ServerMarshaller(AssignmentData.class)
public class AssignmentDataMarshaller
        extends AbstractNullableMarshaller<AssignmentData>

{
    public AssignmentData doNotNullDemarshall(EJValue o, MarshallingSession ctx) {
        EJObject obj = o.isObject();
        String dataInputSet = obj.get("dataInputSet").isString().stringValue();
        String dataOutputSet = obj.get("dataOutputSet").isString().stringValue();
        String processVars = obj.get("processVars").isString().stringValue();
        String assignments = obj.get("assignments").isString().stringValue();
        String dataTypes = obj.get("dataTypes").isString().stringValue();
        return new AssignmentData(dataInputSet, dataOutputSet, processVars, assignments, dataTypes);
    }

    public String doNotNullMarshall(AssignmentData o, MarshallingSession ctx) {
        return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + AssignmentData.class.getName() + "\"," +

                "\"" + SerializationParts.OBJECT_ID + "\":\"" + o.hashCode() + "\"," +
                "\"" + "inputVariables" + "\":\"" + o.getInputVariablesString() + "\"," +
                "\"" + "outputVariables" + "\":\"" + o.getOutputVariablesString() + "\"," +
                "\"" + "processVariables" + "\":\"" + o.getProcessVariablesString() + "\"," +
                "\"" + "assignments" + "\":\"" + o.getAssignmentsString() + "\"," +
                "\"" + "dataTypes" + "\":\"" + o.getDataTypesString() + "\"}";
    }

    @Override public AssignmentData[] getEmptyArray() {
        return new AssignmentData[0];
    }
}
