/**
 * Copyright (c) 2009
 * Helen Kaltegaertner
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

if(!connector)
	var connector = {};

(function() {
	
	connector.model = function(title, shapes){
		this.title = (title ? title : '');
		this.shapes = (shapes ? shapes: '');
	};

	connector.connection = function(){
		
		this.modelA = new model();
		this.modelB = new model();
		this.comment = '';
	};
	
	connector.connection.prototype = {
			
		setModelA: function(model){
			this.modelA = model;
		},
		
		setModelB: function(model){
			this.modelB = model;
		},
		
		setTitle: function(title){
			this.title = title;
		},
		
		getShapesA: function(){
			return this.modelA.shapes;
		},
		
		getShapesA: function(){
			return this.modelB.shapes;
		}
	};
})();

