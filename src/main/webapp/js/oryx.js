/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

function printf() {
	
	var result = arguments[0];
	for (var i=1; i<arguments.length; i++)
		result = result.replace('%' + (i-1), arguments[i]);
	return result;
}

// oryx constants.
var ORYX_LOGLEVEL_TRACE = 5;
var ORYX_LOGLEVEL_DEBUG = 4;
var ORYX_LOGLEVEL_INFO = 3;
var ORYX_LOGLEVEL_WARN = 2;
var ORYX_LOGLEVEL_ERROR = 1;
var ORYX_LOGLEVEL_FATAL = 0;
if (!ORYX_LOGLEVEL) {
  var ORYX_LOGLEVEL = 1;
}
var ORYX_CONFIGURATION_DELAY = 100;
var ORYX_CONFIGURATION_WAIT_ATTEMPTS = 10;

if(!ORYX) var ORYX = {};

ORYX = Object.extend(ORYX, {

	PATH: ORYX.CONFIG.ROOT_PATH,

	alreadyLoaded: [],

	configrationRetries: 0,

	availablePlugins: [],

	/**
	 * The ORYX.Log logger.
	 */
	Log: {
	
		__appenders: [
			{ append: function(message) {
				if(typeof(console) !== 'undefined')
				console.log(message); }}
		],
	
		trace: function() {	if(ORYX_LOGLEVEL >= ORYX_LOGLEVEL_TRACE)
			ORYX.Log.__log('TRACE', arguments); },
		debug: function() { if(ORYX_LOGLEVEL >= ORYX_LOGLEVEL_DEBUG)
			ORYX.Log.__log('DEBUG', arguments); },
		info: function() { if(ORYX_LOGLEVEL >= ORYX_LOGLEVEL_INFO)
			ORYX.Log.__log('INFO', arguments); },
		warn: function() { if(ORYX_LOGLEVEL >= ORYX_LOGLEVEL_WARN)
			ORYX.Log.__log('WARN', arguments); },
		error: function() { if(ORYX_LOGLEVEL >= ORYX_LOGLEVEL_ERROR)
			ORYX.Log.__log('ERROR', arguments); },
		fatal: function() { if(ORYX_LOGLEVEL >= ORYX_LOGLEVEL_FATAL)
			ORYX.Log.__log('FATAL', arguments); },
		
		__log: function(prefix, messageParts) {
			
			messageParts[0] = (new Date()).getTime() + " "
				+ prefix + " " + messageParts[0];
			var message = printf.apply(null, messageParts);
			
			ORYX.Log.__appenders.each(function(appender) {
				appender.append(message);
			});
		},
		
		addAppender: function(appender) {
			ORYX.Log.__appenders.push(appender);
		}
	},

	/**
	 * First bootstrapping layer. The Oryx loading procedure begins. In this
	 * step, all preliminaries that are not in the responsibility of Oryx to be
	 * met have to be checked here, such as the existance of the prototpe
	 * library in the current execution environment. Failing to ensure that any
	 * preliminary condition is not met has to fail with an error.
	 */
	load: function() {
		var waitingpanel = new Ext.Window({renderTo:Ext.getBody(),id:'oryx-loading-panel',bodyStyle:'padding: 8px;background:white',title:ORYX.I18N.Oryx.title,width:'auto',height:'auto',modal:true,resizable:false,closable:false,html:'<span style="font-size:11px;">' + ORYX.I18N.Oryx.pleaseWait + '</span>'})
		waitingpanel.show()
				
		ORYX.Log.debug("Oryx begins loading procedure.");
		
		// check for prototype
		if( (typeof Prototype=='undefined') ||
			(typeof Element == 'undefined') ||
			(typeof Element.Methods=='undefined') ||
			parseFloat(Prototype.Version.split(".")[0] + "." +
				Prototype.Version.split(".")[1]) < 1.5)

			throw("Application requires the Prototype JavaScript framework >= 1.5.3");
		
		ORYX.Log.debug("Prototype > 1.5 found.");

		init();
	}

});

ORYX.Log.debug('Registering Oryx with Kickstart');
Kickstart.register(ORYX.load);

