package org.jbpm.designer.repository;

/**
 * This is a Filter interface used by <code>Repository</code> operations to filter out not needed content.
 *
 * TODO most likely it would need to be implemented by repository specific classes to
 * accommodate repository specific features.
 */
public interface Filter<T> {

    public boolean accept(T object);
}
