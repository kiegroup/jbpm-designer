package org.jbpm.designer.repository;

import static org.junit.Assert.assertEquals;

import org.jbpm.designer.util.ConfigurationProvider;
import org.junit.Test;

public class ConfigurationProviderTest {

    @Test
    public void configurationProviderInitTest() {
        ConfigurationProvider provider = ConfigurationProvider.getInstance();
        assertEquals("/", provider.getDesignerContext());
    }
}
