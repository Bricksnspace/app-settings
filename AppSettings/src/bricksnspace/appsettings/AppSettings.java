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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 * 
 * TODO: documentazione nei commenti da fare
 * TODO: evidenziare i default
 * @author Mario Pascucci
 *
 */
public class AppSettings {
	private static boolean configured = false;
	public static final int STRING = 1;
	public static final int BOOLEAN = 2;
	public static final int INTEGER = 3;
	public static final int FLOAT = 4;
	public static final int FILE = 5;
	public static final int FOLDER = 6;
	public static final int MAXTYPES = 6;
	
	private static Preferences prefs;
	private static boolean stored = false;
	private static Map<String,String> displayName = new HashMap<String,String>();
	private static Map<String,Integer> types = new HashMap<String,Integer>();
	private static List<String> names = new ArrayList<String>(); 
	
	public static String prefsFile = null;
	


	/**
	 * @return the prefsFile
	 */
	public static String getPrefsFile() {
		return prefsFile;
	}



	/**
	 * @param prefsFile the prefsFile to set
	 */
	public static void setPrefsFile(String prefsFile) {
		AppSettings.prefsFile = prefsFile;
	}



	public static boolean openPreferences(Object app) {		
		
		if (prefsFile == null) {
			if (app == null) {
				prefsFile = "app.prefs";
			}
			else {
				prefsFile = app.getClass().getSimpleName()+".prefs";
			}
		}
		File pf = new File(prefsFile);
		if (pf.isFile() && pf.canRead()) {
			configured = true;
		}
		else {
			configured = false;
		}
		prefs = Preferences.userNodeForPackage(app.getClass());
		if (pf.canRead() && pf.isFile()) {
			try {
				Preferences.importPreferences(new FileInputStream(pf));
				stored = true;
			} catch (FileNotFoundException e) {
				try {
					prefs.clear();
				} catch (BackingStoreException e1) {
					// nothing to do
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidPreferencesFormatException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				prefs.clear();
			} catch (BackingStoreException e1) {
				// nothing to do
				e1.printStackTrace();
			}
		}
		// put here your preferences 
		names.clear();
		displayName.clear();
		types.clear();
		
		return isConfigured();
	}
	
	
	
	public static boolean isConfigured() {
		
		return configured;
	}


	
	public static List<String> getPrefsList() {
		
		return names;
	}
	
	
	
	public static String getDescr(String name) {
		
		return displayName.get(name);
	}
	
	
	
	public static boolean isStored() {

		return stored;
	}
	
	
	
	public static void addPref(String name, String dispName, int type) {
		
		if (name == null || name == "")
			throw new IllegalArgumentException("Invalid name for Preference");
		if (dispName == null || dispName == "")
			throw new IllegalArgumentException("Invalid description for Preference");
		if (type <= 0 || type > MAXTYPES)
			throw new IllegalArgumentException("Invalid type for Preference");
		names.add(name);
		displayName.put(name, dispName);
		types.put(name, type);
	}

	
	
	public static int getType(String name) {
		
		return types.get(name);
	}
	
	
	public static void savePreferences() throws IOException, BackingStoreException {
		
		File spf = new File(prefsFile);
		prefs.exportSubtree(new FileOutputStream(spf));
	}
	
	
	
	public static void put(String key, String value) {
		prefs.put(key, value);
	}
	
	
	public static void putBool(String key, boolean value) {
		prefs.putBoolean(key, value);
	}
	
	
	public static void putInt(String key, int value) {
		prefs.putInt(key, value);
	}
	
	
	public static void putFloat(String key, float value) {
		prefs.putFloat(key, value);
	}
	
	
	public static String get(String key) {
		return prefs.get(key, "");
	}
	
	public static boolean getBool(String key) {
		return prefs.getBoolean(key, false);
	}

	
	public static int getInt(String key) {
		return prefs.getInt(key, 0);
	}
	
	
	public static float getFloat(String key) {
		return prefs.getFloat(key, 0f);
	}
	

}
