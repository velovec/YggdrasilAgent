/**
 * Copyright (c) Daniel Doubrovkine, 2010
 * dblock[at]dblock[dot]org
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://ru.linachan.system_info.oshi.codeplex.com/license
 */
package ru.linachan.system_info.hardware;

/**
 * A hardware abstraction layer.
 * @author dblock[at]dblock[dot]org
 */
public interface HardwareAbstractionLayer {

	/**
	 * Get CPUs.
	 * @return
	 *  An array of Processor objects.
	 */
	Processor[] getProcessors();
	
	/**
	 * Get Memory information.
	 * @return
	 *  A memory object.
	 */
	Memory getMemory();

}
