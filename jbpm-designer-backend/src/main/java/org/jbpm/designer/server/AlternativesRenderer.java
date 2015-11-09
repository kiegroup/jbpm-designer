/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

public class AlternativesRenderer extends HttpServlet {

    private static final long serialVersionUID = 8526319871562210085L;

    private String baseFilename;
    private String inFile;
    private String outFile;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) {

	String data = req.getParameter("data");

	try {
		data = new String(data.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	String tmpPath = this.getServletContext().getRealPath("/")
	+ File.separator + "tmp" + File.separator;
	
	//create tmp folder
	File tmpFolder = new File(tmpPath);
	if(!tmpFolder.exists()) {
		tmpFolder.mkdirs();
	}
	
	this.baseFilename = String.valueOf(System.currentTimeMillis());
	this.inFile = tmpPath + this.baseFilename + ".svg";
	this.outFile = tmpPath + this.baseFilename + ".pdf";

	try {
		String contextPath = req.getContextPath();
	    // store model in temporary svg file.
	    BufferedWriter out = new BufferedWriter(new FileWriter(inFile));
	    out.write(data);
	    out.close();
	    makePDF();
	    res.getOutputStream().print(contextPath + "/tmp/" + this.baseFilename + ".pdf");

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    protected void makePDF() throws TranscoderException, IOException {

		PDFTranscoder transcoder = new PDFTranscoder();
	
		InputStream in = new java.io.FileInputStream(inFile);
	
		try {
		    TranscoderInput input = new TranscoderInput(in);
	
		    // Setup output
		    OutputStream out = new java.io.FileOutputStream(outFile);
		    out = new java.io.BufferedOutputStream(out);
		    try {
				TranscoderOutput output = new TranscoderOutput(out);
		
				// Do the transformation
				transcoder.transcode(input, output);
			
		    } finally {
		    	out.close();
		    }
		} finally {
		    in.close();
		}
    }

}
