package org.jbpm.designer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationProvider {

    public static final String CONFIG_FILE = "/designer.configuration";

    private Properties configurationProps = new Properties();

    private static ConfigurationProvider instance;

    private ConfigurationProvider() {
        try {
            InputStream input = this.getClass().getResourceAsStream(CONFIG_FILE);
            if (input != null) {
                configurationProps.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationProvider getInstance() {
        if (instance == null) {
            instance = new ConfigurationProvider();
        }

        return instance;
    }

    public String getDesignerContext() {
        return configurationProps.getProperty("application.context", "/org.jbpm.designer.jBPMDesigner/");
    }
}
