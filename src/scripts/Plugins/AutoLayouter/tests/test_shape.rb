$LOAD_PATH.unshift File.join(File.dirname(__FILE__), "..", "src")

require 'test/unit'
require 'rdfrena'
require 'shape'

class TestShapeObject < Test::Unit::TestCase

  def test_init
    s1 = OryxLayouter::ShapeObject.new("shape1")
    assert_instance_of(OryxLayouter::ShapeObject, s1)
    assert_equal(30, s1.width)
    assert_equal(30, s1.height)
    
    parent1 = OryxLayouter::ShapeObject.new("parent1", nil, 10, 10, 100, 100)
    s2 = OryxLayouter::ShapeObject.new("shape2", parent1, 30, 20)
    assert_same(parent1, s2.parent)
    assert_equal(20, s2.x)
    assert_equal(10, s2.y)
    assert_equal(30, s2.abs_x)
    assert_equal(20, s2.abs_y)
  end
  
  def test_coordinates
    parent1 = OryxLayouter::ShapeObject.new("parent1", nil, 10, 10, 100, 100)
    s2 = OryxLayouter::ShapeObject.new("shape2", nil, 30, 20)
    assert_equal(30, s2.abs_x)
    assert_equal(20, s2.abs_y)
    assert_equal(30, s2.x)
    assert_equal(20, s2.y)
    
    s2.set_abs(40, 20)
    assert_equal(40, s2.x)
    assert_equal(20, s2.y)
    assert_equal(40, s2.abs_x)
    assert_equal(20, s2.abs_y)
    
    s2.set_parent(parent1)
    assert_same(parent1, s2.parent)
    assert_equal(30, s2.x)
    assert_equal(10, s2.y)
    assert_equal(40, s2.abs_x)
    assert_equal(20, s2.abs_y)
    
    parent2 = OryxLayouter::ShapeObject.new("parent2", nil, 10, 10, 10, 10)
    assert_raise(RuntimeError) {s2.set_parent(parent2)}
  end
  
  def test_to_json
    
  end
  
end