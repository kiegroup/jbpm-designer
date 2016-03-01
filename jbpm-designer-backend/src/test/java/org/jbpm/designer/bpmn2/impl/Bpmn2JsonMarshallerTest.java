package org.jbpm.designer.bpmn2.impl;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.jbpm.designer.test.bpmn2.Bpmn2UnmarshallingTestCase;
import org.jbpm.designer.web.profile.impl.DefaultProfileImpl;
import org.junit.Before;
import org.junit.Test;

public class Bpmn2JsonMarshallerTest {

    Bpmn2JsonMarshaller marshaller = null;
    Bpmn2JsonUnmarshaller unmarshaller = null;

    @Before
    public void testSetUp() {
        unmarshaller = new Bpmn2JsonUnmarshaller();

        marshaller = new Bpmn2JsonMarshaller();
        marshaller.setProfile(new DefaultProfileImpl());
    }

    @Test
    public void testGroupMarshalling() throws IOException {
        String json = marshaller.marshall(getDefinitionFor("group.json"), "");

        Definitions definitions  = (Definitions) unmarshaller.unmarshall(json, "").getContents().get(0);

        Process process = getProcessFrom(definitions);
        Group group = (Group) process.getArtifacts().get(0);
        assertEquals("Group name is wrong.", group.getCategoryValueRef().getValue(), "group");
        assertEquals("Group have no documentation.", group.getDocumentation().size(), 1);
        // Unmarshalling is not prepared for two times calling.
        assertEquals("<![CDATA[<![CDATA[group documentation]]>]]>", group.getDocumentation().get(0).getText());
    }

    private static Definitions getDefinitionFor(String filename) throws IOException {
        URL fileURL = Bpmn2UnmarshallingTestCase.class.getResource(filename);
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        return (Definitions) unmarshaller.unmarshall(new File(fileURL.getFile()), "").getContents().get(0);
    }

    private static Process getProcessFrom(Definitions definitions) {
        for(RootElement root: definitions.getRootElements()) {
            if (root instanceof Process) {
                return (Process) root;
            }
        }

        return null;
    }
}
