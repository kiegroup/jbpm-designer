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

# Module providing functionality for automatic layouting of hierarchical, directed graphs.
module OryxLayouter

#---------------paths--------------------------

#Home folder for OryxLayouter
Dev_Path = File.join(File.dirname(__FILE__), "..")

#Location of Dot layouter executable
#Dot_Bin = "#{File.join(Dev_Path, "bin")}/dot.exe"	# For Windows (included)
#Dot_Bin = "/usr/local/bin/dot"						# For OS X (get installer)
Dot_Bin = "/usr/bin/dot"							# For Debian (apt-get graphiz)

#Folder for debugging pictures
Pics_Path = File.join(Dev_Path,"debug_pictures")

#Folder for test cases
Test_Path = File.join(Dev_Path, "tests")

#-----------layouting parameters----------------------

#Underlying layout program; currently only ":dot" supported
Layout_Program = :dot

#Format of resulting layout; supported values
#  * ":json"
#  * ":string"
Return_Format = :json 

#Algorithm to use; currently supported:
#  * ":simple" - container are ignored, only nodes are layouted
#  * ":standard" - containers sizes are adjusted depending on their childrens layout
Layout_Algorithm = :standard #(:simple || :standard) simple => container not resized

#Scaling factor to convert from Oryx to Dot coordinates; Oryx/Scaling = Dot
Scaling = 50.0 

#Minimal size for containers (emtpy containers are ignored by Dot
Min_Container_Size = 30

#Direction of overall flow
#  *":left_right"
#  *":top_down"
#  *":bottom_up"
#  *":right_left"
Flow_Direction = :left_right

#----------------Debugging--------------------------------
#Switch debugging on/off
Debug = false
#Specifies mode for debug picture creation 
#  *":only_pic"
#  *":additional_pic"
#  *":no_pic"
Pic_Creation_Mode = :no_pic

#---------------RDF-Parser----------------------------------

#Parser to use, when none is specified
Default_RDF_Parser = :rena
#RDF-Parser setup
RDF_Parser = {
  :rena => lambda {|filename|
    require 'rdfrena'
    RenaRDFParser.new(filename)
  },
  :active_rdf => lambda {|filename|
    #add constructor here and return parser object
    raise(NotImplementedError, "Constructor for Active-RDF not added yet")
  }
}

#-------------------notation specific settings------------------------

#Amount of space reserved for title of pools
BPMN_Dist_Pool_Lane = 30

#--------------------objects of layouting-----------------------------

#Anything that can contain other objects
Container = :container 
#All kinds of nodes
Node = :node 
#All kinds of edges
Edge = :edge 
#Each object needs to map to one of these categories
Resource_Type_Categories = [Container, Node, Edge]

end #module OryxLayouter
