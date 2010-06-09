package de.hpi.yawl;

/**
 * Copyright (c) 2010, Armin Zamani
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
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Date;

public class YTimer implements FileWritingForYAWL {

	public enum Trigger {
		OnExecuting, OnEnabled
	}
	
	private Trigger trigger;
	private String duration = "";
	private Date expiry;
	
	/**
	 * constructor of class 
	 */
	public YTimer(Trigger trigger, String duration, Date expiry) {
		super();
		setTrigger(trigger);
		setDuration(duration);
		setExpiry(expiry);
	}
	
	public Trigger getTrigger() {
		return trigger;
	}
	
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public Date getExpiry() {
		return expiry;
	}
	
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	
	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		s += "\t\t\t\t\t<timer>\n";
		s += String.format("\t\t\t\t\t\t<trigger>%s</trigger>\n", getTrigger().toString());
		if(duration.isEmpty())
			s += String.format("\t\t\t\t\t\t<expiry>%s</expiry>\n", getExpiry().getTime());
		else
			s += String.format("\t\t\t\t\t\t<duration>%s</duration>\n", getDuration());
		
		s += "\t\t\t\t\t</timer>\n";
		
		return s;
	}
}
