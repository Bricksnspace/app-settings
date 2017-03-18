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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 * Java application settings/preferences helper
 * 
 * Uses Java Preferences from java.util
 * Can save and load preferences from file
 * 
 * @see Preferences
 * 
 * @author Mario Pascucci
 *
 */
public class AppSettings {
	
	// true if preferences was read from file
	private static boolean configured = false;
	
	// type definition for preference type
	public static final int STRING = 1;
	public static final int BOOLEAN = 2;
	public static final int INTEGER = 3;
	public static final int FLOAT = 4;
	public static final int FILE = 5;
	public static final int FOLDER = 6;
	public static final int MAXTYPES = 6;
	
	private static Preferences prefs;
	
	// internal structures for preferences name, description and type
	private static Map<String,String> displayName = new HashMap<String,String>();
	private static Map<String,Integer> types = new HashMap<String,Integer>();
	private static Map<String,Boolean> appPrivate = new HashMap<String,Boolean>();
	
	// defaults 
	private static Map<String,String> defStrings = new HashMap<String,String>();
	private static Map<String,Integer> defInts = new HashMap<String,Integer>();
	private static Map<String,Boolean> defBools = new HashMap<String,Boolean>();
	private static Map<String,Float> defFloats = new HashMap<String,Float>();
	
	private static List<String> names = new ArrayList<String>(); 
	
	private static String prefsFile = null;
	


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



	/**
	 * Opens and reads preferences from file, if exists.
	 * If not exists, creates a new file, and set a boolean to false to notify that is a first run.
	 * File name is generated from SimpleClassName() + ".prefs" and stored in current directory.
	 * After preference file is inited, we can add our preferences with addPref method
	 * @param app application object Class
	 * @return true if 
	 */
	public static boolean openPreferences(Object app) {		
		
		if (prefsFile == null) {
			if (app == null) {
				throw new IllegalArgumentException("Application Class can't be null"); 
			}
			else {
				prefsFile = app.getClass().getSimpleName()+".prefs";
			}
		}
		File pf = new File(prefsFile);
		configured = false;
		//prefs = Preferences.userNodeForPackage(app.getClass());
		if (pf.canRead() && pf.isFile()) {
			try {
				prefs = Preferences.userNodeForPackage(app.getClass());
				prefs.clear();
				Preferences.importPreferences(new FileInputStream(pf));
				configured = true;

			} catch (FileNotFoundException e) {
				try {
					prefs.clear();
				} catch (BackingStoreException e1) {
					// nothing to do
					Logger.getGlobal().log(Level.SEVERE,"Preferences read error", e1);
				}
			} catch (IOException e) {
				Logger.getGlobal().log(Level.SEVERE,"Preferences read error", e);
			} catch (InvalidPreferencesFormatException | BackingStoreException e) {
				Logger.getGlobal().log(Level.SEVERE,"Preferences read error", e);
			}
		}
		else {
			try {
				prefs = Preferences.userNodeForPackage(app.getClass());
				prefs.clear();
			} catch (BackingStoreException e1) {
				// nothing to do
				Logger.getGlobal().log(Level.SEVERE,"Preferences internal error", e1);
			}
		}
		
		return isConfigured();
	}
	
	
	/**
	 * Checks if it is a first run, i.e. return false if preference file doesn't exists
	 * @return true if a preference file already exists
	 */
	public static boolean isConfigured() {
		
		return configured;
	}


	/**
	 * Gets a list of user defined preferences
	 * @return list of preferences 
	 */
	public static List<String> getPrefsList() {
		
		return names;
	}
	
	
	/**
	 * Retrieve a description text for named preference
	 * @param name preference to retrieve
	 * @return a string that describes named preference
	 */
	public static String getDescr(String name) {
		
		return displayName.get(name);
	}
	
	
	
	/**
	 * Add a preference to settings list.
	 * A preference is defined by a unique name, with a description text (used in OptionDialog as text description) and a type
	 * 
	 * @param name unique name for preference
	 * @param dispName description text
	 * @param type chosen from defined type (see above)
	 */
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
		appPrivate.put(name, false); 	// user defined
	}

	
	/**
	 * Add an app private preference to settings list.
	 * A private preference is defined by a unique name, with a description text (used in OptionDialog as text description) and a type, 
	 * but isn't user defined, and don't appear in options setting dialog
	 * 
	 * @param name unique name for preference
	 * @param dispName description text
	 * @param type chosen from defined type (see above)
	 */
	public static void addPrivatePref(String name, String dispName, int type) {
		
		addPref(name, dispName, type);
		appPrivate.put(name, true); 	// app private
	}

	
	/**
	 * @param name preference to query
	 * @return type of preference (see above)
	 */
	public static int getType(String name) {
		
		return types.get(name);
	}
	
	
	
	public static boolean isAppPrivate(String key) {
		
		if (key == null || key =="")
			throw new IllegalArgumentException("Parameter 'key' is null or empty string.");
		return appPrivate.get(key);
	}
	
	
	
	/**
	 * Write current preferences to preference file
	 * @throws IOException
	 * @throws BackingStoreException
	 */
	public static void savePreferences() throws IOException, BackingStoreException {
		
		File spf = new File(prefsFile);
		prefs.exportSubtree(new FileOutputStream(spf));
	}
	
	
	/**
	 * Save a preference of type STRING, FILE or FOLDER
	 * @param key preference name to save
	 * @param value
	 */
	public static void put(String key, String value) {
		prefs.put(key, value);
	}
	
	
	/**
	 * Save a preference of type BOOLEAN
	 * @param key preference name to save
	 * @param value
	 */
	public static void putBool(String key, boolean value) {
		prefs.putBoolean(key, value);
	}
	
	
	/**
	 * Save a preference of type INTEGER
	 * @param key preference name to save
	 * @param value
	 */
	public static void putInt(String key, int value) {
		prefs.putInt(key, value);
	}
	
	
	/**
	 * Save a preference of type FLOAT
	 * @param key preference name to save
	 * @param value
	 */
	public static void putFloat(String key, float value) {
		prefs.putFloat(key, value);
	}
	
	
	/**
	 * Define a default for type STRING, FILE or FOLDER
	 * @param key preference name to read
	 * @param def default value if "key" is not present
	 */
	public static void defString(String key, String def) {
		defStrings.put(key, def);
	}
	

	/**
	 * Read a preference of type STRING, FILE or FOLDER
	 * @param key preference name to read
	 * @return value for preference or default if defined or empty string if preference is not defined
	 */
	public static String get(String key) {
		if (defStrings.containsKey(key))
			return prefs.get(key, defStrings.get(key));
		else
			return prefs.get(key, "");
	}
	

	/**
	 * Read a preference of type BOOLEAN
	 * @param key preference name to read
	 * @return value for preference or default if defined or false if preference is not defined
	 */
	public static boolean getBool(String key) {
		if (defBools.containsKey(key))
			return prefs.getBoolean(key,defBools.get(key));
		else
			return prefs.getBoolean(key, false);
	}

	
	/**
	 * Define a default for type BOOLEAN
	 * @param key preference name to read
	 * @param def default value if "key" is not present
	 */
	public static void defBool(String key, boolean def) {
		defBools.put(key, def);
	}
	

	/**
	 * Read a preference of type INTEGER
	 * @param key preference name to read
	 * @return value for preference or default if defined or zero if preference is not defined
	 */
	public static int getInt(String key) {
		if (defInts.containsKey(key))
			return prefs.getInt(key, defInts.get(key));
		else
			return prefs.getInt(key, 0);
	}
	
	
	/**
	 * Define a default for type INTEGER
	 * @param key preference name to read
	 * @param def default value if "key" is not present
	 */
	public static void defInt(String key, int def) {
		defInts.put(key, def);
	}
	

	/**
	 * Read a preference of type FLOAT
	 * @param key preference name to read
	 * @return value for preference or zero if preference is not defined
	 */
	public static float getFloat(String key) {
		if (defFloats.containsKey(key))
			return prefs.getFloat(key, defFloats.get(key));
		else
			return prefs.getFloat(key, 0f);
	}
	

	/**
	 * Define a default for type FLOAT
	 * @param key preference name to read
	 * @param def default value if "key" is not present
	 */
	public static void defFloat(String key, float def) {
		defFloats.put(key, def);
	}
	

}
