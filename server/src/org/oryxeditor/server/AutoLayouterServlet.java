package org.oryxeditor.server;

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
class StreamReadThread extends Thread
{
	public static final boolean debug = false;
	
    InputStream is;
    String content;
    Boolean stop;
    
    StreamReadThread(InputStream is)
    {
        this.is = is;
        this.content = "";
        this.stop = false;        
        
    }
    
    public void run()
    {
        try
        {
        	if (debug){System.out.println("Auto-Layouter: Reader Thread started");}
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            if (debug){ System.out.println("Auto-Layouter: Reader Thread yields");}
            yield();
            if (debug){ System.out.println("Auto-Layouter: Reader Thread starts reading");}
            while(!stop){
            	if ( (line = br.readLine()) != null){
            		content += line;
            	}
            	else{
            		if (debug){System.out.println("Auto-Layouter: Reader Thread sleeps");}
            		sleep(200);
            	}
            	
            }
                //System.out.println( line);    
            } catch (Exception e)
              {
                e.printStackTrace();
                content += "\nException: " + e.toString() + " occured!";
              }
            if (debug){System.out.println("Auto-Layouter: Reader Thread stopped");}
    }
    
    public void stop_reading(){
    	stop = true;
    	if (debug){System.out.println("Auto-Layouter: Stopping Reader Thread");}
    }
}
public class AutoLayouterServlet extends HttpServlet {

	public static final boolean debug = false;
	private static final long serialVersionUID = -1255777265795121521L;

	protected void wait_for_thread(StreamReadThread thread){
		thread.stop_reading();
		if (debug){System.out.println("Auto-Layouter: Waiting for reader thread");}
		try {
		thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String rdf = req.getParameter("rdf");
		String basefilename = String.valueOf(System.currentTimeMillis());
		
		String tmpRdfFile = this.getServletContext().getRealPath("/")
				+ "tmp" + File.separator + basefilename
				+ ".rdf";
		BufferedWriter out = new BufferedWriter(new FileWriter(tmpRdfFile));
		out.write(rdf);
		out.close();
		if (debug){System.out.println("Auto-Layouter: Saved RDF to " + tmpRdfFile);}
		
		String layouter_path = this.getServletContext().getRealPath("/")
		+ "Plugins" + File.separator + "AutoLayouter" + File.separator + "src"+ File.separator + "startlayout.rb";
		ProcessBuilder builder = new ProcessBuilder("ruby", "\"" + layouter_path + "\"", "\"" + tmpRdfFile + "\"");
		builder.redirectErrorStream(true);
		
		if (debug){System.out.println("Auto-Layouter: Calling ruby with: ruby \"" + layouter_path + "\" \"" + tmpRdfFile + "\"");}
		Process p = builder.start();
		if (debug){System.out.println("Auto-Layouter: Called Ruby, Starting ReaderThread");}
		
		StreamReadThread reader = new StreamReadThread(p.getInputStream());
		reader.start();
		res.setContentType("text/html");
		try {
			if (debug){System.out.println("Auto-Layouter: Waiting for Ruby to return");}
			if (p.waitFor() != 0){
				if (debug){System.out.println("Auto-Layouter: Ruby returned an error");}
				res.getWriter().println("{'error':'Layouter returned an error'}");
				wait_for_thread(reader);
				System.err.println(reader.content);
				return;
			}
			if (debug){System.out.println("Auto-Layouter: Ruby returned");}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Wait interrupted");
		}
		wait_for_thread(reader);
		if (debug){System.out.println("Auto-Layouter: Sending response");}
		res.getWriter().println(reader.content);
	}
}
