/**
 * 
 */
package ru.linachan.system_info.os.mac;

import java.util.ArrayList;
import java.util.List;

import ru.linachan.system_info.hardware.HardwareAbstractionLayer;
import ru.linachan.system_info.hardware.Memory;
import ru.linachan.system_info.hardware.Processor;
import ru.linachan.system_info.os.mac.local.CentralProcessor;
import ru.linachan.system_info.os.mac.local.GlobalMemory;
import ru.linachan.system_info.util.ExecutingCommand;

/**
 * @author alessandro[at]perucchi[dot]org
 */

public class MacHardwareAbstractionLayer implements HardwareAbstractionLayer {

	private Processor[] _processors;
	private Memory _memory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.linachan.system_info.hardware.HardwareAbstractionLayer#getProcessors()
	 */
	public Processor[] getProcessors() {
		if (_processors == null) {
			List<Processor> processors = new ArrayList<Processor>();
			int nbCPU = new Integer(
					ExecutingCommand.getFirstAnswer("sysctl -n hw.logicalcpu"));
			for (int i = 0; i < nbCPU; i++)
				processors.add(new CentralProcessor());
			
			_processors = processors.toArray(new Processor[0]);
		}
		return _processors;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.linachan.system_info.hardware.HardwareAbstractionLayer#getMemory()
	 */
	public Memory getMemory() {
		if (_memory == null) {
			_memory = new GlobalMemory();
		}
		return _memory;
	}

}
