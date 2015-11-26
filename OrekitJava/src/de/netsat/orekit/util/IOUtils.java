package de.netsat.orekit.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class IOUtils {
	private static final int COPY_BUFFER = 8096;
	/** Recursively retrieve all Files within a directory...
	 */
	public static void getAllFiles(File dir, List<File> into) {
		File[] files = dir.listFiles();
		for (File file : files) {
			into.add(file);
			if (file.isDirectory())
				getAllFiles(file, into);
		}
	}
	
	/**
	 * Returns the age of the zip file. Since SVN removes the actual age, we need to open the zip file
	 * and search until we find the first entry. The age of the entry is the age of the zip-File
	 * @param zipFile
	 * @return
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public static long getZipAge(String zipFile) {
		if(zipFile == null || !zipFile.toLowerCase().endsWith(".zip"))
			return 0;
		ZipFile zf = null;
		try {
			zf = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while(entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				if(!ze.isDirectory())
					return ze.getTime();
			}
		} catch(Exception ex) {
		} finally {
			if(zf != null) try {
				zf.close();
			} catch(Exception ex) {}
		}
		return 0;
	}
	
	/**
	 * Write recursively a given directory into a zip file
	 */
	public static void writeZipFile(String toZip, String zip) throws FileNotFoundException, IOException {
		File directoryToZip = new File(toZip);
		List<File> fileList = new LinkedList<File>();
		getAllFiles(directoryToZip, fileList);
		System.out.println("Zip '"+toZip+"' " + fileList.size() + " files");
		for(int i = 0; i < 100; i++)
			System.out.print("-");
		System.out.println();
		FileOutputStream fos = new FileOutputStream(zip);
		ZipOutputStream zos = new ZipOutputStream(fos);
		int lastProgress = -1;
		int cnt = 0;
		for (File file : fileList) {
			int progress = (int)((cnt++) * 100.0 / fileList.size());
			if (!file.isDirectory()) { // we only zip files, not directories
				FileInputStream fis = new FileInputStream(file);
				String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
				if(progress > lastProgress) {
					System.out.print("#");
					lastProgress = progress;
				}
					
				ZipEntry zipEntry = new ZipEntry(zipFilePath);
				zos.putNextEntry(zipEntry);

				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zos.write(bytes, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
		}
		System.out.println();
		zos.close();
		fos.close();
	}
	
	/**
	 * Copy the contents of an input stream into an output stream
	 */
	public static final void copy(InputStream from, OutputStream into, boolean close) throws IOException {
		if(from == null || into == null)
			return;
		// Prepare streams
		BufferedInputStream bis = new BufferedInputStream(from);
		BufferedOutputStream bos = new BufferedOutputStream(into);
		try {
			// Write...
			byte[] buffer = new byte[COPY_BUFFER];
			int len;
			while((len = bis.read(buffer)) > 0)
				bos.write(buffer, 0, len);
			bos.flush();
		} finally {
			if(close) {
				try {
					from.close();
				} catch(Exception ex) {};
				try {
					into.close();
				} catch(Exception ex) {};
			}
		}
	}
}
