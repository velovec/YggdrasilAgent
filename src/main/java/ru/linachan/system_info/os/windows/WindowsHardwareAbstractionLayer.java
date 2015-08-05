package ru.linachan.system_info.os.windows;

import java.util.ArrayList;
import java.util.List;

import ru.linachan.system_info.hardware.HardwareAbstractionLayer;
import ru.linachan.system_info.hardware.Memory;
import ru.linachan.system_info.hardware.Processor;
import ru.linachan.system_info.os.windows.nt.CentralProcessor;
import ru.linachan.system_info.os.windows.nt.GlobalMemory;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class WindowsHardwareAbstractionLayer implements HardwareAbstractionLayer {

	private Processor[] _processors = null;
	private Memory _memory = null;
	
	public Memory getMemory() {
		if (_memory == null) {
			_memory = new GlobalMemory();
		}
		return _memory;
	}

	public Processor[] getProcessors() {
		
		if (_processors == null) {
			final String cpuRegistryRoot = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor";
			List<Processor> processors = new ArrayList<Processor>();
			String[] processorIds = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryRoot);
			for(String processorId : processorIds) {
				String cpuRegistryPath = cpuRegistryRoot + "\\" + processorId; 
				CentralProcessor cpu = new CentralProcessor();
				cpu.setIdentifier(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, 
						cpuRegistryPath, "Identifier"));
				cpu.setName(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, 
						cpuRegistryPath, "ProcessorNameString"));
				cpu.setVendor(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, 
						cpuRegistryPath, "VendorIdentifier"));
				processors.add(cpu);
			}			
			_processors = processors.toArray(new Processor[0]);
		}
		
		return _processors;
	}

}
