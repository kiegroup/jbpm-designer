/* Used interfaces may change in newer
 * Mozilla versions and 
 * code may stop working.
 */
  function externalLoad(uri){
  	try {
		// first construct an nsIURI object using the ioservice
var ioservice = Components.classes["@mozilla.org/network/io-service;1"]
                          .getService(Components.interfaces.nsIIOService);

var uriToOpen = ioservice.newURI(uri, null, null);

var extps = Components.classes["@mozilla.org/uriloader/external-protocol-service;1"]
                      .getService(Components.interfaces.nsIExternalProtocolService);

// now, open it!
extps.loadURI(uriToOpen, null);

	}catch(e){
		alert(e);
	}
  
  };