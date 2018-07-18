/*
 * @(#)IdGenerator.java      Sep 08 2004
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
 * The parent interface for generating unique IDs for entities in
 * RPM application. There can be multiple implementations for 
 * generating unique IDs in the application. Each implementer
 * implements their own algorithm of generating the unique ID.
 */

public interface IdGenerator {
	/*
	 * @return Serializable returns a unique Id
	 */
	public Serializable getId() ;
}
