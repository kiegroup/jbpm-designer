/**
 * Copyright (c) 2010
 * Christian Ress <bart@oryx-uml.the-bart.org>
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

/**
 * @namespace Oryx name space for plugins
 * @name ORYX.Plugins
*/
 if(!ORYX.Plugins)
	ORYX.Plugins = new Object();

/**
 * The UML plugin provides layout methods referring to the UML stencilset. 
 * 
 * @class ORYX.Plugins.UML
 * @extends Clazz
 * @param {Object} facade The facade of the editor
 */
ORYX.Plugins.UML = 
/** @lends ORYX.Plugins.UML.prototype */
{
	/**
	 * Creates a new instance of the UML plugin and registers it on the
	 * layout events listed in the UML stencil set.
	 * 
	 * @constructor
	 * @param {Object} facade The facade of the editor
	 */
	construct: function(facade) {
		this.facade = facade;
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handlePropertyChanged.bind(this));
		this.facade.registerOnEvent('layout.uml.class', this.handleLayoutClass.bind(this));
		this.facade.registerOnEvent('layout.uml.list', this.handleLayoutList.bind(this));
		this.facade.registerOnEvent('layout.uml.association', this.handleLayoutAssociation.bind(this));
		this.facade.registerOnEvent('layout.uml.qualified_association', this.handleLayoutQualifiedAssociation.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.addReadingDirectionOnLoad.bind(this));

	},
	
	/**
	 * Add reading direction on load.
	 *
	 * Because the reading direction arrow is added to the
	 * label of an edge, we have to iterate over all edges
	 * and add the arrow on load for it to appear.
	 *
	 * @param {Object} event
	 */
	addReadingDirectionOnLoad : function(event) {
		this.facade.getCanvas().edges.each(function(shape){
			if (shape.properties["oryx-direction"] == "left" || shape.properties["oryx-direction"] == "right") {
				this.addReadingDirection(shape);
			}
		}.bind(this));
	},
	
	/**
	 * calculates the height of a text, taking line breaks into consideration
	 *
	 * @param {Object} labelElement the label
	 * @param {String} the label test
	 */
	
	calculateLabelHeight : function (labelElement, labelValue) {
		var fontSize = labelElement.getFontSize();
		var newlineOccurences = 1;
		
		labelValue.scan('\n', function() { newlineOccurences += 1; });
		
		// 0.75 account for padding around the label
		return newlineOccurences * fontSize + 0.75;
	},
	
	/**
	 * Add Reading Direction to the name label after it has been changed
	 */
	handlePropertyChanged : function(event) {
		if (event["key"] == "oryx-name") {
			this.addReadingDirection(event.elements[0]);
		}
	},
	
	/**
	 * Layout class shapes.
	 *   - make text italic when abstract
	 *   - resize methods and attributes boxes according to their content
	 */
	handleLayoutClass : function(event) {
		var shape = event.shape;
		
		if (shape.propertiesChanged["oryx-abstract"] == true) {
			var className = event.shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "className") }
				);
			
			if (shape.properties["oryx-abstract"] == true) {
				className.node.setAttribute("font-style", "italic");
			} else {
				className.node.setAttribute("font-style", "none");
			}
		}
		
		if (shape.propertiesChanged["oryx-attributes"] == true || shape.propertiesChanged["oryx-methods"]) {
			var attributesValue = event.shape.properties["oryx-attributes"];
			var methodsValue = event.shape.properties["oryx-methods"];
			var attributes = event.shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "attributes") }
				);
			var methods = event.shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "methods") }
				);
			var separator = event.shape._svgShapes.find(
					function(element) { return element.element.id == (event.shape.id + "separator") }
				).element;
					
					
			var attributesHeight = this.calculateLabelHeight(attributes, attributesValue);
			var methodsHeight = this.calculateLabelHeight(methods, methodsValue);
			
			// 24px account for the class name
			var distanceTilSeparator = 24 + attributesHeight + 2;
			var distanceTilBottom = distanceTilSeparator + methodsHeight + 2;
			
			separator.setAttribute("y1", distanceTilSeparator);
			separator.setAttribute("y2", distanceTilSeparator);
			
			// realign methods label (so that oryx' internal references are correct)
			methods.y = distanceTilSeparator + 3;
			methods.node.setAttribute("y", distanceTilSeparator + 3);
			// realign the methods line by line (required for a visual change)
			for (var i = 0; i < methods.node.childElementCount; i++) {
				methods.node.childNodes[i].setAttribute("y", distanceTilSeparator + 2);
			}
			
			// resize shape
			shape.bounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + distanceTilBottom + 5
			);
		}
	},
	
	/**
	 * Layout the interface and enumeration shape. Resize according to their content.
	 */
	handleLayoutList : function(event) {
		var shape = event.shape;
		
		if (shape.propertiesChanged["oryx-items"] == true) {
			var itemsValue = shape.properties["oryx-items"];
			var items = shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "items") }
				);
			
			var itemsHeight = this.calculateLabelHeight(items, itemsValue);
		
			var distanceTilBottom = 32 + itemsHeight + 2;
			
			// resize shape
			shape.bounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + distanceTilBottom + 5
			);
		}
	},
	
	/**
	 * Draws the reading direction arrow when an association is changed.
	 */
	handleLayoutAssociation : function(event) {
		this.addReadingDirection(event.shape);
	},
	
	/**
	 * Adds the reading direction to the "name" label of an association.
	 */ 
	addReadingDirection : function(shape) {
		var name = shape.getLabels().find(
					function(label) { return label.id == (shape.id + "name") }
				);
		
		if (shape.properties["oryx-direction"] == "left") {
			name.text("◀ " + shape.properties["oryx-name"]);
		}
		else if (shape.properties["oryx-direction"] == "right") {
			name.text(shape.properties["oryx-name"] + " ▶");
		}
		else {
			name.text(shape.properties["oryx-name"]);
		}
		
		name.update();
	},
	
	
	/**
	 * Resizes the qualifier box of a qualified association according to its content.
	 */
	handleLayoutQualifiedAssociation : function(event) {
		var shape = event.shape;
		var qualifier = shape.getLabels().find(
					function(label) { return label.id == (event.shape.id + "qualifier") }
				);
		
		var size = qualifier._estimateTextWidth(shape.properties["oryx-qualifier"], 12);
		// enforce minimum size, looks bad otherwise
		if (size < 40) size = 40;
		shape._markers.values()[0].element.lastElementChild.setAttribute("width", size+5);
		shape._markers.values()[0].element.setAttributeNS(null, "markerWidth", size+5)
	}
};

ORYX.Plugins.UML = Clazz.extend(ORYX.Plugins.UML);
