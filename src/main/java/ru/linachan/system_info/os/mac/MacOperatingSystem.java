/**
 * 
 */
package ru.linachan.system_info.os.mac;

import ru.linachan.system_info.os.OperatingSystem;
import ru.linachan.system_info.os.OperatingSystemVersion;
import ru.linachan.system_info.os.mac.local.OSVersionInfoEx;
import ru.linachan.system_info.util.ExecutingCommand;

/**
 * @author alessandro[at]perucchi[dot]org
 */

public class MacOperatingSystem implements OperatingSystem {
	private String _family;

	private OperatingSystemVersion _version = null;

	public OperatingSystemVersion getVersion() {
		if (_version == null) {
			_version = new OSVersionInfoEx();
		}
		return _version;
	}

	public String getFamily() {
		if (_family == null)
			_family = ExecutingCommand.getFirstAnswer("sw_vers -productName");

		return _family;
	}

	public String getManufacturer() {
		return "Apple";
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
