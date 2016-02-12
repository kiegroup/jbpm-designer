var elementDataInfo = {
    "UserTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "User Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/swimlane/process.participant.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,actors,groupid,subject,description,skippable"
    },
    "SendTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Send Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,messageref,description,skippable"
    },
    "ReceiveTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Receive Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,messageref,description,skippable"
    },
    "ManualTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Manual Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,taskname,description,skippable"
    },
    "ServiceTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Service Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,taskname,description,skippable,serviceoperation,serviceinterface,serviceimplementation"
    },
    "BusinessRuleTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Business Rule Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,taskname,description,skippable,ruleflowgroup"
    },
    "ScriptTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Script Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,taskname,description,skippable,script,script_language"
    },
    "Task": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,taskname,description,skippable"
    },
    "WorkItem": {
        "group": "WorkItems",
        "groupdispname": "Work Items",
        "dispname": "Work Item",
        "groupicon": "images/servicenode.png",
        "icon": "images/servicenode.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,description,skippable"
    },
    "ReusableSubprocess": {
        "group": "Subprocesses",
        "groupdispname":"Subprocesses",
        "dispname": "Reusable Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,calledelement,datainputset,dataoutputset,assignments,independent"
    },
    "MultipleInstanceSubprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Multiple Instance Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,multipleinstancecollectioninput,multipleinstancecollectionoutput,multipleinstancedatainput,multipleinstancedataoutput,multipleinstancecompletioncondition"
    },

    "Subprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Embedded Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,vardefs"
    },
    "AdHocSubprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Ad-Hoc Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,adhocordering,adhoccompletioncondition,adhoccancelremaininginstances,vardefs"
    },
    "EventSubprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Event Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/event.subprocess.png",
        "properties": "name,documentation,vardefs"
    },
    "Exclusive_Databased_Gateway": {
        "group": "Gateways",
        "groupdispname": "Gateways",
        "dispname": "Exclusive Databased Gateway",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/gateway/parallel.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/gateway/exclusive.databased.png",
        "properties": "name,documentation,defaultgate"
    },
    "EventbasedGateway": {
        "group": "Gateways",
        "groupdispname": "Gateways",
        "dispname": "Event-Based Gateway",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/gateway/parallel.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/gateway/eventbased.png",
        "properties": "name,documentation"
    },
    "ParallelGateway": {
        "group": "Gateways",
        "groupdispname": "Gateways",
        "dispname": "Parallel Gateway",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/gateway/parallel.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/gateway/parallel.png",
        "properties": "name,documentation"
    },
    "InclusiveGateway": {
        "group": "Gateways",
        "groupdispname": "Gateways",
        "dispname": "Inclusive Gateway",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/gateway/parallel.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/gateway/inclusive.png",
        "properties": "name,documentation,defaultgate"
    },
    "Lane": {
        "group": "Lanes",
        "groupdispname": "Lanes",
        "dispname": "Swimlane",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/swimlane/lane.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/swimlane/lane.png",
        "properties": "name,documentation"
    },
    "Group": {
        "group": "Artifacts",
        "groupdispname": "Artifacts",
        "dispname": "Group",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/artifact/group.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/artifact/group.png",
        "properties": "name,documentation"
    },
    "TextAnnotation": {
        "group": "Artifacts",
        "groupdispname": "Artifacts",
        "dispname": "Text Annotation",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/artifact/group.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/artifact/text.annotation.png",
        "properties": "name,documentation"
    },
    "DataObject": {
        "group": "DataObjects",
        "groupdispname": "Data Objects",
        "dispname": "Data Object",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/dataobject/data.object.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/dataobject/data.object.png",
        "properties": "name,documentation"
    },
    "StartNoneEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "properties": "name,documentation,dataoutput"
    },
    "StartMessageEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Message Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/message.png",
        "properties": "name,documentation,dataoutput,messageref"
    },
    "StartTimerEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Timer Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/timer.png",
        "properties": "name,documentation,dataoutput,timedate,timeduration,timecycle,timecyclelanguage"
    },
    "StartEscalationEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Escalation Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/escalation.png",
        "properties": "name,documentation,dataoutput,escalationcode"
    },
    "StartConditionalEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Conditional Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/conditional.png",
        "properties": "name,documentation,dataoutput,conditionlanguage,conditionexpression"
    },
    "StartErrorEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Error Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/error.png",
        "properties": "name,documentation,dataoutput,errorref"
    },
    "StartCompensationEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Compensation Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/compensation.png",
        "properties": "name,documentation,dataoutput"
    },
    "StartSignalEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Signal Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/signal.png",
        "properties": "name,documentation,dataoutput,signalref"
    },
    "IntermediateMessageEventCatching": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Message Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/message.png",
        "properties": "name,documentation,messageref"
    },
    "IntermediateTimerEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Timer Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/timer.png",
        "properties": "name,documentation,timedate,timeduration,timecycle,timecyclelanguage"
    },
    "IntermediateEscalationEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Escalation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/escalation.png",
        "properties": "name,documentation,escalationcode"
    },
    "IntermediateConditionalEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Conditional Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/conditional.png",
        "properties": "name,documentation,conditionlanguage,conditionexpression"
    },
    "IntermediateErrorEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Error Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/error.png",
        "properties": "name,documentation,errorref"
    },
    "IntermediateCompensationEventCatching": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Compensation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/compensation.png",
        "properties": "name,documentation"
    },
    "IntermediateSignalEventCatching": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Signal Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "properties": "name,documentation,signalref"
    },
    "IntermediateMessageEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Message Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/message.png",
        "properties": "name,documentation,messageref"
    },
    "IntermediateEscalationEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Escalation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/escalation.png",
        "properties": "name,documentation,escalationcode"
    },
    "IntermediateCompensationEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Compensation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/compensation.png",
        "properties": "name,documentation"
    },
    "IntermediateSignalEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Signal Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/signal.png",
        "properties": "name,documentation,signalref"
    },
    "EndNoneEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "properties": "name,documentation,datainput"
    },
    "EndMessageEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Message End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/message.png",
        "properties": "name,documentation,datainput,messageref"
    },
    "EndEscalationEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Escalation End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/escalation.png",
        "properties": "name,documentation,datainput,escalationcode"
    },
    "EndErrorEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Error End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/error.png",
        "properties": "name,documentation,datainput,errorref"
    },
    "EndCancelEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Cancel End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/cancel.png",
        "properties": "name,documentation,datainput"
    },
    "EndCompensationEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Compensation End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/compensation.png",
        "properties": "name,documentation,datainput"
    },
    "EndSignalEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Signal End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/signal.png",
        "properties": "name,documentation,datainput,signalref"
    },
    "EndTerminateEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Terminate End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/terminate.png",
        "properties": "name,documentation,datainput"
    },
    "SequenceFlow": {
        "group": "ConnectingObjects",
        "groupdispname": "Connectors",
        "dispname": "Sequence Flow",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/connector/sequenceflow.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/connector/sequenceflow.png",
        "properties": "name,documentation,priority"
    },
    "Association_Undirected": {
        "group": "ConnectingObjects",
        "groupdispname": "Connectors",
        "dispname": "Undirected Association",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/connector/sequenceflow.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/connector/association.undirected.png",
        "properties": "documentation"
    },
    "Association_Unidirectional": {
        "group": "ConnectingObjects",
        "groupdispname": "Connectors",
        "dispname": "Unidirectional Association",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/connector/sequenceflow.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/connector/association.undirectional.png",
        "properties": "documentation"
    }
};

var propertyNameMappings = {
    "assignmentsview": "Assignments Count",
    "script_language": "Script Language",
    "multipleinstancecollectioninput": "MI Collection Input",
    "multipleinstancecollectionoutput" : "MI Collection Output",
    "multipleinstancedatainput": "MI Data Iput",
    "multipleinstancedataoutput": "MI Data Output",
    "multipleinstancecompletioncondition": "MI Completion Condition",
    "vardefs": "Variable Definitions",
    "adhocordering": "Ad-Hoc Ordering",
    "adhoccompletioncondition": "Ad-Hoc Completion Condition",
    "adhoccancelremaininginstances": "Ad-Hoc Cancel Remaining",
    "serviceoperation": "Service Operation",
    "serviceinterface": "Service Interface",
    "serviceimplementation": "Service Implementation",
    "dataoutputassociationsview": "Data Output Associations Count",
    "datainputassociationsview": "Data Input Associations Count",
    "datainputset": "Data Inputs",
    "dataoutputset": "Data Outputs"

};

var elementGroups = {
    "Tasks": [],
    "WorkItems":[],
    "Subprocesses": [],
    "Gateways": [],
    "Lanes": [],
    "Artifacts" : [],
    "DataObjects": [],
    "StartEvents": [],
    "EndEvents": [],
    "CatchingEvents": [],
    "ThrowingEvents": [],
    "ConnectingObjects": []
};


function processElementInfo(processJSON) {
    var childElements = jsonPath(JSON.parse(processJSON), "$.childShapes.*");
    var myElementGroups = JSON.parse(JSON.stringify(elementGroups));
    if(childElements) {
        addChildElements(childElements, myElementGroups);
    }
    return myElementGroups;
}

function addChildElements(childShapes, myElementGroups) {
    for(var i=0;i<childShapes.length;i++) {
        var element = childShapes[i];
        addElement(element, myElementGroups);
        if(element.childShapes) {
            addChildElements(element.childShapes, myElementGroups);
        }
    }
}

function addElement(element, myElementGroups) {
    var elementStencilID = element.stencil.id;
    var doProcessElement = false;
    if(elementDataInfo[elementStencilID]) {
        doProcessElement = true;
    } else {
        // check if this is a workitem
        if(element.properties && element.properties.tasktype) {
            doProcessElement = true;
        }
    }

    if(doProcessElement) {
        // for tasks we have to look at specific type
        var elementInfoDetails;
        if(elementStencilID == "Task") {
            var nodeTaskType = element.properties.tasktype;
            if(nodeTaskType == "Send") {
                elementInfoDetails = elementDataInfo["SendTask"];
            } else if(nodeTaskType == "Receive") {
                elementInfoDetails = elementDataInfo["ReceiveTask"];
            } else if(nodeTaskType == "Manual") {
                elementInfoDetails = elementDataInfo["ManualTask"];
            } else if(nodeTaskType == "Service") {
                elementInfoDetails = elementDataInfo["ServiceTask"];
            } else if(nodeTaskType == "Business Rule") {
                elementInfoDetails = elementDataInfo["BusinessRuleTask"];
            } else if(nodeTaskType == "Script") {
                elementInfoDetails = elementDataInfo["ScriptTask"];
            } else if(nodeTaskType == "None") {
                elementInfoDetails = elementDataInfo["Task"];
            } else if(nodeTaskType == "User") {
                elementInfoDetails = elementDataInfo["UserTask"];
            }
        } else {
            if(element.properties && element.properties.tasktype) {
                elementInfoDetails = elementDataInfo["WorkItem"];
            } else {
                elementInfoDetails = elementDataInfo[elementStencilID];
            }
        }

        // properties
        var propsArray = [];
        var nodeName = "";
        var elementInfoPropParts = elementInfoDetails['properties'].split(",");
        for(var i=0; i < elementInfoPropParts.length; i++) {
            var nextPart = elementInfoPropParts[i];
            if(nextPart == "name") {
                nodeName = element.properties[nextPart];
            }
            propsArray.push({
                "name":presentPropertyName(nextPart),
                "value":parsePropertyValue(nextPart,element.properties[nextPart])
            });
        }

        myElementGroups[elementInfoDetails.group].push({
            "id":element.resourceId,
            "group":elementInfoDetails["group"],
            "groupdispname":elementInfoDetails["groupdispname"],
            "dispname":elementInfoDetails["dispname"],
            "groupicon":ctx + elementInfoDetails["groupicon"],
            "icon":ctx + elementInfoDetails["icon"],
            "properties":propsArray,
            "nodename":nodeName.trim().length > 0 ? nodeName : "(Not Named)"
        });
    }
}

function parsePropertyValue(propname, propvalue) {
    if(propname == "datainputset" || propname == "dataoutputset" || propname == "datainput" || propname == "dataoutput" || propname == "vardefs") {
        var retVal = "";
        var propParts = propvalue.trim().split(",");
        for(var i=0; i < propParts.length; i++) {
            var nextPart = propParts[i];
            if(nextPart.indexOf(":") > 0) {
                var innerParts = nextPart.split(":");
                retVal += innerParts[0] +"(" + innerParts[1] + "), ";
            } else {
                if(nextPart.trim().length > 0) {
                    retVal += nextPart + "(no defined type), ";
                }
            }
        }
        if(retVal.length > 0) {
            return retVal.substring(0, retVal.length - 2);
        } else {
            return propvalue;
        }
    } else if(propname == "assignments") {
        var retVal = "";

        var valueParts = propvalue.trim().split(",");
        for(var i=0; i < valueParts.length; i++) {
            var nextPart = valueParts[i];
            if(nextPart.indexOf("=") > 0) {
                var innerParts = nextPart.split("=");
                if(innerParts[0].startsWith("[din]")) {
                    var fromPart = innerParts[0].slice(5, innerParts[0].length);
                    innerParts.shift(); // removes the first item from the array
                    var escapedp = innerParts.join('=').replace(/\#\#/g , ",");
                    escapedp = escapedp.replace(/\|\|/g , "=");

                    retVal += "(Data Input) " + fromPart + " is given value " + escapedp + ", ";
                } else if(innerParts[0].startsWith("[dout]")) {
                    var fromPart = innerParts[0].slice(6, innerParts[0].length);
                    innerParts.shift(); // removes the first item from the array
                    var escapedp = innerParts.join('=').replace(/\#\#/g , ",");
                    escapedp = escapedp.replace(/\|\|/g , "=");

                    retVal += "(Data Output) " + fromPart + " is given value " + escapedp + ", ";
                } else {
                    // for custom tasks we need to deal with no definition
                    var fromPart = innerParts[0];
                    innerParts.shift(); // removes the first item from the array
                    var escapedp = innerParts.join('=').replace(/\#\#/g , ",");
                    escapedp = escapedp.replace(/\|\|/g , "=");

                    retVal += "(Data Input) " + fromPart + " is given value " + escapedp + ", ";
                }
            } else if(nextPart.indexOf("->") > 0) {
                var innerParts = nextPart.split("->");

                if(innerParts[0].startsWith("[din]")) {
                    var fromPart = innerParts[0].slice(5, innerParts[0].length);

                    retVal += "(Data Input) " + fromPart + " is mapped to " + innerParts[1] + ", ";
                } else if(innerParts[0].startsWith("[dout]")) {
                    var fromPart = innerParts[0].slice(6, innerParts[0].length);

                    retVal += "(Data Output) " + fromPart + " is mapped to " + innerParts[1] + ", ";
                }
            } else {
                // default to equality
                if(nextPart.trim().length > 0) {
                    var innerParts = nextPart.split("=");
                    var fromPart = innerParts[0].slice(5, innerParts[0].length);
                    var inType = innerParts[0].startsWith("[din]") ? "(Data Input) " : "(Data Output) ";
                    retVal += inType + fromPart + "is given value , ";
                }
            }
        }

        if(retVal.length > 0) {
            return retVal.substring(0, retVal.length - 2);
        } else {
            return propvalue;
        }
    } else {
        return propvalue;
    }
}

function presentPropertyName(rawprop) {
    if(propertyNameMappings[rawprop]) {
        return propertyNameMappings[rawprop];
    } else {
        // default just campitalize first letter
        return rawprop.charAt(0).toUpperCase() + rawprop.slice(1);
    }
}