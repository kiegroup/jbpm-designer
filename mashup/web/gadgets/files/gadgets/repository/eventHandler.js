/**
 * Copyright (c) 2008
 * Bj�rn Wagner, Sven Wagner-Boysen
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

// set namespace

function EventHandler() {
	this._callbacks = new Array();
}

EventHandler.prototype = {
	registerCallback : function(callback) {
		if ( callback instanceof Function ) {
			this._callbacks.push(callback);
		}
	},
	
	unregisterCallback : function(callback) {
		// if callback exists
		if (this._callbacks.indexOf(callback) > -1) {
			this._callbacks[this._callbacks.indexOf(callback)] = null; // remove it
			this._callbacks = this._callbacks.compact(); // remove null item from array
		}
	},
	
	invoke : function(arg) {
		this._callbacks.each(function(callback) { 
			try {
				callback(arg);
			} catch(e) {
				// if the call fails do nothing but call remaining callbacks
			}});
	},
	
	invoke : function(arg1, arg2) {
		this._callbacks.each(function(callback) {callback(arg1, arg2);})
	}
}