package de.netsat.orekit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import de.netsat.orekit.util.IOUtils;

/**
 * This module transmits all defined remote files into the local data folder and zips them afterwards.
 * 
 * @author Slavi Dombrovski
 * @version 1.0
 */
public class NetSatConfigurationCreator {
	
	/**
	 * Definition of server addresses.
	 */
	public static final String[][] CONFIGS = { // 
		// DIRECTORY FILTER _OR_ SINGLE FILE									   FILE FILTER
		{"https://hpiers.obspm.fr/eoppc/bul/bulc/UTC-TAI.history",			       ""},						// leap seconds introduction history
		{"ftp://ftp.iers.org/products/eop/rapid/bulletina/.*-volume-.*",	       "bulletina.*[.]txt"},	// weekly Earth Orientation Parameters, IAU-1980 and IAU-2000 rapid service and prediction
		{"ftp://ftp.iers.org/products/eop/bulletinb/format_2009",        	       "bulletinb-.*[.]txt"},	// monthly Earth Orientation Parameters model IAU 2006/2000A, final values
		{"ftp://ftp.iers.org/products/eop/long-term/c04_08/iau2000", 		       "eopc04_.*"}, 			// yearly Earth Orientation Parameters model IAU 2006/2000A
		{"ftp://ftp.iers.org/products/eop/long-term/c04_08/iau1980",               "eopc04.*"},		    	// yearly Earth Orientation Parameters model IAU 1980
		{"ftp://ftp.iers.org/products/eop/rapid/standard/finals2000A.all",         ""},	        // Earth Orientation Parameters model IAU 2006/2000A
		{"ftp://ftp.iers.org/products/eop/rapid/standard/finals.all",              ""},	        // Earth Orientation Parameters model IAU 1980
		{"ftp://ftp.iers.org/products/eop/rapid/standard/xml/finals2000A.all.xml", ""},         // Earth Orientation Parameters model IAU 2006/2000A
		{"ftp://ftp.iers.org/products/eop/rapid/standard/xml/finals.all.xml",      ""},         // Earth Orientation Parameters model IAU 1980
		
		// The following entry will produce couple of Gigabytes, so use with caution
		//{"ftp://ssd.jpl.nasa.gov/pub/eph/planets/Linux/de.*",					   ".*"},	    // JPL DE 4xx planets ephemerides
		{"ftp://ssd.jpl.nasa.gov/pub/eph/planets/Linux/de421/lnxp1900p2053.421",   ""},			// JPL DE421 ephemeris, incl. libration and nutation, valid until 2053
		
		// The following entry will produce couple of Gigabytes, so use with caution
		//{"ftp://ftp.imcce.fr/pub/ephem/planets/inpop.*", 						   "inpop.*_littleendian[.]dat"},	// IMCCE inpop planets ephemerides
		{"ftp://ftp.imcce.fr/pub/ephem/planets/inpop13c/inpop13c_TDB_m100_p100_littleendian.dat", ""}, 				// IMCCE inpop planets ephemerides selection
		
		// Old Format -> skip (?)
		//{"http://op.gfz-potsdam.de/grace/results/main_RESULTS.html#gravity", ""}, 			// Eigen gravity field (old format)
		
		// Tripper stuff (no automation...):
		//{"http://icgem.gfz-potsdam.de/ICGEM/modelstab.html", 					   ""},				// gravity fields from International Centre for Global Earth Models
		{"http://icgem.gfz-potsdam.de/ICGEM/shms/eigen-6s.gfc", 				   ""}, 			// see http://icgem.gfz-potsdam.de/ICGEM/shms/
		
		{"ftp://cddis.gsfc.nasa.gov/pub/egm96/general_info",			   		   "egm.*_to.*"},	// EGM gravity field
		
		// Solar Activity... Tripper again...
		//{"http://sail.msfc.nasa.gov/archive_index.htm",						   ""},				// Marshall Solar Activity Future Estimation
		{"http://sail.msfc.nasa.gov/solar_report_archives/Jan2015F10.txt",		   ""},				// Jan 2015
		{"http://sail.msfc.nasa.gov/solar_report_archives/Feb2015F10.txt",		   ""},				// Jan 2015
		{"http://sail.msfc.nasa.gov/solar_report_archives/Mar2015F10.txt",		   ""},				// Jan 2015
		
		{"http://syrte.obspm.fr/~lambert/fcn/table.txt",						   ""}				// Lambert Function (per year)
	};
	
	/**
	 * Catches and returns files from the given target.
	 * 
	 * @param f FTP client
	 * @param target target address
	 * @param fileFilter 
	 * @return
	 * @throws IOException
	 */
	private static final String[] findFTPFiles(FTPClient f, String target, String fileFilter) throws IOException {
		if(f == null || target == null)
			return new String[0];
		System.out.println("Search files in " + target + "/" + fileFilter);
		String host = target;
		List<String> toRet = new LinkedList<String>();
		int i;
		if((i = target.indexOf("//")) != -1) {// ftp://test.domain.com/dir/subdir -> test.domain.com/dir/subdir/
			target = target.substring(i+2);
			if(host.indexOf("/", i+2) != -1)
				host = host.substring(0, host.indexOf("/", i+2));
		}
		if((i = target.indexOf("/")) != -1) // test.domain.com/dir/subdir -> dir/subdir/
			target = target.substring(i+1);
		if(target.endsWith("/"))
			target = target.substring(0, target.length() - 1); // dir/subdir/ -> dir/subdir
		
		// Find out the directories
		List<String> dirs = new LinkedList<String>();
		if(target.contains("?") || target.contains("*")) {
			String parent    = (target.contains("/") ? target.substring(0, target.lastIndexOf("/"))  : "/");
			String dirFilter = (target.contains("/") ? target.substring(target.lastIndexOf("/") + 1) : target);
			for(FTPFile dir : f.listDirectories(parent)) {
				if(dir.getName().matches(dirFilter))
					dirs.add(parent + "/" + dir.getName());
			}
			System.out.println(dirs.size() + " sub directories found");
		} else {
			dirs.add(target);
		}
		
		// Find out the files
		for(String dir : dirs) {
			for(FTPFile file : f.listFiles(dir)) {
				if(file.getName().matches(fileFilter))
					toRet.add(host + "/" + dir + "/" + file.getName());
			}
		}
		System.out.println(toRet.size() + " files found");
		return toRet.toArray(new String[toRet.size()]);
	}
	
	/**
	 * Transmit all defined configuration files from remote servers into the local 'data' directory
	 */
	static void getRemoteConfiguration(int maximumAgeDays) {
		getRemoteConfiguration(new File("data").getAbsolutePath(), new File("lib/orekit-netsat-data.zip").getAbsolutePath(), maximumAgeDays);
	}
	
	/**
	 * Transmit all defined configuration files from remote servers into the defined directory
	 */
	static void getRemoteConfiguration(String into, String zipFile, int maximumAgeDays) {
		File zip = new File(zipFile);
		if(zip.exists()) {
			long modified = IOUtils.getZipAge(zipFile);
			int age = (int)((System.currentTimeMillis() - modified) / (1000 * 60 * 60 * 24));
			System.out.println("The configuration is " +age+" days old (Maximum: "+maximumAgeDays+" days)");
			if(age <= maximumAgeDays)
				return;
		}
		System.out.println("Receive new netsat configuration from remote servers...\r\n");
		NetSatConfigurationCreator cl = new NetSatConfigurationCreator();
		// Transmit data...
		for(String[] c : CONFIGS) {
			try {
				String[] urls = cl.find(c[0], c[1]);
				int cnt = 1;
				for(String url : urls) {
					System.out.print("["+(cnt++)+" of "+urls.length+"] copy " + url + " -> ");
					try {
						String createdFile = cl.copy(url, into, false);
						System.out.println(createdFile);
					} catch(Exception ex) {
						System.out.println("FAILED");
						Thread.sleep(1);
						System.err.println(ex.getMessage());
						Thread.sleep(1);
					}
				}
			} catch(Exception ex) {
				System.err.println("Error occured during the resolve of " + c[0] + "/" + c[1] + ": " + ex.getMessage());
			}
		}
		// Create ZIP
		try {
			IOUtils.writeZipFile(into, zipFile);
			System.out.println(zipFile + " written");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	private FTPClient f = null;
	
	private String connectedToHost = null;
	
	/**
	 * Connect to required FTP if not already happened
	 */
	private void connectToFTP(String target) throws SocketException, IOException {
		String host = target.substring(target.indexOf("//") + 2);
		host = host.substring(0, host.indexOf("/"));
		if(f == null)
			f = new FTPClient();
		if(!f.isConnected() || !host.equalsIgnoreCase(connectedToHost)) {
			f.connect(host);
			f.enterLocalPassiveMode();
			f.login("anonymous", "");
			connectedToHost = host;
		}
	}
	
	/** Copy remote file into the given directory. The subdirectories are created automatically
	 * @throws IOException 
	 */
	private String copy(String url, String into, boolean replaceIfExists) throws IOException {
		if(url == null || into == null)
			return null;
		
		// Create necessary paths
		String postfixFile = (url.contains("//") ? url.substring(url.indexOf("//") + 2) : url); // ftp://test.domain.de/dir/file.txt -> test.domain.de/dir/file.txt
		String postfixDir = (postfixFile.contains("/") ? postfixFile.substring(0, postfixFile.lastIndexOf("/")) : postfixFile); // test.domain.de/dir/file.txt -> test.domain.de/dir
		String remoteFile = (postfixFile.contains("/") ? postfixFile.substring(postfixFile.indexOf("/") + 1) : postfixFile);     // test.domain.de/dir/file.txt -> dir/file.txt
		File localFile = new File(into + File.separator + postfixFile);
		if(!replaceIfExists && localFile.exists() && localFile.length() > 0)
			return localFile.getAbsolutePath();
		File localDir = new File(into + File.separator + postfixDir);
		if(localDir.exists() && !localDir.isDirectory())
			localDir.delete();
		if(!localDir.exists())
			localDir.mkdirs();
		
		// Get the stream
		InputStream is = null;
		if(url.toLowerCase().startsWith("ftp")) {
			connectToFTP(url);
			is = f.retrieveFileStream(remoteFile);
			if(is == null)
				throw new IOException(remoteFile + " does not exist");
			try {
				IOUtils.copy(is, new FileOutputStream(localFile), true);
			} finally {
				f.completePendingCommand();
			}
		} else {
			is = new URL(url).openStream(); // Note: its much slower to use this technique instead of using FTP client
			IOUtils.copy(is, new FileOutputStream(localFile), true);
		}
		// Return the created file name
		return localFile.getAbsolutePath();
	}
	
	/**
	 * Resolve can be used to expand one text entry to multiple files. Usually this is the case
	 * when multiple files on a FTP server have to be added in a simple manner.
	 * If the target does not contain ? or * it is handled as a single target file definition
	 * @param target
	 * @param fileFilter
	 * @return
	 * @throws IOException 
	 * @throws SocketException 
	 */
	private final String[] find(String target, String fileFilter) throws SocketException, IOException {
		if(target == null)
			return new String[0];
		if(target.startsWith("http://") || target.startsWith("https://")) {
			// Only single Files can be added using HTTP-protocol (no lists, no subdirs)
			return new String[] {target};
		} else if(target.startsWith("ftp://")) {
			// Single FTP file? Or a list of file and/or subdirectories
			if((fileFilter == null || fileFilter.isEmpty()) && !(target.contains("?") || target.contains("*"))) {
				return new String[] {target};
			} else {
				if(target.contains("?") || target.contains("*") || (fileFilter != null && !fileFilter.isEmpty())) { // Subdirectories defined?
					connectToFTP(target);
					return findFTPFiles(f, target, fileFilter);
				} else {
					return new String[] {target};
				}
			}
		} else if(target.toLowerCase().startsWith("file")) {
			if((fileFilter == null || fileFilter.isEmpty()) && !(target.contains("?") || target.contains("*"))) {
				return new String[] {target};
			} else {
				throw new RuntimeException("Directory resolve is not implemented yet");
			}
		}
		return new String[] {target};
	}
}
