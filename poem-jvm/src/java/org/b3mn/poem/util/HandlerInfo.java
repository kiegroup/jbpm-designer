package org.b3mn.poem.util;

import org.b3mn.poem.handler.HandlerBase;

public class HandlerInfo {
	protected String uri;
	protected boolean needsModelContext;
	protected boolean permitPublicUserAccess;
	protected boolean filterBrowser;
	protected AccessRight accessRestriction;
	protected HandlerBase handler = null;
	
	public String getUri() {
		return uri;
	}

	public boolean isNeedsModelContext() {
		return needsModelContext;
	}

	public boolean isPermitPublicUserAccess() {
		return permitPublicUserAccess;
	}

	public boolean isFilterBrowser() {
		return filterBrowser;
	}

	public AccessRight getAccessRestriction() {
		return accessRestriction;
	}

	public HandlerBase getHandler() {
		return handler;
	}
	
	public HandlerInfo(HandlerWithoutModelContext annotation) {
		this.uri = annotation.uri();
		this.needsModelContext = false;
		this.permitPublicUserAccess = annotation.permitPublicUserAccess();
		this.filterBrowser = annotation.filterBrowser();
		this.accessRestriction = null;
	}
	
	public HandlerInfo(HandlerWithModelContext annotation) {
		this.uri = annotation.uri();
		this.needsModelContext = false;
		this.permitPublicUserAccess = annotation.permitPublicUserAccess();
		this.filterBrowser = annotation.filterBrowser();
		this.accessRestriction = annotation.accessRestriction();
	}
	
	public HandlerInfo(ExportHandler annotation) {
		this.uri = annotation.uri();
		this.needsModelContext = false;
		this.permitPublicUserAccess = annotation.permitPublicUserAccess();
		this.filterBrowser = annotation.filterBrowser();
		this.accessRestriction = annotation.accessRestriction();
	}
}
