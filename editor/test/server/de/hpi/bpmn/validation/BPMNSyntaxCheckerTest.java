package de.hpi.bpmn.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Task;

public class BPMNSyntaxCheckerTest {
	BPMNSyntaxChecker checker;
	BPMNDiagram diag;

	@Before public void setUp() throws Exception {
		diag = new BPMNDiagram();
		checker = new BPMNSyntaxChecker(diag);
	}
	
	@Test public void testAllowedNodes(){
		Task task = new Task();
		task.setResourceId("myTask");
		Task miTask = new Task();
		miTask.setResourceId("myMI");
		miTask.setMultipleInstance();
		
		// If allowed is empty, all is allowed
		clearChecker();
		checker.checkForAllowedAndForbiddenNode(task);
		assertNoErrors();
		
		// If adding first element to allowed, all other aren't allowed anymore
		clearChecker();
		checker.allowedNodes.add("Blub");	
		checker.checkForAllowedAndForbiddenNode(task);
		assertErrorsOn("myTask");
		
		// If adding class to allowed, that nodes of this class are allowed
		clearChecker();
		checker.allowedNodes.add("Task");
		checker.checkForAllowedAndForbiddenNode(new Task());
		assertNoErrors();
		
		// If adding MultipleInstanceActivity to allowed, than multiple instances are allowed
		clearChecker();
		checker.allowedNodes.add("Blub");	
		checker.checkForAllowedAndForbiddenNode(miTask);
		assertErrorsOn("myMI");
		checker.clearErrors();
		checker.allowedNodes.add("MultipleInstanceActivity");
		checker.checkForAllowedAndForbiddenNode(miTask);
		assertNoErrors();
		
		// If adding Task to forbidden, than tasks aren't allowed anymore
		clearChecker();
		checker.forbiddenNodes.add("Task");
		checker.checkForAllowedAndForbiddenNode(task);
		assertErrorsOn("myTask");
		
		// If adding MultipleInstanceActivity to forbidden, than they aren't allowed anymore
		clearChecker();
		checker.checkForAllowedAndForbiddenNode(miTask);
		assertNoErrors();
		checker.forbiddenNodes.add("MultipleInstanceActivity");
		checker.checkForAllowedAndForbiddenNode(miTask);
		assertErrorsOn("myMI");
	}
	
	@Test public void testClearErrors(){
		assertEquals(0, checker.getErrors().size());
		checker.addError(new Task(), "Blub");
		assertEquals(1, checker.getErrors().size());
		checker.clearErrors();
		assertNoErrors();
	}

	private void clearChecker(){
		checker.allowedNodes.clear();
		checker.clearErrors();
		checker.forbiddenNodes.clear();
	}
	
	private void assertErrorsOn(String resourceId){
		assertEquals(1, checker.getErrors().size());
		assertTrue(checker.getErrors().containsKey(resourceId));
	}
	
	private void assertNoErrors(){
		assertEquals(0, checker.getErrors().size());
	}
}
