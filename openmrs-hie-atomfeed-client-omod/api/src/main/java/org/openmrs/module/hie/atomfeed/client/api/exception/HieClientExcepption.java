/**
 * Portions Copyright 2015-2018 Mohawk College of Applied Arts and Technology
 * Portions Copyright (c) 2014-2020 Fyfe Software Inc.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 */
package org.openmrs.module.hie.atomfeed.client.api.exception;

import ca.uhn.hl7v2.model.Message;

/**
 * Health information exchange communication base exception
 * 
 * @author Justin
 */

public class HieClientExcepption extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Backing field for message
	private Message m_message;
	
	/**
	 * HIE Exception
	 */
	public HieClientExcepption() {
		
	}
	
	/**
	 * Creates a new HIE exception
	 */
	public HieClientExcepption(Exception cause) {
		super(cause);
	}
	
	/**
	 * Create health information exception
	 * 
	 * @param string
	 */
	public HieClientExcepption(String message) {
		super(message);
	}
	
	/**
	 * Create health information exception
	 * 
	 * @param string
	 */
	public HieClientExcepption(String message, Message response) {
		super(message);
		this.m_message = response;
	}
	
	/**
	 * Create HIE Exception with cause
	 */
	public HieClientExcepption(String message, Exception e) {
		super(message, e);
	}
	
	/**
	 * Get the response message
	 */
	public Message getResponseMessage() {
		return this.m_message;
	}
}
