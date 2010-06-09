package de.hpi.util.reflection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
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
		// This will hold a list of directories matching the packagename. There may be more than one if a package is split over multiple jars/paths
      	String path = pckgname.replace('.', '/');
        
      	File directory = new File(servletContext.getRealPath("WEB-INF/classes/" + path));

        ArrayList<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        // For every directory identified capture all the .class files
        if (directory != null && directory.exists()) {
        	// Get the list of the files contained in the package
        	File[] files = directory.listFiles();
        	for (File file : files) {
        		// we are only interested in .class files
        		if (file.getName().endsWith(".class")) {
        			// removes the .class extension
        			// Get the class object
        			Class<? extends Object> cls = Class.forName(pckgname + '.' + file.getName().substring(0, file.getName().length() - 6));
        			// Checks if its an AbstractHandler
        			if( superclass.isAssignableFrom(cls) ){
        				classes.add( (Class<? extends T>) cls );
        			}
        		} else if( file.isDirectory() ) { 
        			// Add recursive all child packages
        			List<Class<? extends T>> childPackages = ClassFinder.getClassesByPackageName(superclass, pckgname + '.' + file.getName(), servletContext );
        			classes.addAll( childPackages );
        		}
        	}
        } else {
        	throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
        }
        return classes;
    }
}
