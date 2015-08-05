/**
 * Copyright (c) Alessandro Perucchi, 2014
 * alessandro[at]perucchi[dot]org
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://ru.linachan.system_info.oshi.codeplex.com/license
 */
package ru.linachan.system_info.os.linux;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import ru.linachan.system_info.os.OperatingSystem;
import ru.linachan.system_info.os.OperatingSystemVersion;
import ru.linachan.system_info.os.linux.proc.OSVersionInfoEx;

/**
 * Linux is a family of free operating systems most commonly used on personal
 * computers.
 *
 * @author alessandro[at]perucchi[dot]org
 */
public class LinuxOperatingSystem implements OperatingSystem {

	private OperatingSystemVersion _version = null;
	private String _family = null;

	public String getFamily() {
		if (_family == null) {
			Scanner in;
			try {
				in = new Scanner(new FileReader("/etc/os-release"));
			} catch (FileNotFoundException e) {
				return "";
			}
			in.useDelimiter("\n");
			while (in.hasNext()) {
				String[] splittedLine = in.next().split("=");
				if (splittedLine[0].equals("NAME")) {
					// remove beginning and ending '"' characters, etc from
					// NAME="Ubuntu"
					_family = splittedLine[1].replaceAll("^\"|\"$", "");
					break;
				}
			}
			in.close();
		}
		return _family;
	}

	public String getManufacturer() {
		return "GNU/Linux";
	}

	public OperatingSystemVersion getVersion() {
		if (_version == null) {
			_version = new OSVersionInfoEx();
		}
		return _version;
	}

	@Override
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
