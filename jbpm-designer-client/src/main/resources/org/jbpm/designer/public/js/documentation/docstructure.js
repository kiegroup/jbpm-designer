// Implements String startsWith for IE11
if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position) {
        position = position || 0;
        return this.indexOf(searchString, position) === position;
    };
}

var elementDataInfo = {
    "UserTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "User Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/swimlane/process.participant.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,actors,groupid,subject,description,skippable,content,createdby,locale,multipleinstance,notifications,priority,reassignment"
    },
    "SendTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Send Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,messageref,description,skippable,multipleinstance"
    },
    "ReceiveTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Receive Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,messageref"
    },
    "ManualTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Manual Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,skippable,datainputset,dataoutputset,assignments,multipleinstance,"
    },
    "ServiceTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Service Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,skippable,serviceoperation,serviceinterface,serviceimplementation,datainputset,dataoutputset,assignments,multipleinstance"
    },
    "BusinessRuleTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Business Rule Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,ruleflowgroup,datainputset,dataoutputset,assignments"
    },
    "ScriptTask": {
        "group": "Tasks",
        "groupdispname": "Tasks",
        "dispname": "Script Task",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/task.png",
        "properties": "name,documentation,isasync,script,script_language"
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
        "properties": "name,documentation,datainputset,dataoutputset,assignments,isasync,taskname,description"
    },
    "ReusableSubprocess": {
        "group": "Subprocesses",
        "groupdispname":"Subprocesses",
        "dispname": "Reusable Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,calledelement,datainputset,dataoutputset,assignments,independent,isasync,waitforcompletion"
    },
    "MultipleInstanceSubprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Multiple Instance Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,multipleinstancecollectioninput,multipleinstancecollectionoutput,multipleinstancedatainput,multipleinstancedataoutput,multipleinstancecompletioncondition,vardefs,isasync"
    },

    "Subprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Embedded Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,vardefs,isasync"
    },
    "AdHocSubprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Ad-Hoc Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "properties": "name,documentation,adhocordering,adhoccompletioncondition,adhoccancelremaininginstances,vardefs,isasync"
    },
    "EventSubprocess": {
        "group": "Subprocesses",
        "groupdispname": "Subprocesses",
        "dispname": "Event Subprocess",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/activity/subprocess.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/activity/event.subprocess.png",
        "properties": "name,documentation,vardefs,isasync"
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
        "properties": "name,documentation,eventtype"
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
        "properties": "name,documentation,dataoutput,dataoutputassociations,isinterrupting"
    },
    "StartMessageEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Message Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/message.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,messageref,isinterrupting"
    },
    "StartTimerEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Timer Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/timer.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,timedate,timeduration,timecycle,timecyclelanguage,isinterrupting"
    },
    "StartEscalationEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Escalation Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/escalation.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,escalationcode,isinterrupting"
    },
    "StartConditionalEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Conditional Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/conditional.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,conditionlanguage,conditionexpression,isinterrupting"
    },
    "StartErrorEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Error Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/error.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,errorref,isinterrupting"
    },
    "StartCompensationEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Compensation Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/compensation.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,isinterrupting"
    },
    "StartSignalEvent": {
        "group": "StartEvents",
        "groupdispname": "Start Events",
        "dispname": "Signal Start Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/startevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/startevent/signal.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,signalref,isinterrupting"
    },
    "IntermediateMessageEventCatching": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Message Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/message.png",
        "properties": "name,documentation,messageref,dataoutput,dataoutputassociations,boundarycancelactivity"
    },
    "IntermediateTimerEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Timer Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/timer.png",
        "properties": "name,documentation,timedate,timeduration,timecycle,timecyclelanguage,boundarycancelactivity"
    },
    "IntermediateEscalationEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Escalation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/escalation.png",
        "properties": "name,documentation,escalationcode,dataoutput,dataoutputassociations,boundarycancelactivity"
    },
    "IntermediateConditionalEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Conditional Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/conditional.png",
        "properties": "name,documentation,conditionlanguage,conditionexpression,dataoutput,dataoutputassociations,boundarycancelactivity"
    },
    "IntermediateErrorEvent": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Error Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/error.png",
        "properties": "name,documentation,errorref,dataoutput,dataoutputassociations,boundarycancelactivity"
    },
    "IntermediateCompensationEventCatching": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Compensation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/compensation.png",
        "properties": "name,documentation,dataoutput,dataoutputassociations,boundarycancelactivity"
    },
    "IntermediateSignalEventCatching": {
        "group": "CatchingEvents",
        "groupdispname": "Catching Events",
        "dispname": "Catching Signal Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/catching/signal.png",
        "properties": "name,documentation,signalref,signalscope,dataoutput,dataoutputassociations,boundarycancelactivity"
    },
    "IntermediateMessageEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Message Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/message.png",
        "properties": "name,documentation,datainput,datainputassociations,messageref"
    },
    "IntermediateEscalationEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Escalation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/escalation.png",
        "properties": "name,documentation,datainput,datainputassociations,escalationcode"
    },
    "IntermediateCompensationEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Compensation Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/compensation.png",
        "properties": "name,documentation,datainput,datainputassociations,activityref"
    },
    "IntermediateSignalEventThrowing": {
        "group": "ThrowingEvents",
        "groupdispname": "Throwing Events",
        "dispname": "Throwing Signal Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/throwing/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/throwing/signal.png",
        "properties": "name,documentation,datainput,datainputassociations,signalref,signalscope"
    },
    "EndNoneEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "properties": "name,documentation,datainput,datainputassociations"
    },
    "EndMessageEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Message End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/message.png",
        "properties": "name,documentation,datainput,datainputassociations,messageref"
    },
    "EndEscalationEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Escalation End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/escalation.png",
        "properties": "name,documentation,datainput,datainputassociations,escalationcode"
    },
    "EndErrorEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Error End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/error.png",
        "properties": "name,documentation,datainput,datainputassociations,errorref"
    },
    "EndCancelEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Cancel End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/cancel.png",
        "properties": "name,documentation,datainput,datainputassociations"
    },
    "EndCompensationEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Compensation End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/compensation.png",
        "properties": "name,documentation,datainput,datainputassociations,activityref"
    },
    "EndSignalEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Signal End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/signal.png",
        "properties": "name,documentation,datainput,datainputassociations,signalref,signalscope"
    },
    "EndTerminateEvent": {
        "group": "EndEvents",
        "groupdispname": "End Events",
        "dispname": "Terminate End Event",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/endevent/none.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/endevent/terminate.png",
        "properties": "name,documentation,datainput,datainputassociations"
    },
    "SequenceFlow": {
        "group": "ConnectingObjects",
        "groupdispname": "Connectors",
        "dispname": "Sequence Flow",
        "groupicon": "stencilsets/bpmn2.0jbpm/icons/connector/sequenceflow.png",
        "icon": "stencilsets/bpmn2.0jbpm/icons/connector/sequenceflow.png",
        "properties": "name,documentation,priority,conditionexpression"
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
    "dataoutputset": "Data Outputs",
    "activityref": "Activity Ref",
    "signalref": "Signal Ref",
    "signalscope": "Signal Scope",
    "dataoutput": "Data Input",
    "dataoutputassociations": "Data Output Associations",
    "datainputassociations": "Data Input Associations",
    "boundarycancelactivity": "Cancel Activity",
    "isinterrupting": "Is Interrupting",
    "waitforcompletion" : "Wait for completion",
    "eventtype": "Event Type",
    "groupid": "Groups",
    "conditionexpression":"Condition Expression"

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
                "value":parsePropertyValue(nextPart,element.properties[nextPart]).replace(/</g,'&lt;').replace(/>/g,'&gt;')
            });
        }

        var showInDocumentation = true;
        if(elementInfoDetails["group"] == "ConnectingObjects") {
            if(!element.properties.name.length && !element.properties.documentation.length && !element.properties.conditionexpression.length) {
                showInDocumentation = false;
            }
        }

        myElementGroups[elementInfoDetails.group].push({
            "id": element.resourceId,
            "group": elementInfoDetails["group"],
            "groupdispname": elementInfoDetails["groupdispname"],
            "dispname": elementInfoDetails["dispname"],
            "groupicon": ctx + elementInfoDetails["groupicon"],
            "icon": ctx + elementInfoDetails["icon"],
            "properties": propsArray,
            "nodename": nodeName.trim().length > 0 ? nodeName : "(Not Named)",
            "showindocumentation": showInDocumentation
        });
    }
}

function parsePropertyValue(propname, propvalue) {
    if(propvalue === undefined) {
        return "";
    }
    if(typeof propvalue === 'boolean') {
        propvalue = propvalue.toString();
    }
    if(propname == "datainputset" || propname == "dataoutputset" || propname == "datainput" || propname == "dataoutput" || propname == "vardefs") {
        var retVal = "";
        var propParts = propvalue.trim().split(",");
        for(var i=0; i < propParts.length; i++) {
            var nextPart = propParts[i];
            if(nextPart.indexOf(":") > 0) {
                var innerParts = nextPart.split(":");
                if(innerParts[1] != "false" && innerParts[1] != "true") {
                    retVal += innerParts[0] + "(" + innerParts[1] + ")\n";
                } else {
                    retVal += innerParts[0] + "(no defined type)\n";
                }
            } else {
                if(nextPart.trim().length > 0) {
                    retVal += nextPart + "(no defined type)\n";
                }
            }
        }
        if(retVal.length > 0) {
            return retVal.substring(0, retVal.length - 1);
        } else {
            return propvalue;
        }
    } else if(propname == "assignments" || propname == "dataoutputassociations" || propname == "datainputassociations") {
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

                    retVal += "(Data Input) " + fromPart + " is given value " + decodeURIComponent(escapedp.replace(/\+/g, ' ')) + "\n";
                } else if(innerParts[0].startsWith("[dout]")) {
                    var fromPart = innerParts[0].slice(6, innerParts[0].length);
                    innerParts.shift(); // removes the first item from the array
                    var escapedp = innerParts.join('=').replace(/\#\#/g , ",");
                    escapedp = escapedp.replace(/\|\|/g , "=");

                    retVal += "(Data Output) " + fromPart + " is given value " + decodeURIComponent(escapedp.replace(/\+/g, ' ')) + "\n";
                } else {
                    // for custom tasks we need to deal with no definition
                    var fromPart = innerParts[0];
                    innerParts.shift(); // removes the first item from the array
                    var escapedp = innerParts.join('=').replace(/\#\#/g , ",");
                    escapedp = escapedp.replace(/\|\|/g , "=");

                    retVal += "(Data Input) " + fromPart + " is given value " + decodeURIComponent(escapedp.replace(/\+/g, ' ')) + "\n";
                }
            } else if(nextPart.indexOf("->") > 0) {
                var innerParts = nextPart.split("->");

                if(innerParts[0].startsWith("[din]")) {
                    var fromPart = innerParts[0].slice(5, innerParts[0].length);

                    retVal += "(Data Input) " + fromPart + " is mapped to " + innerParts[1] + "\n";
                } else if(innerParts[0].startsWith("[dout]")) {
                    var fromPart = innerParts[0].slice(6, innerParts[0].length);

                    retVal += "(Data Output) " + fromPart + " is mapped to " + innerParts[1] + "\n";
                }
            } else {
                // default to equality
                if(nextPart.trim().length > 0) {
                    var innerParts = nextPart.split("=");
                    var fromPart = innerParts[0].slice(5, innerParts[0].length);
                    var inType = innerParts[0].startsWith("[din]") ? "(Data Input) " : "(Data Output) ";
                    retVal += inType + fromPart + "is given value\n";
                }
            }
        }

        return retVal;
    } else if(propname == "script") {
        return formatScript(propvalue);
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

function displayProcessImg() {
    var svg = parent.ORYX.EDITOR.getCanvas().getSVGRepresentation(true, true);
    var formattedSvgDOM = parent.DataManager.serialize(svg);
    var svgHeight = svg.getAttributeNS(null, 'height');
    var svgWidth = svg.getAttributeNS(null, 'width');

    var isIE = Object.hasOwnProperty.call(window, "ActiveXObject");
    var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);

    if(isChrome || isIE) {
        // remove the non-chrome div display
        var nonChromeDiv = document.getElementById("processimgdivdisplay");
        if(nonChromeDiv) {
            nonChromeDiv.parentNode.removeChild(nonChromeDiv);
        }

        var chromeIeSVG = parent.ORYX.EDITOR.getCanvas().getSVGRepresentation(false, false);
        chromeIeSVG.setAttributeNS(null, 'width', parent.ORYX.CONFIG.MAXIMUM_SIZE);
        chromeIeSVG.setAttributeNS(null, 'height', parent.ORYX.CONFIG.MAXIMUM_SIZE);

        $("#processimgdivdisplayframe").contents().find("body").html('');
        $("#processimgdivdisplayframe").contents().find("body").html(chromeIeSVG);
    } else {
        // remove the frame used for chrome
        var chromeFrame = document.getElementById("processimgdivdisplayframe");
        if(chromeFrame) {
            chromeFrame.parentNode.removeChild(chromeFrame);
        }

        parent.Ext.Ajax.request({
            url: parent.ORYX.PATH + "transformer",
            method: 'POST',
            success: function(request){
                try {
                    document.getElementById("processimgdivdisplay").innerHTML = "";
                    document.getElementById("processimgdivdisplay").innerHTML = request.responseText;
                } catch(e) {
                    parent.ORYX.EDITOR._pluginFacade.raiseEvent({
                        type 		: parent.ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                        ntype		: 'error',
                        msg         : parent.ORYX.I18N.view.processImgFail+': ' + e,
                        title       : ''

                    });
                }
            },
            failure: function(){
                parent.ORYX.EDITOR._pluginFacade.raiseEvent({
                    type 		: parent.ORYX.CONFIG.EVENT_NOTIFICATION_SHOW,
                    ntype		: 'error',
                    msg         : parent.ORYX.I18N.view.processImgFail+'.',
                    title       : ''

                });
            },
            params: {
                profile: parent.ORYX.PROFILE,
                uuid :  window.btoa(encodeURI(parent.ORYX.UUID)),
                fsvg : parent.Base64.encode(formattedSvgDOM),
                transformto : "png",
                respaction : "showurl",
                svgheight: svgHeight,
                svgwidth: svgWidth
            }
        });
    }
}

function showElementInModel(eleID) {
    // raise event to focus on specific node id
    parent.ORYX.EDITOR._pluginFacade.raiseEvent({
        type 		: parent.ORYX.CONFIG.EVENT_DOCELEMENT_TO_MODEL,
        eleid		: eleID
    });

    // switch to modeling tab
    parent.Ext.getCmp('maintabs').setActiveTab(0);
}

function showAsPDF() {
    var transformval = 'html2pdf';
    $("table").attr("border", "1");
    var contentDivEncoded = parent.Base64.encode( $("#pagecontainercore").clone().wrap('<div></div>').parent().html() );
    $("table").attr("border", "0");

    var method = "post";
    var form = document.createElement("form");
    form.setAttribute("name", "transformerform");
    form.setAttribute("method", method);
    form.setAttribute("action", parent.ORYX.CONFIG.TRANSFORMER_URL());
    form.setAttribute("target", "_blank");

    var hfFSVG = document.createElement("input");
    hfFSVG.setAttribute("type", "hidden");
    hfFSVG.setAttribute("name", "htmlenc");
    hfFSVG.setAttribute("value", contentDivEncoded);
    form.appendChild(hfFSVG);

    var hfUUID = document.createElement("input");
    hfUUID.setAttribute("type", "hidden");
    hfUUID.setAttribute("name", "uuid");
    hfUUID.setAttribute("value", parent.ORYX.UUID);
    form.appendChild(hfUUID);

    var hfPROFILE = document.createElement("input");
    hfPROFILE.setAttribute("type", "hidden");
    hfPROFILE.setAttribute("name", "profile");
    hfPROFILE.setAttribute("value", parent.ORYX.PROFILE);
    form.appendChild(hfPROFILE);

    var hfTRANSFORMTO = document.createElement("input");
    hfTRANSFORMTO.setAttribute("type", "hidden");
    hfTRANSFORMTO.setAttribute("name", "transformto");
    hfTRANSFORMTO.setAttribute("value", transformval);
    form.appendChild(hfTRANSFORMTO);

    var processJSON = parent.ORYX.EDITOR.getSerializedJSON();
    var processId = jsonPath(JSON.parse(processJSON), "$.properties.id");
    var hfPROCESSID = document.createElement("input");
    hfPROCESSID.setAttribute("type", "hidden");
    hfPROCESSID.setAttribute("name", "processid");
    hfPROCESSID.setAttribute("value", processId);
    form.appendChild(hfPROCESSID);

    document.body.appendChild(form);
    form.submit();
}

function formatScript(str) {
    var result = new String("");
    var c = '\0';
    var prevC = '\0';
    var atEscape = false;
    for (i = 0; i < str.length; i++) {
        prevC = c;
        c = str.charAt(i);
        // set atEscape flag
        if (c === '\\') {
            // deal with 2nd '\\' char
            if (atEscape) {
                result = result + c;
                atEscape = false;
                // set c to '\0' so that prevC doesn't match '\\'
                // the next time round
                c = '\0';
            }
            else {
                atEscape = true;
            }
        }
        else if (atEscape) {
            if (c === 'n') {
                result = result + "<br />";
            }
            else if (c === 't') {
                result = result + "&nbsp;&nbsp;&nbsp;&nbsp";
            }
            else {
                result = result + c;
            }
        }
        else if (c === ' ') {
            result = result + "&nbsp;";
        }
        else {
            result = result + c;
        }
        // unset atEscape flag if required
        if (prevC === '\\') {
            if (atEscape) {
                atEscape = false;
            }
        }
    }
    return result;
}

function scrollToElement() {
    if(parent.ORYX.PROCESSDOC_RESOURCEID != "" && parent.ORYX.PROCESSDOC_RESOURCEID.length > 0) {
        document.getElementById(parent.ORYX.PROCESSDOC_RESOURCEID).scrollIntoView();
        parent.ORYX.PROCESSDOC_RESOURCEID = "";
    }

}
