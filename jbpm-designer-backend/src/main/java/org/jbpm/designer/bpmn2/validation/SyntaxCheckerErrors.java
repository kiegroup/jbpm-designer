package org.jbpm.designer.bpmn2.validation;

public class SyntaxCheckerErrors {
    public static final String PROCESS_HAS_NO_PACKAGE_NAME = "Process has no package name.";
    public static final String START_NODE_NO_OUTGOING_CONNECTIONS = "Start node has no outgoing connections";
    public static final String USER_TASK_HAS_NO_TASK_NAME = "User Task has no task name.";
    public static final String END_NODE_NO_INCOMING_CONNECTIONS = "End node has no incoming connections";
    public static final String NODE_NO_OUTGOING_CONNECTIONS = "Node has no outgoing connections";
    public static final String NODE_NO_INCOMING_CONNECTIONS = "Node has no incoming connections";
    public static final String BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP = "Business Rule Task has no ruleflow-group.";
    public static final String SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED = "Sequence flow has no condition expression defined.";
}
