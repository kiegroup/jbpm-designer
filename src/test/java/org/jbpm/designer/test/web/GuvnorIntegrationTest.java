package org.jbpm.designer.test.web;

import static junit.framework.Assert.*;

import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletContext;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
@RunAsClient
@SuppressWarnings("rawtypes")
public class GuvnorIntegrationTest extends AbstractGuvnorIntegrationTest {

    private Logger logger = LoggerFactory.getLogger(GuvnorIntegrationTest.class);
    
    private Set<Class<? extends AbstractGuvnorIntegrationTest>> integrationTestClasses = null;
    private Set<String> otherStaticMethods = new HashSet<String>();
    private HashMap<Method, Class> methodClassMap = new HashMap<Method, Class>();
    
    private Set<Class<? extends AbstractGuvnorIntegrationTest>> getIntegrationTestClasses() { 
        if( integrationTestClasses == null ) { 
            Reflections reflections = new Reflections(this.getClass().getPackage().getName());
            integrationTestClasses = reflections.getSubTypesOf(AbstractGuvnorIntegrationTest.class);
        }
        return integrationTestClasses;
    }

    public List<Method> getTestClassMethods() { 
        otherStaticMethods.add("deployment");
        otherStaticMethods.add("createServletContext");
        
        ArrayList<Method> testMethods = new ArrayList<Method>();
        for( Class clazz : getIntegrationTestClasses() ) { 
            Method [] methods = clazz.getMethods();
            for( Method possibleTestMethod : methods ) { 
                String methodName = possibleTestMethod.getName();
                if( methodName.startsWith("run") ) { 
                    assertTrue("Method " + clazz.getSimpleName() + "." + methodName + " is not static.", Modifier.isStatic(possibleTestMethod.getModifiers()));
                    Class [] parameters = possibleTestMethod.getParameterTypes();
                    assertTrue(clazz.getSimpleName() + "." + methodName + " has incorrect number of arguments: " + parameters.length, 
                            parameters.length == 3);
                    Class [] correctTypes = { URL.class, IDiagramProfile.class, ServletContext.class };
                    for( int i = 0; i < parameters.length; ++i ) { 
                        assertTrue("Method " + methodName + "  does not have " + correctTypes[i].getSimpleName() + " as parameter type " + i,
                                parameters[i].equals(correctTypes[i]));
                    }
                    testMethods.add(possibleTestMethod);
                    methodClassMap.put(possibleTestMethod, clazz);
                } else if( Modifier.isStatic(possibleTestMethod.getModifiers()) ) {
                     if( ! otherStaticMethods.contains(methodName) ) { 
                         fail("Method " + clazz.getSimpleName() + "." + methodName + " is static but not a test method.");
                     }
                }
            }
        }
        assertTrue( testMethods.size() > 0 );
        
        return testMethods;
    }
    
    @Test
    public void doTests() throws Throwable {
        List<Method> testMethods = getTestClassMethods();
        
        setupGuvnor(guvnorUrl, profile);

        for( Method test : testMethods ) { 
            logger.info("Running " + methodClassMap.get(test).getSimpleName() + "." + test.getName() );
            try { 
                test.invoke(null, guvnorUrl, profile, servletContext);
            } catch( InvocationTargetException ite ) { 
                throw ite.getCause();
            }
        }
    }

}
