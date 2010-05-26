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


require 'rdfparser'
require 'constants'
require 'singleton'

module OryxLayouter
#--TODO: Predicate for type category
#--------------------predicates and their namespaces----------------------
OryxNS = "http://oryx-editor.org/"
RazielNS = "http://raziel.org/"



Type = "#{OryxNS}type"
Render = "#{OryxNS}render"
Parent = "#{RazielNS}parent"
Bounds = "#{OryxNS}bounds"
Outgoing = "#{RazielNS}outgoing"
Name = "#{OryxNS}name"
Self = "#{RazielNS}self"
Stencilset = "#{OryxNS}stencilset"

#Convenience class for accessing information of Oryx RDF-documents
class OryxRDFDoc
  include Singleton
  
  attr_reader :filename
  
  def initialize
    @filename = nil
    @rdf = nil
    @modelname = nil
  end
  
  #Prepares for a new document
  def set_up(filename, parser=Default_RDF_Parser)
    @filename = filename
    if RDF_Parser.key? parser then
      @rdf = RDF_Parser[parser].call(filename)
    else
      raise "OryxRDFDoc-Invalid RDF parser selected: #{parser}"
    end
    get_modelname
  end
  
  #Returns the name of current model
  def get_modelname
    if @modelname.nil?
      triple = @rdf.get_triples(nil, Self).first
      if triple.nil?
        @modelname = @rdf.get_triples(nil, Type).first[0].split("#").first
        #raise "OryxRDFDoc-No modelname found for model:#{@modelname}"
      else
        @modelname = triple[0]
      end
    else
      @modelname
    end
  end
  
  #Returns the stencilset of the current model
  def get_stencilset
    triple = @rdf.get_triples("#{@modelname}#oryx-canvas123", Stencilset).first
    if triple.nil? then raise "OryxRDFDoc-No stencilset found for model:#{@modelname}"
    end
    triple[2]
  end
  
  #Returns all resources that are subject to layouting (all resources that can be rendered)
  def get_all_resources
    @rdf.get_triples("#{@modelname}#oryx-canvas123", Render).collect{ |triple| triple[2]}
  end

  #Returns the type of construct represented by the given resource (e.g. Task, IntermediateEvent, Transition,...)
  #
  #Returns nil if no 'type' was found
  def get_type(subject)
    triple = @rdf.get_triples(subject.to_s, Type).first
    return triple.nil? ? nil : triple[2]
  end

  #Returns the constant for the category to which the subjects type belongs (Container, Flow_Object or Edge)
  def get_type_category(subject)
    type = get_type(subject)
    if is_edge?(subject, type)
      return Edge
    elsif is_node?(subject, type)
      return Node
    elsif is_container?(subject, type)
      return Container
    else
      raise "OryxRDFDoc-No type category found for subject '#{subject.to_s}' with type '#{type.to_s}'"
    end
  end
  
  #Returns the parent resource of subject, nil if no parent was found
  def get_parent(subject)
    triple = @rdf.get_triples(subject.to_s, Parent).first
    return triple.nil? ? nil : triple[2]
  end
  
  #Returns the bounds of subject in the form [upper_left_x, upper_left_y, lower_right_x, lower_right_y]
  def get_bounds(subject)
    triple = @rdf.get_triples(subject.to_s, Bounds).first
    return triple.nil? ? nil : triple[2].split(",").collect{|string| string.to_i}
  end
  
  #Returns [width, height] of subject
  def get_size(subject)
    bounds = get_bounds(subject)
    return bounds.nil? ? nil : [bounds[2] - bounds[0], bounds[3] - bounds[1]]
  end
  
  #Returns all resources that succeed the subject (e.g. edges that base at a flow-object,
  # or flow-objects that are at the head of a edge)
  def get_successors(subject)
    @rdf.get_triples(subject.to_s, Outgoing).collect{ |triple| triple[2]}
  end
  
  #Returns all resources that precede the subject
  def get_predecessors(subject)
    @rdf.get_triples(nil, Outgoing, subject.to_s).collect{ |triple| triple[0]}
  end
  
  #Returns all resources that have the given resource as their parent
  def get_all_resources_with_parent(parent_resource)
    @rdf.get_triples(nil, Parent, parent_resource.to_s).collect{ |triple| triple[0]}
  end
  
  #Returns the label of subject
  def get_label(subject)
    triple = @rdf.get_triples(subject.to_s, Name).first
    return triple.nil? ? nil : triple[2]
  end
  
  
private
  #transitional; checks whether resource is an edge
  def is_edge?(resource, type)
    if (type =~ /Flow/) || (type =~ /Association/)
      return true
    else
      return false
    end
  end
  
  #transitional; checks whether resource is a node
  def is_node?(resource, type)
    if ( (type =~ /Task/) || (type =~ /Gateway/) || #BPMN
      (type =~ /Event/) || (type =~ /Object/) || 
      (type =~ /Subprocess/) || (type =~ /Annotation/) || 
      (type =~ /Transition/)|| (type =~ /Place/)) #Petrinets
      return true
    else
      return false
    end
  end

  #transitional; checks whether resource is a container
  def is_container?(resource, type)
    #in BPMN nur Lane, Pool
    if (type =~ /Lane/) || (type =~ /Pool/)
      return true
    else
      return false
    end
  end
  
end #class OryxRDFDoc

end #module OryxLayouter
