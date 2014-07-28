#jBPM Designer Image Sprites#

## Important Note

Whenever icons in the toolbar, palette, shapemorph menus or simulation graph are modified or removed / added, you should re-generate the sprites.

##Image sprites

Image sprites are used to replace sets of .png files in 4 places:

* toolbar images
* palette images
* shapemorph menu icons - the icons surrounding activities in the BP editor
* simulation activity icons - the icons in the process simulation graph representing process activities

The image sprites and .css files containing classes which reference the sprites are generated using [SmartSprites](http://csssprites.org/) from the relevant icons and a set of source .css files which reference the icons.

The source .css files are located in /jbpm-designer-client/src/main/resources/org/jbpm/designer/public/css/sprites:

* toolbar-images.css
* palette-images.css
* simulation-images.css

When the sprites are generated, the sprite .png files and css files which reference the sprite .png files are created. 

The 3 image sprites are generated in jbpm-designer-client/src/main/resources/org/jbpm/designer/public/images/sprites:

* toolbar-images-sprite.png - includes toolbar icons
* palette-images-sprite.png - includes palette and shapemorph menu icons
* simulation-images-sprite.png - includes simulation activity icons

The generated css files are in jbpm-designer-client/src/main/resources/org/jbpm/designer/public/images/sprites:

* toolbar-images-sprite.css
* palette-images-sprite.css
* simulation-images-sprite.css

## Generating the Sprites

* Download and unzip Smart Sprites from [SmartSprites](http://csssprites.org/). I used smartsprites-0.2.10.zip
* Edit the script file runSS.sh located in the current folder and make the variable SMART_SPRITES_LOCATION refer to the location in which Smart Sprites is unzipped.
* Execute the script runSS.sh
* If there are any Warnings or Errors from Smart Sprites during sprite generation, they will need to be fixed
