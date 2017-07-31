/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.utilities.svginline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * When run for the first time on a stencilset, takes a stencilset StringTemplate
 * and replaces view="<file>.svg" properties of nodes with the contents of the
 * referenced .svg files and adds a _view_file="<file>.svg" property.
 * <p>
 * When run again, updates the svg xml in view="..." properties by reading the svg
 * file in the previous _view_file="<file>.svg" property.
 */

public class SvgInline {

    private static final String PROPERTY_NAME_VIEW = "view";
    private static final String SVG_SUFFIX = ".svg";
    private static final String VIEW_FOLDER = "view";
    private static final String VIEW_PROPERTY_NAME_PATTERN = "\\\"view\\\"\\s*:.*$";
    private static final String VIEW_PROPERTY_VALUE_SUFFIX = ".svg\",";
    private static final String VIEW_FILE_PROPERTY_NAME_PATTERN = "\\\"_view_file\\\"\\s*:.*$";
    private static final String SVG_FILE_SUFFIX = ".svg";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String ssInFile;
    private String ssOutFile;

    private String viewFolder;

    // Cache for SVG file contents as JSON values
    private Map<String, String> mapSVG = new HashMap<String, String>();
    private Map<String, Integer> mapSVGCounts = new HashMap<String, Integer>();

    public SvgInline(String ssInFile,
                     String ssOutFile) throws IOException {
        this.ssInFile = ssInFile;
        this.ssOutFile = ssOutFile;

        this.viewFolder = getViewFolderFromSSFile();
    }

    private String getViewFolderFromSSFile() throws IOException {
        File inFile = new File(ssInFile);
        return inFile.getParentFile().getParentFile().getCanonicalPath() + File.separator + VIEW_FOLDER + File.separator;
    }

    /**
     * Processes a stencilset template file
     * @throws IOException
     */
    public void processStencilSet() throws IOException {
        StringBuilder stencilSetFileContents = new StringBuilder();

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(ssInFile),
                                  "UTF-8");
            String currentLine = "";
            String prevLine = "";
            while (scanner.hasNextLine()) {
                prevLine = currentLine;
                currentLine = scanner.nextLine();

                String trimmedPrevLine = prevLine.trim();
                String trimmedCurrentLine = currentLine.trim();

                // First time processing - replace view="<file>.svg" with _view_file="<file>.svg" + view="<svg_xml>"
                if (trimmedCurrentLine.matches(VIEW_PROPERTY_NAME_PATTERN) && trimmedCurrentLine.endsWith(VIEW_PROPERTY_VALUE_SUFFIX)) {
                    String newLines = processViewPropertySvgReference(currentLine);
                    stencilSetFileContents.append(newLines);
                }
                // Second time processing - replace view="<svg_xml>" with refreshed contents of file referenced by previous line
                else if (trimmedPrevLine.matches(VIEW_FILE_PROPERTY_NAME_PATTERN) && trimmedPrevLine.endsWith(VIEW_PROPERTY_VALUE_SUFFIX)
                        && trimmedCurrentLine.matches(VIEW_PROPERTY_NAME_PATTERN)) {
                    String newLines = processViewFilePropertySvgReference(prevLine,
                                                                          currentLine);
                    stencilSetFileContents.append(newLines);
                } else {
                    stencilSetFileContents.append(currentLine + LINE_SEPARATOR);
                }
            }
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ssOutFile),
                                                            "UTF-8"));
            out.write(stencilSetFileContents.toString());
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        System.out.println("SVG files referenced more than once:");
        for (Map.Entry<String, Integer> stringIntegerEntry : mapSVGCounts.entrySet()) {
            if (stringIntegerEntry.getValue() > 1) {
                System.out.println("\t" + stringIntegerEntry.getKey() + "\t = " + stringIntegerEntry.getValue());
            }
        }
    }

    /**
     * Replaces view="<file>.svg" properties of nodes with the contents of the
     * referenced .svg files and adds a _view_file="<file>.svg" property.
     * @param viewProperty
     * @return
     * @throws IOException
     */
    private String processViewPropertySvgReference(final String viewProperty) throws IOException {
        // find start of value
        int indexOfColon = viewProperty.indexOf(':');
        if (indexOfColon > -1) {
            int indexOfValueStart = -1;
            int indexOfSQ = viewProperty.indexOf('\'',
                                                 indexOfColon + 1);
            int indexOfDQ = viewProperty.indexOf('"',
                                                 indexOfColon + 1);
            if (indexOfSQ > -1) {
                indexOfValueStart = indexOfSQ;
            } else {
                indexOfValueStart = indexOfDQ;
            }
            if (indexOfValueStart > -1) {
                String viewValue = viewProperty.substring(indexOfValueStart + 1);

                // find end of value
                int indexOfValueEnd = -1;
                indexOfSQ = viewValue.lastIndexOf('\'');
                indexOfDQ = viewValue.lastIndexOf('"');
                if (indexOfSQ > -1) {
                    indexOfValueEnd = indexOfSQ;
                } else {
                    indexOfValueEnd = indexOfDQ;
                }
                if (indexOfValueEnd > -1) {
                    viewValue = viewValue.substring(0,
                                                    indexOfValueEnd);
                    if (viewValue.endsWith(SVG_FILE_SUFFIX)) {
                        // read svg file
                        String svgContents = getReferencedSvgFileContents(viewValue);
                        if (svgContents != null) {
                            String ignorableViewProperty = viewProperty.replace("\"view\"",
                                                                                "\"_view_file\"");
                            StringBuilder newViewProperty = new StringBuilder(ignorableViewProperty).append(LINE_SEPARATOR);
                            newViewProperty.append(viewProperty.substring(0,
                                                                          indexOfValueStart));
                            newViewProperty.append("\"").append(svgContents).append("\",").append(LINE_SEPARATOR);
                            return newViewProperty.toString();
                        }
                    }
                }
            }
        }

        // property not changed, return original property
        return viewProperty + LINE_SEPARATOR;
    }

    /**
     * Updates the svg xml in view="..." properties by reading the svg
     * file in the previous _view_file="<file>.svg" property.
     * @param viewFileProperty
     * @param viewProperty
     * @return
     * @throws IOException
     */
    private String processViewFilePropertySvgReference(final String viewFileProperty,
                                                       final String viewProperty) throws IOException {
        // find start of _view_file value
        int indexOfViewFileValueStart = -1;
        int indexOfViewFileColon = viewFileProperty.indexOf(':');
        if (indexOfViewFileColon > -1) {
            int indexOfSQ = viewFileProperty.indexOf('\'',
                                                     indexOfViewFileColon + 1);
            int indexOfDQ = viewFileProperty.indexOf('"',
                                                     indexOfViewFileColon + 1);
            if (indexOfSQ > -1) {
                indexOfViewFileValueStart = indexOfSQ;
            } else {
                indexOfViewFileValueStart = indexOfDQ;
            }
        }

        // find start of view value
        int indexOfViewValueStart = -1;
        int indexOfViewColon = viewProperty.indexOf(':');
        if (indexOfViewColon > -1) {
            int indexOfSQ = viewProperty.indexOf('\'',
                                                 indexOfViewColon + 1);
            int indexOfDQ = viewProperty.indexOf('"',
                                                 indexOfViewColon + 1);
            if (indexOfSQ > -1) {
                indexOfViewValueStart = indexOfSQ;
            } else {
                indexOfViewValueStart = indexOfDQ;
            }
        }

        if (indexOfViewFileValueStart > -1 && indexOfViewValueStart > -1) {
            String viewFileValue = viewFileProperty.substring(indexOfViewFileValueStart + 1);

            // find end of value
            int indexOfViewFileValueEnd = -1;
            int indexOfSQ = viewFileValue.lastIndexOf('\'');
            int indexOfDQ = viewFileValue.lastIndexOf('"');
            if (indexOfSQ > -1) {
                indexOfViewFileValueEnd = indexOfSQ;
            } else {
                indexOfViewFileValueEnd = indexOfDQ;
            }
            if (indexOfViewFileValueEnd > -1) {
                viewFileValue = viewFileValue.substring(0,
                                                        indexOfViewFileValueEnd);
                if (viewFileValue.endsWith(SVG_FILE_SUFFIX)) {
                    // read svg file
                    String svgContents = getReferencedSvgFileContents(viewFileValue);
                    if (svgContents != null) {
                        StringBuilder newViewProperty = new StringBuilder("");
                        newViewProperty.append(viewProperty.substring(0,
                                                                      indexOfViewValueStart));
                        newViewProperty.append("\"").append(svgContents).append("\",").append(LINE_SEPARATOR);
                        return newViewProperty.toString();
                    }
                }
            }
        }

        // property not changed, return original property
        return viewProperty + LINE_SEPARATOR;
    }

    private String getReferencedSvgFileContents(String svgFilename) throws IOException {
        // Returned cached file contents if available
        if (mapSVG.containsKey(svgFilename)) {
            mapSVGCounts.put(svgFilename,
                             mapSVGCounts.get(svgFilename) + 1);
            return mapSVG.get(svgFilename);
        }

        String svgPath = this.viewFolder + svgFilename;
        File svgFile = new File(svgPath);
        if (svgFile.exists()) {
            String svgFileContents = null;
            svgFileContents = readFile(svgPath);
            svgFileContents = svgFileContents.replace('\r',
                                                      ' ').replace('\n',
                                                                   ' ').replace("\"",
                                                                                "\\\"");

            // Cache the file contents
            mapSVGCounts.put(svgFilename,
                             Integer.valueOf(1));
            mapSVG.put(svgFilename,
                       svgFileContents);
            return svgFileContents;
        }

        return null;
    }

    private String readFile(String pathname) throws FileNotFoundException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname),
                                      "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
