$LOAD_PATH.unshift File.join(File.dirname(__FILE__), "..", "src")

require 'test/unit'
require 'oryxrdfdoc'
require 'constants'

class TestOryxRDFDoc < Test::Unit::TestCase
  
  def test_init
    #.new mit ungültigem Parser soll Fehler werfen
    oryx = OryxLayouter::OryxRDFDoc.instance
    assert_raise RuntimeError do
      oryx.set_up("simple_sample", :blabla)
    end
    assert_nothing_raised do
      oryx.set_up("simple_sample")
    end
  end
  
  def test_getter
    modelname = "http://b3mn.hpi.uni-potsdam.de/server.php?resource=simple_sample"
    @doc = OryxLayouter::OryxRDFDoc.instance
    @doc.set_up("simple_sample")
    invalid_resource = "blabla"
    #test get_modelname
    assert_equal(modelname, @doc.get_modelname)
    
    #test get_stencilset
    assert_equal("http://b3mn.hpi.uni-potsdam.de/data/stencilsets/bpmn/bpmn.json",
      @doc.get_stencilset)
      
    #test get_all_resources
    assert_equal(16, @doc.get_all_resources.size)
    
    #test get_type
    assert_equal("http://b3mn.org/stencilset/bpmn#BPMNDiagram",
      @doc.get_type(modelname))
    assert_nil(@doc.get_type(invalid_resource))  
    
    #test get_type_category
    sequence_flow = modelname + "#resource10"
    task = modelname + "#resource2"
    lane = modelname + "#resource1"
    assert_equal(OryxLayouter::Container, 
      @doc.get_type_category(lane))
    assert_equal(OryxLayouter::Edge, 
      @doc.get_type_category(sequence_flow))
    assert_equal(OryxLayouter::Node, 
      @doc.get_type_category(task))
    assert(OryxLayouter::Resource_Type_Categories.include?(
      @doc.get_type_category(lane)))
    assert(OryxLayouter::Resource_Type_Categories.include?(
      @doc.get_type_category(sequence_flow)))
    assert(OryxLayouter::Resource_Type_Categories.include?(
      @doc.get_type_category(task)))
    assert_raise(RuntimeError){@doc.get_type_category(invalid_resource)}
    
    #test get_parent
    assert_equal(lane, @doc.get_parent(task))
    assert_nil(@doc.get_parent(invalid_resource))
    
    #test get_bounds
    assert_equal([30,0,600,250], @doc.get_bounds(lane))
    assert_equal([110,90,210,170], @doc.get_bounds(task))
    assert_nil(@doc.get_bounds(invalid_resource))
    
    #test get_size
    assert_equal([570,250], @doc.get_size(lane))
    assert_equal([100,80], @doc.get_size(task))
    assert_nil(@doc.get_size(invalid_resource))
    
    #test get_successors
    assert_equal(sequence_flow, @doc.get_successors(task).first)
    assert_equal([], @doc.get_successors(invalid_resource))
    
    #test get_predecessors
    assert_equal(task, @doc.get_predecessors(sequence_flow).first)
    assert_equal([], @doc.get_predecessors(invalid_resource))
    
    #test get_resources_with_parent
    assert_equal(14, @doc.get_all_resources_with_parent(lane).size)
    assert_equal([], @doc.get_all_resources_with_parent(invalid_resource))
    
    #test get_label
    assert_equal("Blubb", @doc.get_label(lane))
    assert_equal("A", @doc.get_label(task))
    assert_nil(@doc.get_label(invalid_resource))
  end
  
end