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


require 'layoutprog'
require 'oryxrdfdoc'
require 'node'
require 'container'

module OryxLayouter
#Wrapper class for the dot layouting program
class Dot < LayoutProgram
  
  def initialize(filename)
    super
    @edges = []
    @canvas_height = nil
  end
  
  private
   #Starts Dot and collects results
   #
   #Behaviour dependant on Pic_Creation_Mode
  def run_program
    super  
    if Pic_Creation_Mode != :no_pic
      pic = "\"#{Pics_Path}/#{@oryx.get_modelname.split("=").last}.png\""
      puts "Saving layout to file #{pic}" if Debug
      IO.popen("#{Dot_Bin} -Tpng -o#{pic} -q2","r+") do |pipe|
        @input.each{|line| pipe.puts line}
        pipe.close_write
      end
    end
    if Pic_Creation_Mode != :only_pic
      format = " "
      case Layout_Algorithm
      when :standard
        format << " "
      when :simple
        format << "-Tplain "
      end
      IO.popen("#{Dot_Bin}#{format}-q2","r+") do |pipe|
        @input.each{|line| pipe.puts line}
        pipe.close_write
        @output = pipe.read
      end
    end
  end
  
   #Parses information from OryxRDFDoc into the Dot language
  def parse_input_from_model
    super
    resources = @oryx.get_all_resources
    @input = "strict digraph G{\n  center=true;compound=true\n"
    case Flow_Direction
    when :left_right
      @input << "  rankdir=LR\n"
    when :top_down
      @input << "  rankdir=TB\n"
    when :bottom_up
      @input << "  rankdir=BT\n"
    when :right_left
      @input << "  rankdir=RL\n"
    end
    @input << "  node [shape=box]\n"
    resources.each do |resource|
      #filter edges, since they're parsed in the end
      if Edge == @oryx.get_type_category(resource)
        @edges << resource
      #parse only top-level resources
      #not top-level resources are inside containers and will be parsed in their context
      elsif @oryx.get_parent(resource).nil?
        parse_composition(resource)
      end
    end
    @edges.each do |edge|
      parse_edge(edge)
    end
    @input << "\n}\n"
  end
  
  #Parses flow objects and container
  def parse_composition(resource)
    case @oryx.get_type_category(resource)
    when Node
      size = @oryx.get_size(resource).collect{ |num| num/Scaling}
      @input << "  #{print_resource(resource)}"#[label=#{make_label(resource)}"
      @input << "[fixedsize=true,width=#{size[0]},height=#{size[1]}"
      @input << "]\n"
    when Container
      @input << "subgraph cluster_#{print_resource(resource)}{\n"
      #@input << "  size=1.0\n"
      children = @oryx.get_all_resources_with_parent(resource)
      children.each do |child_resource|
        parse_composition(child_resource)
      end
      @input << "}\n"
    when Edge
      #do nothing, edges are parsed later
    end
  end
  
  #Returns a label for the given resource depending on Pic_Creation_Mode to ensure better readability when in only_pic mode.
  def make_label(resource)
    label = ""
    if Pic_Creation_Mode == :only_pic
      label = @oryx.get_label(resource)
      label = @oryx.get_type(resource).split("#").last if label.nil?
    else
      label = resource.to_s.split("#").last
    end
    label
  end
  
  
  def parse_edge(resource)
    @oryx.get_predecessors(resource).each do |pred|
      @oryx.get_successors(resource).each do |succ|
        @input << "  #{print_resource(pred)} -> #{print_resource(succ)}\n"
      end
    end
  end
  
  #Returns resources name without the URL
  def print_resource(resource)
    "#{resource.to_s.split("#").last}"
  end
  
  # Parses result of a layout run into an internal data structure
  #
  # Return format as seen below
  #   {Node => {:<resource_id> => <NodeObject>, ...}, Container => {...}, Edge => {}}
  def parse_layout_from_output
    super
    case Layout_Algorithm
    when :simple
      parse_plain
    when :standard
      parse_attr_dot
    end
    if Debug 
      #print new positions
      @layout[Container].each_value {|c| puts c.to_s}
      @layout[Node].each_value {|n| puts n.to_s}
    end
  end
  
  #Parses Dots 'plain' output format
  def parse_plain
    canvas = nil
    @output.each do |line|
      puts line if Debug
      values = line.chomp.split
      graph_count = 0
      case values[0]
      when "graph"
        canvas = ContainerObject.new(
          "canvas", nil, 0, 0, *values[2..3].collect{ |str| str.to_f * Scaling})
        @layout[Container][:canvas] = canvas
        raise "Layout-multiple graph nodes detected" if (graph_count+=1) > 1
      when "node"
        x, y, width, height = *values[2..5].collect{ |str| str.to_f * Scaling}
        x, y = get_upper_left(x, y, width, height)
        obj = NodeObject.new(values[1], canvas, x, y, width, height)
        @layout[Node][values[1].to_sym] = obj
      when "edge"
        next
      when "stop"
        break
      end
    end
  end
  
  #Parses Dots 'dot'(attributed dot) output format
  def parse_attr_dot
    parents = [nil, "canvas"] #canvas is root-parent and has no parent(==nil)
    parent_objects = [nil]
    @output.each do |line|
      puts line if Debug
      if (line =~ /subgraph cluster_(\w+)/)
        parents << $1
        puts "Found container named: #{parents.last}" if Debug
        
      elsif (line =~ /graph \[bb=(.*)\]/)
        bounds = $1.split(",").collect { |str| str.to_f}
        parent = parent_objects.last
        min = Min_Container_Size
        if bounds[0].nil? || bounds[1].nil? || bounds[2].nil? || bounds[3].nil?
          bounds = [parent.abs_x, parent.abs_y, 
            parent.abs_x + min, parent.abs_y + min]
        end
        height = bounds[3] - bounds[1]
        width = bounds[2] - bounds[0]
        width = min if width < min
        height = min if height < min
        @canvas_height ||= height #the first height is always the height of complete model
        bounds[1] = revert_y(bounds[1]) - height
        obj = ContainerObject.new(
            parents.last,             #id
            parent_objects.last,   #parent
            bounds[0], bounds[1], #x, y (upper left)
            width,#width
            height)#height
        @layout[Container][parents.last.to_sym] = obj
        parent_objects << obj
        puts "Container size" if Debug
        
      elsif (line =~ /\s*\}\s*/)
        parents.pop
        parent_objects.pop
        puts "End container" if Debug
        
      elsif (line =~ /(\w+).*width="(.+)", height="(.+)", pos="(.+)"/)
        temp = [$1, $4, $2, $3]
        bounds = temp[1].split(",").collect {|str| str.to_f}
        #only width and height need to be scaled
        bounds.concat([temp[2],temp[3]].collect {|str| str.to_f * Scaling})
        bounds[0], bounds[1] = get_upper_left(*bounds[0..3])
        @layout[Node][temp[0].to_sym] = NodeObject.new(
          temp[0], parent_objects.last, *bounds[0..3])
        puts "Node found" if Debug
        
      elsif (line =~ /(\w+) \[pos="(.+)", width="(.+)", height="(.+)"\]/)
        temp = [$1, $2, $3, $4]
        bounds = temp[1].split(",").collect {|str| str.to_f}
        #only width and height need to be scaled
        bounds.concat([temp[2],temp[3]].collect {|str| str.to_f * Scaling})
         bounds[0], bounds[1] = get_upper_left(*bounds[0..3])
        @layout[Node][temp[0].to_sym] = NodeObject.new(
          temp[0], parent_objects.last, *bounds[0..3])
        puts "Node found" if Debug
      end
    end
  end
  
  #Returns [ul_x, ul_y], the coordinates of upper left edge, assuming x and y specify the middle of an object
  def get_upper_left(x, y, width, height)
    n_y = revert_y(y)
    n_x = x - (width / 2)
    n_y = n_y - (height / 2)
    return [n_x, n_y]
  end
  
  #Reverts y value from lower-left-coordinate system(dot) to upper-left one(oryx)
  def revert_y(y)
    @canvas_height - y
  end
  
end #class Dot

end #module OryxLayouter