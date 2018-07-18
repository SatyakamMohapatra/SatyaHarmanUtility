/*
* Classname: SearchMDX.java
*
* (c) Copyright Information Resources, Inc.
* ALL RIGHTS RESERVED
* This UNPUBLISHED PROPRIETARY software is subject to the full copyright
* notice in the COPYRIGHT file in this directory.
*/
package util.id;

/**
 * @author prvad
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import java.rmi.dgc.VMID;

public class Guid {

	private VMID guid;

	/**
	 * @see Object#Object()
	 */
	public Guid() {
		// No public constructor, although this can be made public if required
		super();
		guid = new VMID();
	}
	
	/**
	 * Method newGuid.
	 * @return Guid
	 */
	public static Guid newGuid() {
		// A wrapper for VMID
		// This is thread safe, no need to synchronize it.
		return new Guid();
	}

	/**
	 * @see Object#toString()
	 */
	// Implement other methods if needed
	public String toString() {
		return guid.toString();
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if( obj instanceof Guid ) {
			return guid.equals(((Guid)obj).guid);
		} else {
			return false;
		}
	}
	
	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return guid.hashCode();
	}
}
