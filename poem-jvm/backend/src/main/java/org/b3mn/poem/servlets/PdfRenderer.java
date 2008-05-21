package org.b3mn.poem.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.b3mn.poem.Identity;

public class PdfRenderer {

    private static final long serialVersionUID = 8526319871562210085L;

    public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object, String hostname) {
	  	try {
	    	InputStream in = new ByteArrayInputStream(object.read().getSvg().getBytes());
	    	res.setStatus(200);
	    	res.setContentType("application/pdf");
	    	makePDF(in, res.getOutputStream());

	    } catch (Exception e) {
	      	// TODO Auto-generated catch block
	      	e.printStackTrace();
	    }
    }

    private void makePDF(InputStream in, OutputStream out) throws TranscoderException, IOException {

	  	PDFTranscoder transcoder = new PDFTranscoder();
	  	try {
	    	TranscoderInput input = new TranscoderInput(in);

	    	// Setup output
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
