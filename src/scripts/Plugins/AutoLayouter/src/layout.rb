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


require 'constants'
require 'dot'

module OryxLayouter
#Represents the layout of a diagram
class Layout
  attr_reader :container, :nodes
  
  #Starts automatic layouting for diagram in filename and returns new layout
  def Layout.automatic_layout(filename)
    layout = self.new
    layout.load_model(filename)
    puts (new_layout =  layout.get_new_layout)
    return new_layout
  end
  
  
  def initialize
    @nodes = []
    @container = []
    @layout_program = nil
    @oryx = nil
    @filename = nil
  end
  
  #Opens model in filename and creates layout information
  def load_model(filename)
    @filename = filename
    case Layout_Program
    when :dot
      @layout_program = Dot.new(filename)
    else
      raise "Layout-No valid input structure selected:#{Layout_program}"
    end
    info = @layout_program.compute_layout_information
    @container = info[Container]
    @nodes = info[Node]
    apply_notation_specific_changes
  end
  
  #Returns layout information in the format specified in Return_Format
  def get_new_layout
    if Return_Format == :json
      return to_json
    else
      return to_s
    end
  end
  
   #Return layout information in JSON compatible format
  def to_json
    json = "{"
    @container.each_value {|cont| json << "#{cont.to_json},"}
    @nodes.each_value {|node| json << "#{node.to_json},"}
    json = json[0..json.size-2]   #delete last ','
    json << "}"
    json
  end
  
  #Returns plaintext representation of layout
  def to_s
    str = "Layout[\n"
    @container.each_value {|c| str << "  #{c.to_s}\n"}
    @nodes.each_value {|n| str << "  #{n.to_s}\n"}
    str << "]"
  end
  
  private
  
  #Checks whether special changes for the notation of the diagram are specified and applies them correspondingly
  def apply_notation_specific_changes
    @oryx = OryxRDFDoc.instance
    if @oryx.filename != @filename
      raise "Differing filenames\n  original:#{@filename}\n  oryx:#{@oryx.filename}"
    end
    
    if @oryx.get_stencilset =~ /stencilsets\/bpmn\/bpmn.json/
      apply_bpmn_changes
    elsif @oryx.get_stencilset =~ /stencilset\/petrinet/
      apply_petrinet_changes
    end
  end
  
  #Should be self-descriptive
  def apply_bpmn_changes
    bpmn_pool_lane_distance
    bpmn_intermediate_events
  end
  
  #Just for exemplary reasons, not functionality implemented yet
  def apply_petrinet_changes
    #petrinet_token
  end
  
  #Gets full resource id by appending id to current modelname
  def complete_id(id)
    "#{@oryx.get_modelname}##{id}"
  end
  
  #Sets bounds of lanes so that they are justified with the pool 
  #and a little space to the left or top, depending on Flow_Direction
  #
  #Remark: The current BPMN stencilset only supports a left-to-right flow direction,
  #so one will get weird results when trying to make a top-down layout
  def bpmn_pool_lane_distance
    @container.each_pair do |id, cont|
      res_id = complete_id(id)
      if @oryx.get_type(res_id) =~ /bpmn#Pool/
        #get corresponding lane objects
        lanes = cont.children.reject {|res| #remove all non-lane objects
          @oryx.get_type(complete_id(res.res_id)) !~ /bpmn#Lane/
        }
        case Flow_Direction
        when :left_right #leave some space to the left of lanes
          cont.width += BPMN_Dist_Pool_Lane
          #sort lanes according to their position beginning with the topmost to retain ordering of lanes
          lanes.sort! {|l1, l2| l1.y <=> l2.y}
          max_height = 0
          lanes.each {|lane| max_height = lane.height if max_height < lane.height}
          cont.height = max_height * lanes.size
          (0..lanes.size-1).each do |i|
            lanes[i].width = cont.width - BPMN_Dist_Pool_Lane
            #set height of all lanes to max_height
            lanes[i].height = max_height
            prev_y = (i==0) ? 0 : lanes[i-1].y
            prev_height = (i==0) ? 0 : lanes[i-1].height
            abs_y = cont.abs_y + prev_y + prev_height
            abs_x = cont.abs_x + BPMN_Dist_Pool_Lane
            lanes[i].set_abs(abs_x, abs_y)
          end #each
          
        when :top_down #leave some space at the top of lanes
          cont.height += BPMN_Dist_Pool_Lane
          #sort lanes according to their position beginning with the leftmost to retain ordering of lanes
          lanes.sort! {|l1, l2| l1.x <=> l2.x}
          max_width = 0
          lanes.each {|lane| max_width = lane.width if max_width < lane.width}
          cont.width = max_width * lanes.size
          (0..lanes.size-1).each do |i|
            lanes[i].height = cont.height - BPMN_Dist_Pool_Lane
            #set width of all lanes to max_width
            lanes[i].width = max_width
            prev_x = (i==0) ? 0 : lanes[i-1].x
            prev_width = (i==0) ? 0 : lanes[i-1].width
            abs_y = cont.abs_y + BPMN_Dist_Pool_Lane
            abs_x = cont.abs_x + prev_x + prev_width
            lanes[i].set_abs(abs_x, abs_y)
          end #each
        end #case
      end #if
    end #each_pair
  end
  
  #Searches for intermediate events docked to a task and positions them accordingly
  def bpmn_intermediate_events
    @nodes.each_pair do |id, node|
      res_id = complete_id(id)
      if @oryx.get_type(res_id) =~ /Intermediate.*Event/
        pre = @oryx.get_predecessors(res_id)
        return if pre.size != 1 #docked intermediate events can only have one predecessor
        #TODO: intermediate message events can have incoming edges, should be supported
        return if @oryx.get_type(pre.first) !~ /Task/
        puts "Docked intermediate event discovered" if Debug
        #reposition event to lower right corner of task
        #TODO: consider multiple events on one task
        task = @nodes[pre.first.split("#").last.to_sym]
        abs_x = task.abs_x + task.width - (node.width/2)
        abs_y = task.abs_y + task.height - (node.height/2)
        node.set_abs(abs_x, abs_y)
      end
    end
  end
  
end #class Layout

end #module OryxLayouter