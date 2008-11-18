
/**
 * Copyright (c) 2008
 * Willi Tscheschner
 * 
 */

/**
 * Implementation of an OpenID Cropping-Tool
 * 
 * 
 */
if( !Repository ){ Repository = {} }
if( !Repository.Helper ){ Repository.Helper = {} }


Repository.Helper.CutOpenID = function( openid, width ){
	
	var OpenIDsRightTruncated = ['blogspot.com', 'verisignlabs.com']
	var cutTemplate = new Ext.Template('<span title="{openid}">{cuttedopenid}</span>');


	width 				= width || 30;
	var shouldCut 		= openid.length >= width;
	if( shouldCut ){
		var rightCut		= OpenIDsRightTruncated.some(function(val){ return openid.toLowerCase().include( val )})
		var cuttedOpenId 	= rightCut ? openid.slice( 0, width ) + "..." : "..."+ openid.slice( openid.length-width ); 		
	}
	
	return shouldCut ? cutTemplate.apply({openid:openid, cuttedopenid:cuttedOpenId}): openid ;
	
}
