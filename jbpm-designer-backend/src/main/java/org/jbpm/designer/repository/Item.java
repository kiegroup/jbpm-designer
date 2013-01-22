package org.jbpm.designer.repository;

/**
 * Generic type of component managed by repository
 *
 */
public interface Item {
    /**
     * Returns uniqueId of this asset
     * @return unique identifier of this asset
     */
    String getUniqueId();

    /**
     * Returns name of the item if present
     * @return - item name
     */
    String getName();

    /**
     * Returns full name of the asset that usually is name and type.
     * e.g. in case of files it's file name and extension.
     * @return - returns full asset name
     */
    String getFullName();

    /**
     * Returns description of the item if present
     * @return - item description
     */
    String getDescription();

    /**
     * Returns version of this item
     * @return - item version
     */
    String getVersion();

    /**
     * Returns owner (usually user if) of this item
     * @return - item owner
     */
    String getOwner();

    /**
     * Returns date when this item was created
     * @return - item creation date
     */
    String getCreationDate();

    /**
     * Returns date when this item was last time modified
     * @return - item last modification date
     */
    String getLastModificationDate();
}
