package org.jbpm.designer.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//Thread for continuously reading a process' InputStream
//needed because java processes can deadlock, when InputStream exceeds a certain threshold 
class StreamReadThread extends Thread {

    InputStream is;
    String content;
    Boolean stop;

    StreamReadThread(InputStream is) {
	this.is = is;
	this.content = "";
	this.stop = false;

    }

    public void run() {
	try {
	    if (AutoLayouterServlet.DEBUG) {
		System.out.println("Auto-Layouter: Reader Thread started");
	    }
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String line = null;
	    if (AutoLayouterServlet.DEBUG) {
		System.out.println("Auto-Layouter: Reader Thread yields");
	    }
	    yield();
	    if (AutoLayouterServlet.DEBUG) {
		System.out
			.println("Auto-Layouter: Reader Thread starts reading");
	    }
	    while (!stop) {
		if ((line = br.readLine()) != null) {
		    content += line;
		} else {
		    if (AutoLayouterServlet.DEBUG) {
			System.out
				.println("Auto-Layouter: Reader Thread sleeps");
		    }
		    sleep(200);
		}

	    }
	    // System.out.println( line);
	} catch (Exception e) {
	    e.printStackTrace();
	    content += "\nException: " + e.toString() + " occured!";
	}
	if (AutoLayouterServlet.DEBUG) {
	    System.out.println("Auto-Layouter: Reader Thread stopped");
	}
    }

    public void stop_reading() {
	stop = true;
	if (AutoLayouterServlet.DEBUG) {
	    System.out.println("Auto-Layouter: Stopping Reader Thread");
	}
    }
}

public class AutoLayouterServlet extends HttpServlet {

    public static final boolean DEBUG = false;
    private static final long serialVersionUID = -1255777265795121521L;

    protected void wait_for_thread(StreamReadThread thread) {
	thread.stop_reading();
	if (DEBUG) {
	    System.out.println("Auto-Layouter: Waiting for reader thread");
	}
	try {
	    thread.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	if (false && DEBUG) {

	    // execute 'which ruby' in the context of the servlet container.
	    try {

		// start a process for 'which ruby' and wait for it.
		Process which = Runtime.getRuntime().exec("which ruby");
		which.waitFor();

		// get the output in a buffered reader.
		BufferedReader whichIn = new BufferedReader(
			new InputStreamReader(which.getInputStream(), "UTf-8"));

		// output the debug information.
		System.out.print("ruby location: ");
		System.out.println(whichIn.readLine());

	    } catch (InterruptedException e1) {

		// if something goes wrong, tell us.
		e1.printStackTrace();
	    }
	}

	// get rdf and timestamp for the temporary file.
	String rdf = req.getParameter("rdf");
	String basefilename = String.valueOf(System.currentTimeMillis());

	// create a new temporary file and store the rdf.
	String tmpRdfFile = this.getServletContext().getRealPath("/") + "tmp"
		+ File.separator + basefilename + ".rdf";
	BufferedWriter out = new BufferedWriter(new FileWriter(tmpRdfFile));
	out.write(rdf);
	out.close();

	// get the path to the layouter.
	String layouter_path = this.getServletContext().getRealPath("/")
		+ "Plugins" + File.separator + "AutoLayouter" + File.separator
		+ "src" + File.separator + "startlayout.rb";

	if (DEBUG) {

	    System.out.println("Auto-Layouter: Saved RDF to " + tmpRdfFile);

	    // try to find the layouter.
	    try {

		// start a process for 'ls %layouter_path%' and wait for it.
		Process ls = Runtime.getRuntime().exec("ls " + layouter_path);
		ls.waitFor();

		// get the output in a buffered reader.
		BufferedReader lsIn = new BufferedReader(new InputStreamReader(
			ls.getInputStream(), "UTF-8"));

		// output the debug information.
		System.out.print("ls on layouter: ");
		System.out.println(lsIn.readLine());

	    } catch (InterruptedException e1) {

		// if something goes wrong, tell us.
		e1.printStackTrace();
	    }
	}

	ProcessBuilder builder = new ProcessBuilder("ruby", layouter_path,
		tmpRdfFile);
	builder.redirectErrorStream(true);
	;

	if (DEBUG) {
	    System.out.println("Auto-Layouter: Calling ruby with: "
		    + builder.command());
	}
	Process p = builder.start();

	if (DEBUG) {
	    System.out
		    .println("Auto-Layouter: Called Ruby, Starting ReaderThread");
	}

	StreamReadThread reader = new StreamReadThread(p.getInputStream());
	reader.start();
	res.setContentType("text/html");
	try {
	    if (DEBUG) {
		System.out.println("Auto-Layouter: Waiting for Ruby to return");
	    }
	    if (p.waitFor() != 0) {
		if (DEBUG) {
		    System.out.println("Auto-Layouter: Ruby returned an error");
		}
		res.getWriter().println(
			"{'error':'Layouter returned an error:\n"
				+ reader.content + "'}");
		wait_for_thread(reader);
		System.err.println(reader.content);
		return;
	    }
	    if (DEBUG) {
		System.out.println("Auto-Layouter: Ruby returned");
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    System.out.println("Wait interrupted");
	}
	wait_for_thread(reader);
	if (DEBUG) {
	    System.out.println("Auto-Layouter: Sending response");
	}
	res.getWriter().println(reader.content);
    }
}
