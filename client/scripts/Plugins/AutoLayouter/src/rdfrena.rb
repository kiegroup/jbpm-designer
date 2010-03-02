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


require 'rena'
require 'rdfparser.rb'
require 'constants.rb'

module OryxLayouter
#Wrapper class for the Rena RDF parser (http://raa.ruby-lang.org/project/rena)
class RenaRDFParser < RDFParser
  
  def initialize(filename)
    @parser = Rena::MemModel.new
    params = Hash.new
    params[:content_type] = "text/xml"
    @parser.load(filename, params)
  end

  #Searches for all triples, that match with the specified arguments
  #
  #Returns array of all corresponding triples, each being an array of size 3
  def get_triples(subject=nil, predicate=nil, object=nil)
    triples = []
    @parser.each_resource{ |curr_subj|
      if curr_subj.uri
        subj = curr_subj.uri.to_s
      else
        subj = curr_subj.to_s
      end
      if subject.nil? || subj == subject then
        curr_subj.each_property{ |curr_pred, curr_obj|
          pred = curr_pred.to_s
          if curr_obj.is_a? Rena::Resource then
            obj = curr_obj.uri.to_s
          else
            obj = curr_obj.to_s
          end
          if (predicate.nil? || pred == predicate) && (object.nil? || obj == object) then
            triples << [subj, pred, obj]
          end
        } #each_property
      end
    } #each_resource
    triples
  end
  
end #class RenaRDFParser

end #module OryxLayouter