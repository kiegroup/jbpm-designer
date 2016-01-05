#!/bin/sh
SMART_SPRITES_LOCATION=/Users/tihomirsurdilovic/Downloads/smartsprites-0.2.11

# Validate SMART_SPRITES_LOCATION
if [ -d $SMART_SPRITES_LOCATION ];
then
   echo "Generating sprites..."
else
   echo "ERROR: SMART_SPRITES_LOCATION folder $SMART_SPRITES_LOCATION does not exist.\n\tYou should edit the SMART_SPRITES_LOCATION variable in the script then run again."
   exit 1
fi

# cd to folder of this script
cd $(dirname $0)
# Remove existing generated css files, called *-sprite.css
rm ../../jbpm-designer-client/src/main/resources/org/jbpm/designer/public/css/sprites/*-sprite.css
# Generate sprite png's and new *-sprite.css files
$SMART_SPRITES_LOCATION/smartsprites.sh --root-dir-path ../../jbpm-designer-client/src/main/resources/org/jbpm/designer/public
