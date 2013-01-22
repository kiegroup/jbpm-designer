=begin
 * Copyright (c) 2008
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


require 'shape.rb'

module OryxLayouter
#Represents container in a diagram (anything that can contain other elements)
class ContainerObject < ShapeObject
  
  attr_reader :children
  
  def initialize(id, parent=nil, abs_x=0, abs_y=0, width=30, height=30)
    @children = []
    super
  end
  
  #Calls super and then adjusts absolute values for all children
  def set_abs(abs_x, abs_y)
    super
    @children.each {|shape| shape.set_abs(@abs_x + shape.x, @abs_y + shape.y)}
  end
  
  def add_child(shape)
    @children << shape
  end
  
  def remove_child(shape)
    @children.delete(shape)
  end
  
  def to_s
    super.gsub("ShapeObject", "ContainerObject")
  end
  
end

end #module OryxLayouter