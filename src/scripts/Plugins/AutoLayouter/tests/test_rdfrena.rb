$LOAD_PATH.unshift File.join(File.dirname(__FILE__), "..", "src")

require 'test/unit'
require 'rdfrena'
require 'oryxrdfdoc'

class TestRenaRDFParser < Test::Unit::TestCase
  
  def test_get_triples
    rdf = OryxLayouter::RenaRDFParser.new("simple_sample")
    #triple used for testing:
    base = "http://b3mn.hpi.uni-potsdam.de/server.php?resource=simple_sample"
    base_object = "http://b3mn.org/stencilset/bpmn#BPMNDiagram"
    resource3 = "http://b3mn.hpi.uni-potsdam.de/server.php?resource=simple_sample#resource3"
    resource9 = "http://b3mn.hpi.uni-potsdam.de/server.php?resource=simple_sample#resource9"
    
    #check that result contains arrays of size 3
    assert_equal(3, rdf.get_triples(base).first.size)
    
    #check if searching with subject given finds the right triple
    assert_equal(base_object, rdf.get_triples(base).first[2])
    
    #check searching with object (there should be only one object of type BPMNDiagram)
    assert_equal(base, rdf.get_triples(nil, nil, base_object).first[0])
    
    #check searching with predicate and object
    assert_equal(resource3, rdf.get_triples(nil, OryxLayouter::Outgoing, resource9).first[0])
    
    #check total number of triples
    assert_equal(164, rdf.get_triples.size)
  end
  
end