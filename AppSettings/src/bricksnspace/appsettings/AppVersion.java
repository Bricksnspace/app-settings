/**
	Copyright 2016 Mario Pascucci <mpascucci@gmail.com>
	This file is part of AppSettings

	AppSettings is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	AppSettings is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with AppSettings.  If not, see <http://www.gnu.org/licenses/>.
 
 */

package bricksnspace.appsettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * Static utilities for app versions handling
 * 
 * @author Mario Pascucci
 *
 */
public class AppVersion {
	
	public static int MAJOR = 0;
	public static int MINOR = 0;
	public static int PATCH = 0;
	

	
	public static int myVersionMajor() {
		
		return MAJOR;
	}
	
	
	
	public static int myVersionMinor() {
		
		return MINOR;
	}
	
	
	
	public static int myVersionPatch() {
		
		return PATCH;
	}
	
	/**
	 * combine version in a single string.
	 * @return combined version as major.minor.patch
	 */
	public static String myVersion() {
		
		return MAJOR + "." + MINOR + "." + PATCH;
	}
	
	
	/**
	 * Parses a standard version string to integer values. 
	 * Accepts up to three version levels, major, minor and patchlevel, assigning zero to omitted values. 
	 * "1.4" -> "1.4.0"
	 * DO NOT ACCEPT non-numeric values, like "1.0.3a", evaluated as 0. -> "1.0.3a" == "1.0.0"
	 * @param version standard version string (major.minor.patchlevel) es. "0.2.12" "2.3.0"
	 * @return integer array with versions values (three integers)
	 */
	public static int[] parseVersionString(String version) {

		int[] ver = {0,0,0};
		
		String[] s = version.split("\\.");
		if (s.length >= 1) {
			try {
				ver[0] = Integer.parseInt(s[0]);
			}
			catch (NumberFormatException e) {
				// unable to read major-> 0
			}
		}
		if (s.length >= 2) {
			try {
				ver[1] = Integer.parseInt(s[1]);
			}
			catch (NumberFormatException e) {
				// unable to read minor-> 0
			}
		}
		if (s.length >= 3) {
			try {
				ver[2] = Integer.parseInt(s[2]);
			}
			catch (NumberFormatException e) {
				// unable to read patch-> 0
			}
		}
		return ver;
	}
	
	
	/**
	 * Sets current version
	 * @param version string containing a standard version signature
	 */
	public static void setMyVersion(String version) {
		
		int[] v = parseVersionString(version);
		MAJOR = v[0];
		MINOR = v[1];
		PATCH = v[2];
	}
	
	
	/**
	 * Compares given version to current stored version
	 * @param ver a standard string version
	 * @return 0 if equals, 1 if given version is newer that current, -1 if older
	 */
	public static int compareVersion(String ver) {
		
		int v[] = parseVersionString(ver);
		
		if (v[0] > MAJOR)
			return 1;
		else if (v[0] < MAJOR)
			return -1;
		// major version is equal, checks minor
		if (v[1] > MINOR)
			return 1;
		else if (v[1] < MINOR)
			return -1;
		// major and minor are equal, check patchlevel
		if (v[2] > PATCH)
			return 1;
		else if (v[2] < PATCH)
			return -1;
		// version are equal
		return 0;
	}
	
	
	
	/**
	 * gets a version from an URL
	 * 
	 * version is a plain text file with a string representing a version triplet
	 * 
	 * @param updateUrl
	 * @return version read from url
	 */
	public static String getUrlVersion(String updateUrl) {

		URL updUrl;
		try {
			updUrl = new URL(updateUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return "";
		}
		URLConnection connect = null;
		InputStream urlStream = null;
		try {
			HttpURLConnection.setFollowRedirects(false);
			connect = updUrl.openConnection();
			urlStream = connect.getInputStream();
		}
		catch (IOException ex) {
			// probably no Internet connection is available...
			return "";
		}
		if (connect.getContentLength() == 0) {
			// no updates
			return "";
		}
		InputStreamReader isr = new InputStreamReader(urlStream);
		String readVer = "";
		try {
			int c;
			do {
				c = isr.read();
				if (c != -1) {
					readVer += (char)c;
				}
			} while (c != -1);
			readVer = readVer.trim();
			return readVer;
		}
		catch (IOException e) {
			return readVer;
		}
	}
	
	
	/**
	 * gets version from a zipfile
	 * Version number must be inside a text file called VERSION as a version string triplet
	 * 
	 * @param zf
	 * @return version from zipfile
	 */
	public static String getZipVersion(String zf) {
		
		ZipFile z;
		try {
			z = new ZipFile(zf);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		ZipEntry ze = z.getEntry("VERSION");
		if (ze == null) {
			try {
				z.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}
		String readVer;
		try {
			InputStream is = z.getInputStream(ze);
			readVer = "";
			int c;
			do {
				c = is.read();
				if (c != -1) {
					readVer += (char)c;
				}
			} while (c != -1);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		try {
			z.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		readVer = readVer.trim();
		return readVer;
	}

	
	

}
