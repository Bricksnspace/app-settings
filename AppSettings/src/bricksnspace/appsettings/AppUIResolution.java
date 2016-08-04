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

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Static utilities for UI resolution versus screen size handling
 * 
 * @author Mario Pascucci
 *
 */
public class AppUIResolution {

	private static int xLimit = 1600;
	private static int yLimit = 1250;
	
	private static String hiresFolder = "imghires/";
	private static String stdresFolder = "images/";
	
	
	
	
	/**
	 * User defined resource folder for icons and images
	 * @param std standard resolution folder
	 * @param hires high-resolution folder
	 */
	public static void setFolders(String std, String hires) {
		
		if (std == null || std == "" || hires == null || hires == "")
			throw new IllegalArgumentException("Resource folder(s) cannot be null or empty string");
		stdresFolder = std;
		hiresFolder = hires;
	}
	
	
	/**
	 * User defined limits for std to hires switching
	 * @param x display width limit in pixel
	 * @param y display height limit in pixel
	 */
	public static void setLimits(int x, int y) {
		
		xLimit = x;
		yLimit = y;
	}
	
	
	
	/**
	 * Returns resource folder according to limits defined for std or hi-res
	 * @return a string containing resource folder
	 */
	public static String getImgDir() {
		
		if (isHiRes()) {
			return hiresFolder;
		}
		else {
			return stdresFolder;
		}
	}
	
	
	/**
	 * Checking for display size against user-defined limits
	 * @return true if display is bigger than defined limits
	 */
	public static boolean isHiRes() {
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if (d.width > xLimit && d.height > yLimit) {
			return true;
		}
		return false;
	}
	
	

	
}
