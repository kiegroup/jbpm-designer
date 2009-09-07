package org.oryxeditor.server.diagram;

/**
 * @author Philipp
 * Represents a Stencilset of a shape
 */
public class StencilSet {
	String url;
	String namespace;
	/** Constructs a stencilSet with url and namespace
	 * @param url
	 * @param namespace
	 */
	public StencilSet(String url, String namespace) {
		this.url = url;
		this.namespace = namespace;
	}
	/** Minimal constructor of an stencilset, only expects an uri
	 * @param url
	 */
	public StencilSet(String url) {
		this.url = url;
	}
	/** Gice the specific url of an stencilset
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/** Set a new specific url for an stencilset
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**Give the namespace of a stencilset
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}
	/**Set a new namespace for a stencil set
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
}
