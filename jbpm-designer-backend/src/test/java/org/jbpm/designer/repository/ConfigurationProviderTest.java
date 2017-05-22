package org.jbpm.designer.repository;

import org.jbpm.designer.util.ConfigurationProvider;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationProviderTest {

    @Test
    public void configurationProviderInitTest() {
        ConfigurationProvider provider = ConfigurationProvider.getInstance();
        assertEquals("/",
                     provider.getDesignerContext());
    }
}
