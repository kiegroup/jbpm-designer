package org.jbpm.designer.wildfly;

import java.lang.reflect.Type;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.jboss.solder.servlet.http.ImplicitHttpServletObjectsProducer;

/**
 * Workaround for SOlder issue with servlet CDI integration and CDI 1.1 spec, see here https://issues.jboss.org/browse/SOLDER-322
 */
public class SolderCDIWorkaround implements Extension {

    public void vetoHttpServletObjectsProducer(@Observes ProcessAnnotatedType<ImplicitHttpServletObjectsProducer> event) {
        AnnotatedTypeBuilder<ImplicitHttpServletObjectsProducer> builder = new AnnotatedTypeBuilder<ImplicitHttpServletObjectsProducer>();
        builder.readFromType(event.getAnnotatedType());

        // remove producer methods for HttpServletRequest, HttpSession and ServletContext
        for (AnnotatedMethod<? super ImplicitHttpServletObjectsProducer> method : event.getAnnotatedType().getMethods()) {
            Type type = method.getBaseType();
            if (HttpServletRequest.class.equals(type) || HttpSession.class.equals(type) || ServletContext.class.equals(type)) {
                builder.removeFromMethod(method, Produces.class);
            }
        }
        event.setAnnotatedType(builder.create());
    }
}
