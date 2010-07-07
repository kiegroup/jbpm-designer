package de.hpi.util.reflection;

import java.util.List;

import javax.servlet.ServletContext;

public class ClassFinder {

	/** Returns all classes of the specified package, who are subclasses of the given super class
	 * 
	 * @param <T>
	 * @param T superclass of results
	 * @param pckgname package to search in
	 * @return subclasses of the given super class in the given package
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<? extends T>> getClassesByPackageName(Class<T> superclass, String pckgname, ServletContext servletContext) throws ClassNotFoundException {
		throw new UnsupportedOperationException("Manipulating classes from folders is unsupported");
    }
}
