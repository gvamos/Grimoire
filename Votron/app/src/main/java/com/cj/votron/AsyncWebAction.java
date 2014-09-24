/**
 * 
 */
package com.cj.votron;

import android.app.Activity;

/**
 * @author gvamos
 *
 * Interface for asynch web requests
 */
public interface AsyncWebAction {
	
	public void exec();
	public void followUp();
	public String getStatus();
	public String getResult();
}
