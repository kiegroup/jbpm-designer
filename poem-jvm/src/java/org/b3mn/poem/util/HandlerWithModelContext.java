package org.b3mn.poem.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HandlerWithModelContext {
	
	String uri();
	boolean denyPublicUserAccess() default false;
	boolean filterBrowser() default false;
	AccessRight accessRestriction() default AccessRight.READ;
}
