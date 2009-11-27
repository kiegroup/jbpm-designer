package de.hpi.bpmn2_0.validation;

import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.conversation.Communication;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;

public class BPMN2ConversationChecker {

	// CONVERSATION
	protected static final String COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS";
	
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
	}
	
	private void checkConversationNode(ConversationNode conversationNode) {
		if(conversationNode.getIncoming().size()+conversationNode.getOutgoing().size() < 2) {
			syntaxChecker.addError(conversationNode, COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS);
		}
	}
	
	private void checkConversationLink(ConversationLink conversationLink) {
		if(conversationLink.getSourceRef() == null) {
			syntaxChecker.addError(conversationLink, syntaxChecker.NO_SOURCE);
		}
		
		if(conversationLink.getTargetRef() == null) {
			syntaxChecker.addError(conversationLink, syntaxChecker.NO_TARGET);
		}
	}
	
	private void checkMessageFlow(MessageFlow messageFlow) {
		if(messageFlow.getSourceRef() == null) {
			syntaxChecker.addError(messageFlow, syntaxChecker.NO_SOURCE);
		}
		
		if(messageFlow.getTargetRef() == null) {
			syntaxChecker.addError(messageFlow, syntaxChecker.NO_TARGET);
		}
	}
}
