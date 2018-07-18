/*
 * @(#)GUIDGenerator.java      Sep 08 2004
 *
 * Copyright (c) 2002 Symphony Services. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Symphony Services, ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Symphony Services.
 *
 * SYMPHONY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SYMPHONY SERVICES SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version			:	1.0
 * @author			:	sdas
 * @reviewed by		:
 * @date reviewed	:
 */

package util.id;

import java.io.Serializable;


/**
 * GUID generator class that generates Globally Unique Identifiers.
 * This internally uses the Guid generator from SSI such that unique
 * IDs get generated from a single place. SSI Guid generator internally
 * uses java.rmi.DGC.VMID class that generates unique ids across JVMs.
 * 
 * This class also wraps SSI specifc code. So any change in SSI API
 * would reflect only in this class and not other classes that use 
 * IdGenerator interface.
 * @author sdas
 * @since 4.2
 */

public class GUIDGenerator implements IdGenerator{
	
	/* (non-Javadoc)
	 * @see com.symphonyrpm.applayer.common.util.IdGenerator#getId()
	 */
	public Serializable getId() {
		return Guid.newGuid().toString() ;
	}

	public static void main(String[] args) {
		IdGenerator generator = new GUIDGenerator() ;
		long startTime = System.currentTimeMillis() ;
		for (int i=0; i<10; i++)
		{
			String guid = (String) generator.getId() ;
		}
		long diff = System.currentTimeMillis() - startTime ;
	}

}
