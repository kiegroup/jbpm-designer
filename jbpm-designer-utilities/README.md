#jBPM Designer Image Utilities#

## bpmn2 svg files
Whenever svg images referenced by the main bpmn2 stencilset template 
      jbpm-designer/jbpm-designer-client/src/main/resources/org/jbpm/designer/public/stencilsets/bpmn2.0jbpm/stencildata/bpmn2.0jbpm.orig
are modified, you should regenerate the file by running:

* first edit the paths in jbpm-designer-utilities/src/main/java/org/jbpm/designer/utilities/svginline/RunSvgInline.java to point to 
locations on your own machine
* mvn clean install -DskipTests
* mvn exec:java
* then copy the new generated stencilset file "....orig.svginline" over the top of the original stencilset file

## Image Sprites
Whenever icons in the toolbar, palette, shapemorph menus or simulation graph are modified
or removed / added, you should re-generate the sprites. See sprites/README.md
