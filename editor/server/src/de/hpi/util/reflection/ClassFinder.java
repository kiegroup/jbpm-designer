package de.hpi.util.reflection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
	public static <T> List<Class<? extends T>> getClassesByPackageName(Class<T> superclass, String pckgname) throws ClassNotFoundException {
        // This will hold a list of directories matching the packagename. There may be more than one if a package is split over multiple jars/paths
        ArrayList<File> directories = new ArrayList<File>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }
 
        ArrayList<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        // For every directory identified capture all the .class files
        for (File directory : directories) {
            if (directory.exists()) {
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
                    	List<Class<? extends T>> childPackages = ClassFinder.getClassesByPackageName(superclass, pckgname + '.' + file.getName() );
                    	classes.addAll( childPackages );
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }
}
