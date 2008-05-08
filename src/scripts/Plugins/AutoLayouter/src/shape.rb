=begin
 * Copyright (c) 2006
 * Philipp Maschke
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
=end


module OryxLayouter
#Parent class for all objects in a diagram
class ShapeObject
  attr_reader :res_id, :x, :y, :abs_x, :abs_y, :parent
  attr_accessor :width, :height
  #x, y => upper left edge relative to parent
  
  #abs_x, abs_y => absolute upper-left edge coordinates
  def initialize(id, parent=nil, abs_x=0, abs_y=0, width=30, height=30)
    @res_id = id
    @parent = parent
    @width = width
    @height = height
    if @parent.respond_to? :add_child
      @parent.add_child(self)
    end
    set_abs(abs_x, abs_y)
  end
  
  #Sets absolute position, computes relative positions and checks if shape is still within parents bounds
  def set_abs(abs_x, abs_y)
    if @parent.nil?
      @x = abs_x
      @y = abs_y
      @abs_x = abs_x
      @abs_y = abs_y      
    else
      @x = abs_x - parent.abs_x
      @y = abs_y - parent.abs_y
      @abs_x = abs_x
      @abs_y = abs_y
      if outside_parents_bounds?(@parent, abs_x, abs_y)
        #TODO: resize parent(?)
        raise "Shape outside of parents bounds:\n  shape:#{to_s}\n  parent:#{@parent.to_s}"
      end
    end
  end
  
  #Sets a new parent for current shape, removes itself from former parents child list, adds itself to new parents list and computes new relative position
  def set_parent(new_parent)
    if (!@parent.nil?) && (@parent.respond_to?(:remove_child))
      @parent.remove_child(self)
    end    
    @parent = new_parent
    if @parent.respond_to? :add_child
      @parent.add_child(self)
    end
    set_abs(@abs_x, @abs_y)
  end
  
  #Returns shapes string representation
  def to_s
    "ShapeObject[id:#{@res_id}, abs_x:#{@abs_x}, abs_y:#{@abs_y}" +
      ", x:#{@x}, y:#{@y}, width:#{@width}, height:#{@height}" +
      ", parent_id:#{@parent.nil? ? nil : @parent.res_id}]"
  end
  
  #Returns representation of shape in JavaScript Object Notation
  def to_json
    "\"#{@res_id}\":{x:#{@x}, y:#{@y}, width:#{@width}, height:#{@height}}"
  end
  
  private
  
  #Checks whether shape is somewhere outside the parents bounds
  #
  #Parent and absolute position can be specified manually to support what-if checks
  def outside_parents_bounds?(parent=@parent, abs_x=@abs_x, abs_y=@abs_y)
    (parent.abs_x > abs_x) || (parent.abs_y > abs_y) ||
      ((parent.abs_x + parent.width) < (abs_x + @width)) ||
      ((parent.abs_y + parent.height) < (abs_y + @height))
  end
  
end #class ShapeObject

end #module OryxLayouter