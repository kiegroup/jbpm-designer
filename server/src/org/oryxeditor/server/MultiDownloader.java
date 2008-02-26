package org.oryxeditor.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Copyright (c) 2007 Martin Czuchra.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * This is a rewrite of the original download.php for usage in plugins that need
 * to run in the Java environment.
 * 
 * There is no distinction between GET and POST requests, since code relying on
 * download.php behaviour would perform a POST to just request the download of
 * one or more files from the server.
 * 
 * @author Martin Czuchra
 */
public class MultiDownloader extends HttpServlet {

    private static final long serialVersionUID = 544537395679618334L;

    /**
     * The GET request forwards to the performDownload method.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	this.performDownload(req, res);
    }

    /**
     * The POST request forwards to the performDownload method.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	this.performDownload(req, res);
    }

    private void prepareHeaders(HttpServletResponse res, String mimetype,
	    String name) {

	res.setHeader("Pragma", "public");
	res.setHeader("Expires", "0");
	res.setHeader("Cache-Control",
		"must-revalidate, post-check=0, pre-check=0");
	res.addHeader("Cache-Control", "private");
	res.setHeader("Content-Transfer-Encoding", "binary");
	res.setHeader("Content-Type", mimetype);
	res.setHeader("Content-Disposition", "attachment; filename=\"" + name
		+ "\"");
    }

    /**
     * Creates a zipfile consisting of filenames and contents provided in
     * filenames and contents parameters, respectively. The resulting zip file
     * is written into the whereTo stream.
     * 
     * This method requires that the length of the filenames and contents array
     * is equal and performs an assertion on this.
     * 
     * @param filenames
     *                the array of filenames to be written into the zip file.
     * @param contents
     *                the array of file contents to be written into the zipfile
     *                for the file that is at the same array position in the
     *                filenames array.
     * @param whereTo
     *                the stream the zipfile should be written to.
     * @throws IOException
     *                 when something goes wrong.
     */
    private void zip(String[] filenames, String[] contents, OutputStream whereTo)
	    throws IOException {

	assert (filenames.length == contents.length);

	// enclose the whereTo stream into a ZipOutputStream.
	ZipOutputStream out = new ZipOutputStream(whereTo);

	// iterate over all filenames.
	for (int i = 0; i < filenames.length; i++) {

	    // add a new entry for the current filename, write the current
	    // content and close the entry again.

	    out.putNextEntry(new ZipEntry(filenames[i]));
	    out.write(contents[i].getBytes());
	    out.closeEntry();
	}

	// Complete the ZIP file
	out.close();
    }

    /**
     * Performs the actual download independently of the original HTTP method of
     * the request. See in-method comments for details.
     * 
     * @param req
     *                The original HttpServletRequest object.
     * @param res
     *                The original HttpServletResponse object.
     * @throws IOException
     */
    private void performDownload(HttpServletRequest req, HttpServletResponse res)
	    throws IOException {

	// if there is a parameter named "download_0", then, by convention,
	// there are more than one files to be downloaded in a zip file.

	if (req.getParameter("download_0") != null) {

	    // traverse all files that should be downloaded and that are
	    // embedded in the request. the content of each file is stored in
	    // content, the filename in name.

	    int i = 0;
	    String content, name;
	    Vector<String> contents = new Vector<String>(), names = new Vector<String>();

	    while ((content = req.getParameter("download_" + i)) != null) {

		// get current name and increment file counter.

		name = req.getParameter("file_" + i++);

		// while collecting, write all current names and contents into
		// the appropriate Vector objects.

		contents.add(content);
		names.add(name);
	    }

	    // init two arrays the vectors will be cast into.

	    String[] contentsArray = new String[contents.size()];
	    String[] namesArray = new String[names.size()];

	    // prepare the response headers and send the requested file to the
	    // client. mimetype and filename originally were hardcoded into
	    // download.php, so they are here, since code may rely on this.

	    this.prepareHeaders(res, "application/zip", "result.zip");
	    this.zip(names.toArray(namesArray),
		    contents.toArray(contentsArray), res.getOutputStream());

	} else if (req.getParameter("download") != null) {

	    // branch for fetching of one file exactly. get the name and content
	    // of the file into the appropriate string variables.

	    String name = req.getParameter("file");
	    String content = req.getParameter("download");

	    // prepare headers, with empty mimetype (as download.php does), and
	    // send the content of the file back to the user.

	    this.prepareHeaders(res, "", name);
	    res.getWriter().write(content);

	} else {

	    // when none of the above rules applies, inform the user that
	    // nothing remains to be done.

	    // TODO Find appropriate HTTP message here, no code relies on this.
	    res.getWriter().println("There is nothing to be downloaded.");
	}
    }
}
