package org.jbpm.designer.shared.security;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.uberfire.security.annotations.RolesType;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@RolesType
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface StandaloneRoles {

    AppRoles[] value();
}
