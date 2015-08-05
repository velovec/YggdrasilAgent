/**
 * Copyright (c) Daniel Doubrovkine, 2010
 * dblock[at]dblock[dot]org
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://ru.linachan.system_info.oshi.codeplex.com/license
 */
package ru.linachan.system_info.os.windows;

import ru.linachan.system_info.os.OperatingSystem;
import ru.linachan.system_info.os.OperatingSystemVersion;
import ru.linachan.system_info.os.windows.nt.OSVersionInfoEx;

/**
 * Microsoft Windows is a family of proprietary operating systems most commonly used on
 * personal computers.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsOperatingSystem implements OperatingSystem {
	
	private OperatingSystemVersion _version = null;
	
	public OperatingSystemVersion getVersion() {
		if (_version == null) {
			_version = new OSVersionInfoEx();
		}		
		return _version;
	}

	public String getFamily() {
		return "Windows";
	}

	public String getManufacturer() {
		return "Microsoft";
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getManufacturer());
		sb.append(" ");
		sb.append(getFamily());
		sb.append(" ");
		sb.append(getVersion().toString());
		return sb.toString();
	}
}
