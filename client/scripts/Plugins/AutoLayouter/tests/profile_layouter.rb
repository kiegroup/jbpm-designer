$LOAD_PATH.unshift File.join(File.dirname(__FILE__), "..", "src")

require 'layout'
require 'ruby-prof'

RubyProf.start
OryxLayouter::Layout::automatic_layout(ARGV[0])
result = RubyProf.stop
printer = RubyProf::FlatPrinter.new(result)
File.open("profile_run.txt", "w"){ |file| printer.print(file, 0)}
