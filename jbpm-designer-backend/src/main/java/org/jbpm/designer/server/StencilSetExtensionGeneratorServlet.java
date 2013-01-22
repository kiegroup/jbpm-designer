package org.jbpm.designer.server;

/**
 * Copyright (c) 2008
 * SAP Research
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Portions of the code in this file have been developed by
 * Stefan Krumnow and Falko Menge at SAP Research Brisbane and
 * contributed to the Oryx project in October 2008 under the
 * terms of the MIT License.
 *
 * @author Stefan Krumnow
 * @author Falko Menge
 **/

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class StencilSetExtensionGeneratorServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String baseUrl;
	protected Repository repository;
	
	/**
	 * Request parameters are documented in
	 * editor/test/examples/stencilset-extension-generator.xhtml
	 * The parameter 'csvFile' is always required.
	 * An example CSV file can be found in
	 * editor/test/examples/design-thinking-example-data.csv
	 * which has been exported using OpenOffice.org from
	 * editor/test/examples/design-thinking-example-data.ods
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.baseUrl = Repository.getBaseUrl(request);
		this.repository = new Repository(baseUrl);
		
		// parameters and their default values
		String modelNamePrefix                         = "Generated Model using ";
		String stencilSetExtensionNamePrefix           = StencilSetExtensionGenerator.DEFAULT_STENCIL_SET_EXTENSION_NAME_PREFIX;
		String baseStencilSetPath                      = StencilSetExtensionGenerator.DEFAULT_BASE_STENCIL_SET_PATH;
		String baseStencilSet                          = StencilSetExtensionGenerator.DEFAULT_BASE_STENCIL_SET;
		String baseStencil                             = StencilSetExtensionGenerator.DEFAULT_BASE_STENCIL;
		List<String> stencilSetExtensionUrls           = new ArrayList<String>();
		String[] columnPropertyMapping                 = null;
		String[] csvHeader                             = null;
		List<Map<String,String>> stencilPropertyMatrix = new ArrayList<Map<String,String>>();
		String modelDescription                        = "The initial version of this model has been created by the Stencilset Extension Generator.";
		String additionalERDFContentForGeneratedModel  = "";
		String[] modelTags                             = null;
		
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if (isMultipart) {
		
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload();
	
			// Parse the request
			FileItemIterator iterator;
			try {
				iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
				    FileItemStream item = iterator.next();
				    String name = item.getFieldName();
				    InputStream stream = item.openStream();
				    if (item.isFormField()) {
				    	// ordinary form field
				    	String value = Streams.asString(stream);
				        //System.out.println("Form field " + name + " with value "
				        //    + value + " detected.");
				        if (name.equals("modelNamePrefix")) {
				        	modelNamePrefix = value;
				        } else if (name.equals("stencilSetExtensionNamePrefix")) {
				        	stencilSetExtensionNamePrefix = value;
				        } else if (name.equals("baseStencilSetPath")) {
				        	baseStencilSetPath = value;
				        } else if (name.equals("baseStencilSet")) {
				        	baseStencilSet = value;
				        } else if (name.equals("stencilSetExtension")) {
					        stencilSetExtensionUrls.add(value);
				        } else if (name.equals("baseStencil")) {
				        	baseStencil = value;
			        	} else if (name.equals("columnPropertyMapping")) {
				        	columnPropertyMapping = value.split(",");
				        } else if (name.equals("modelDescription")) {
				        	modelDescription = value;
				        } else if (name.equals("modelTags")) {
				        	modelTags = value.split(",");
				        } else if (name.equals("additionalERDFContentForGeneratedModel")) {
				        	additionalERDFContentForGeneratedModel = value;
				        }
				    } else {
				    	// file field
				        //System.out.println("File field " + name + " with file name "
				        //    + item.getName() + " detected.");
				        // Process the input stream
				        if (name.equals("csvFile")) {
				        	CsvMapReader csvFileReader = new CsvMapReader(
					        		new InputStreamReader(stream, "UTF-8"),
					        		CsvPreference.EXCEL_PREFERENCE
					        		);
					        csvHeader = csvFileReader.getCSVHeader(true);
							if (columnPropertyMapping != null || columnPropertyMapping.length > 0) {
								csvHeader = columnPropertyMapping;
							}
					        Map<String,String> row;
					        while ((row = csvFileReader.read(csvHeader)) != null) {
					        	stencilPropertyMatrix.add(row);
					        }
				        }
				    }
				}
	
				
				// generate stencil set
				Date creationDate = new Date(System.currentTimeMillis());
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
				String stencilSetExtensionName = stencilSetExtensionNamePrefix + " " + dateFormat.format(creationDate);

				stencilSetExtensionUrls.add(
						StencilSetExtensionGenerator.generateStencilSetExtension(
								stencilSetExtensionName,
								stencilPropertyMatrix,
								columnPropertyMapping,
								baseStencilSet,
								baseStencil
						)
				);
				
				// generate new model
				String modelName = modelNamePrefix + stencilSetExtensionName;
				String model = repository.generateERDF(
						UUID.randomUUID().toString(), 
						additionalERDFContentForGeneratedModel, 
						baseStencilSetPath, 
						baseStencilSet,
						stencilSetExtensionUrls,
						modelName,
						modelDescription
				);
				String modelUrl = baseUrl + repository.saveNewModel(
						model,
						modelName,
						modelDescription,
						baseStencilSet,
						baseStencilSetPath
				);

				// hack for reverse proxies:
				modelUrl = modelUrl.substring(modelUrl.lastIndexOf("http://"));

				// tag model
				if (modelTags != null) {
					for (String tagName : modelTags) {
						repository.addTag(modelUrl, tagName.trim());
					}
				}
				
				// redirect client to editor with that newly generated model
				response.setHeader("Location", modelUrl);
				response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// TODO Add some error message
		}
	}


	protected void println(String output) {
		try {
			response.getWriter().println(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
