package de.hpi.petrinet.verification;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hpi.petrinet.AbstractPetriNetTest;
import de.hpi.petrinet.PetriNet;

public class PetriNetSoundnessCheckerTest extends AbstractPetriNetTest{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test public void testIsRelaxedSound(){
		/**
		 * A not relaxed sound petri net
		 */
		PetriNet net = this.openPetriNetFromFile("verification/notRelaxedSoundPetrinet.xml");
		PetriNetSoundnessChecker checker = new PetriNetSoundnessChecker(net);
		
		checker.calculateRG();
		
		assertFalse(checker.isRelaxedSound());
		assertEquals(1, checker.getNotParticipatingTransitions().size());
		assertEquals("t3", checker.getNotParticipatingTransitions().iterator().next().getId());
		
		/**
		 * A relaxed sound petri net
		 */
		PetriNet net2 = this.openPetriNetFromFile("verification/relaxedSoundPetrinet.xml");
		PetriNetSoundnessChecker checker2 = new PetriNetSoundnessChecker(net2);
		
		checker2.calculateRG();
		
		assertTrue(checker2.isRelaxedSound());
	}

}
