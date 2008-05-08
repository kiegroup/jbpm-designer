$LOAD_PATH.unshift File.join(File.dirname(__FILE__), "..", "src")

require 'test/unit'
require 'layout'
require 'constants'

class TestLayout < Test::Unit::TestCase
  
  def setup
    @layout = OryxLayouter::Layout.new
    @layout.load_model(File.join(OryxLayouter::Test_Path, "simple_sample"))
  end

  def test_init
    assert_equal(7, @layout.nodes.size)
  end
  
  
    
end