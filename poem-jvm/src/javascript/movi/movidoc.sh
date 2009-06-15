#!/bin/sh

# The location of your yuidoc install
yuidoc_home=/Users/You/yuidoc

# The location of the files to parse.  Parses subdirectories, but will fail if
# there are duplicate file names in these directories.
parser_in=/Users/You/eclipse_workspace/oryx/poem-jvm/src/javascript/movi/src

# The location to output the parser data.  This output is a file containing a 
# json string, and copies of the parsed files.
parser_out=/Users/You/eclipse_workspace/oryx/poem-jvm/src/javascript/movi/tmp

# The directory to put the html file outputted by the generator
generator_out=/Users/You/eclipse_workspace/oryx/poem-jvm/src/javascript/movi/doc

# The location of the template files.  Any subdirectories here will be copied
# verbatim to the destination directory.
template=$yuidoc_home/template

# The version of your project to display within the documentation.
version=0.3

# The version of YUI the project is using.  This effects the output for
# YUI configuration attributes.  This should start with '2' or '3'.
yuiversion=2.7.0

##############################################################################
# add -s to the end of the line to show items marked private

$yuidoc_home/bin/yuidoc.py $parser_in -p $parser_out -o $generator_out -t $template -v $version -Y $yuiversion
