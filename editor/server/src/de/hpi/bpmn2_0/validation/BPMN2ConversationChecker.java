package de.hpi.bpmn2_0.validation;

import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.conversation.Communication;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.participant.Participant;

public class BPMN2ConversationChecker {

	// CONVERSATION
	protected static final String COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS";
	protected static final String MESSAGEFLOW_SOURCE_MUST_BE_PARTICIPANT = "MESSAGEFLOW_START_MUST_BE_PARTICIPANT";
	protected static final String MESSAGEFLOW_TARGET_MUST_BE_PARTICIPANT = "MESSAGEFLOW_END_MUST_BE_PARTICIPANT";
	protected static final String CONV_LINK_CANNOT_CONNECT_CONV_NODES = "CONV_LINK_CANNOT_CONNECT_CONV_NODES";
	
	private BPMN2SyntaxChecker syntaxChecker;
	
	public BPMN2ConversationChecker(BPMN2SyntaxChecker syntaxChecker) {
		this.syntaxChecker = syntaxChecker;
	}
	
	public void checkConversation(Conversation conversation) {
		
		for(ConversationLink cLink : conversation.getConversationLink()) {
			checkConversationLink(cLink);
		}
		
		for(MessageFlow mFlow : conversation.getMessageFlow()) {
			checkMessageFlow(mFlow);
		}
		
		for(ConversationNode cNode : conversation.getConversationNode()) {
			checkConversationNode(cNode);
		}
		
		for(Participant participant : conversation.getParticipant()) {
			checkParticipant(participant);
		}
	}
	
	private void checkParticipant(Participant participant) {
		// TODO Auto-generated method stub
		
	}

	private void checkConversationNode(ConversationNode conversationNode) {
		if(conversationNode.getIncoming().size()+conversationNode.getOutgoing().size() < 2) {
			syntaxChecker.addError(conversationNode, COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS);
		}
	}
	
	private void checkConversationLink(ConversationLink conversationLink) {
//		if(conversationLink.getSourceRef() == null) {
//			syntaxChecker.addError(conversationLink, BPMN2SyntaxChecker.NO_SOURCE);
//		}
//		
//		if(conversationLink.getTargetRef() == null) {
//			syntaxChecker.addError(conversationLink, BPMN2SyntaxChecker.NO_TARGET);
//		}
		
		if(conversationLink.getSourceRef() != null && conversationLink.getTargetRef() != null && conversationLink.getSourceRef() instanceof ConversationNode && conversationLink.getTargetRef() instanceof ConversationNode) {
			syntaxChecker.addError(conversationLink, CONV_LINK_CANNOT_CONNECT_CONV_NODES);
		}
	}
	
	private void checkMessageFlow(MessageFlow messageFlow) {
//		if(messageFlow.getSourceRef() == null) {
//			syntaxChecker.addError(messageFlow, BPMN2SyntaxChecker.NO_SOURCE);
//		}
//		
//		if(messageFlow.getTargetRef() == null) {
//			syntaxChecker.addError(messageFlow, BPMN2SyntaxChecker.NO_TARGET);
//		}
		
		if(messageFlow.getSourceRef() != null && !(messageFlow.getSourceRef() instanceof Participant)) {
			syntaxChecker.addError(messageFlow, MESSAGEFLOW_SOURCE_MUST_BE_PARTICIPANT);
		}
		
		if(messageFlow.getTargetRef() != null && !(messageFlow.getTargetRef() instanceof Participant)) {
			syntaxChecker.addError(messageFlow, MESSAGEFLOW_TARGET_MUST_BE_PARTICIPANT);
		}
	}
}
