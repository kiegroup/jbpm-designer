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


module OryxLayouter
#Abstract parent class for layout programs (such as Dot or Neato)
class LayoutProgram
  
  def initialize(filename)
    @input = nil
    @output = nil
    @layout = nil
    @oryx = OryxRDFDoc.instance
    @oryx.set_up(filename)
  end
  
  #returns the computed layout information, for return's data structure see below
  def compute_layout_information
    parse_input_from_model
    puts "Layout-Program's input:\n#{@input}" if Debug
    run_program
    parse_layout_from_output
    @layout
  end
  
  protected
  #Parses information from OryxRDFDoc into layouters input structure
  def parse_input_from_model
    @input = ""
  end
  
  #Starts layouter and collects results
  def run_program
    @output = ""
  end
  
  # Parses result of a layout run into an internal data structure
  #
  # Return format as seen below
  #   {Node => {:<resource_id> => <NodeObject>, ...}, Container => {...}, Edge => {}}
  def parse_layout_from_output
    @layout = {Node => {}, Container => {}, Edge => {}}
  end
  
end

end #module OryxLayouter