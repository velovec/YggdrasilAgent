/**
 * Copyright (c) Daniel Doubrovkine, 2010
 * dblock[at]dblock[dot]org
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://ru.linachan.system_info.oshi.codeplex.com/license
 */
package ru.linachan.system_info;

import ru.linachan.system_info.hardware.HardwareAbstractionLayer;
import ru.linachan.system_info.os.OperatingSystem;
import ru.linachan.system_info.os.linux.LinuxHardwareAbstractionLayer;
import ru.linachan.system_info.os.linux.LinuxOperatingSystem;
import ru.linachan.system_info.os.mac.MacHardwareAbstractionLayer;
import ru.linachan.system_info.os.mac.MacOperatingSystem;
import ru.linachan.system_info.os.windows.WindowsHardwareAbstractionLayer;
import ru.linachan.system_info.os.windows.WindowsOperatingSystem;

import com.sun.jna.Platform;

public class SystemInfo {
	private OperatingSystem _os = null;
	private HardwareAbstractionLayer _hardware = null;
	private PlatformEnum currentPlatformEnum;

	{
		if (Platform.isWindows())
			currentPlatformEnum = PlatformEnum.WINDOWS;
		else if (Platform.isLinux())
			currentPlatformEnum = PlatformEnum.LINUX;
		else if (Platform.isMac())
			currentPlatformEnum = PlatformEnum.MACOSX;
		else
			currentPlatformEnum = PlatformEnum.UNKNOWN;
	}

	public OperatingSystem getOperatingSystem() {
		if (_os == null) {
			switch (currentPlatformEnum) {

			case WINDOWS:
				_os = new WindowsOperatingSystem();
				break;
			case LINUX:
				_os = new LinuxOperatingSystem();
				break;
			case MACOSX:
				_os = new MacOperatingSystem();
				break;
			default:
				throw new RuntimeException("Operating system not supported");
			}
		}
		return _os;
	}

	public HardwareAbstractionLayer getHardware() {
		if (_hardware == null) {
			switch (currentPlatformEnum) {

			case WINDOWS:
				_hardware = new WindowsHardwareAbstractionLayer();
				break;
			case LINUX:
				_hardware = new LinuxHardwareAbstractionLayer();
				break;
			case MACOSX:
				_hardware = new MacHardwareAbstractionLayer();
				break;
			default:
				throw new RuntimeException("Operating system not supported");
			}
		}
		return _hardware;
	}
}