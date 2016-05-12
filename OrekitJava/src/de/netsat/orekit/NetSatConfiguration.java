package de.netsat.orekit;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.orekit.data.DataProvidersManager;

import de.netsat.orekit.util.ZipJarCrawler;

/**
 * Configuration class. Automatically fetches configuration files, if 
 * existing ones are older than defined age.
 * 
 * @author Slavi Dombrovski
 * @version 1.0
 * 
 * @see MAXIMUM_AGE_DAYS
 * 
 * @see https://www.orekit.org/forge/projects/orekit/wiki/Configuration
 * @see https://www.orekit.org/forge/projects/orekit/wiki/FAQ
 */
public class NetSatConfiguration {
	/** 
	 * Maximum age of the NetSat configuration file. If the file is older, 
	 * new files will get fetched from the defined remote FTP/HTTP servers 
	 * followed by a new ZIP creation. Everything is done automatically, so 
	 * you only need to adjust the maximum age here.
	 */
	public static final int MAXIMUM_AGE_DAYS = 365;
	private static boolean initialized = false;
	
	/** 
	 * Initialization method. Should be called in the beginning.
	 */
	public static void init() {
		if(initialized)
			return;
		initialized = true;
		// Netsat specific configuration
		try {
			// The configuration will be refreshed automatically
			NetSatConfigurationCreator.getRemoteConfiguration(MAXIMUM_AGE_DAYS);
			File f = new File("lib/orekit-netsat-data.zip");
			if(f.exists()) {
				// Our own crawler is bug-free
				DataProvidersManager.getInstance().addProvider(new ZipJarCrawler(f));
			} else {
				System.err.println(f.getAbsolutePath() + " does not exists!");
			}
		} catch (Exception e){
			System.err.println("Error occured during the configuration of NetSat data: " 
					+ e.getMessage());
		}
		
		// Standard configuration
    	final File home    = new File(System.getProperty("user.home"));
        final File current = new File(System.getProperty("user.dir"));
        StringBuffer pathBuffer = new StringBuffer();
        appendIfExists(pathBuffer, new File("lib/orekit-netsat-data.zip")); 
        appendIfExists(pathBuffer, new File(current, "orekit-data.zip"));
        appendIfExists(pathBuffer, new File(current, "orekit-data"));
        appendIfExists(pathBuffer, new File(current, ".orekit-data"));
        appendIfExists(pathBuffer, new File(home,    "orekit-data.zip"));
        appendIfExists(pathBuffer, new File(home,    "orekit-data"));
        appendIfExists(pathBuffer, new File(home,    ".orekit-data"));
        appendIfExists(pathBuffer, "regular-data");
        System.setProperty(DataProvidersManager.OREKIT_DATA_PATH, pathBuffer.toString());
	}
	
    /** 
     * Appends a directory/zip archive to the path if it exists.
     * 
     * @param path placeholder where to put the directory/zip archive
     * @param file file to try
     */
    private static void appendIfExists(final StringBuffer path, final File file) {
        if (file.exists() && (file.isDirectory() || file.getName().endsWith(".zip"))) {
            if (path.length() > 0) {
                path.append(System.getProperty("path.separator"));
            }
            path.append(file.getAbsolutePath());
        }
    }

    /** 
     * Appends a classpath-related directory to the path if the directory exists.
     * 
     * @param path placeholder where to put the directory
     * @param directory directory to try
     */
    private static void appendIfExists(final StringBuffer path, final String directory) {
        try {
            final URL url = NetSatConfiguration.class.getClassLoader().getResource(directory);
            if (url != null) {
                if (path.length() > 0) {
                    path.append(System.getProperty("path.separator"));
                }
                path.append(url.toURI().getPath());
            }
        } catch (URISyntaxException use) {
            // display an error message and simply ignore the path
            System.err.println(use.getLocalizedMessage());
        }
    }
}
