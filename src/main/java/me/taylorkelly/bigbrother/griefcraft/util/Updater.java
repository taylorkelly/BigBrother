/**
 * This file is based on that of LWC (https://github.com/Hidendra/LWC)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.taylorkelly.bigbrother.griefcraft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Updater {

	private Logger logger = Logger.getLogger("Minecraft");

	private final static String UPDATE_SITE = "http://stethoscopesmp.com/tkelly/";

	private List<UpdaterFile> needsUpdating = new ArrayList<UpdaterFile>();

	public Updater() {
	}

	public void check() {
		String[] paths = new String[] { "lib/h2.jar", "lib/" + getOSSpecificFileName(), "lib/mysql.jar" };

		for (String path : paths) {
			File file = new File(path);

			if (file != null && !file.exists() && !file.isDirectory()) {
			    String url=UPDATE_SITE+path;
			    if(path.equalsIgnoreCase("lib/h2.jar"))
			        url="http://mine.7chan.org/mirror/lib/h2.jar"; // Temporary
				UpdaterFile updaterFile = new UpdaterFile(url);
				updaterFile.setLocalLocation(path);
				needsUpdating.add(updaterFile);
			}
		}
	}


	/**
	 * Get the OS specific sqlite file name (arch specific, too, for linux)
	 * 
	 * @return
	 */
	public String getOSSpecificFileName() {
		String osname = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch");

		if (osname.contains("windows")) {
			osname = "win";
			arch = "x86";
		} else if (osname.contains("mac")) {
			osname = "mac";
			arch = "universal";
		} else if (osname.contains("nix")) {
			osname = "linux";
		} else if (osname.equals("sunos")) {
			osname = "linux";
		}

		if (arch.startsWith("i") && arch.endsWith("86")) {
			arch = "x86";
		}

		return osname + "-" + arch + ".lib";
	}

	/**
	 * Ensure we have all of the required files (if not, download them)
	 */
	public void update() throws Exception {
		if (needsUpdating.size() == 0) {
			return;
		}

		File folder = new File("lib");

		if (folder.exists() && !folder.isDirectory()) {
			throw new Exception("Folder \"lib\" cannot be created ! It is a file!");
		} else if (!folder.exists()) {
			logger.info("Creating folder : lib");
			folder.mkdir();
		}

		logger.info("Need to download " + needsUpdating.size() + " object(s)");

		Iterator<UpdaterFile> iterator = needsUpdating.iterator();
		
		while(iterator.hasNext()) {
			UpdaterFile item = iterator.next();
			
			logger.info(" - Downloading file : " + item.getRemoteLocation());

			URL url = new URL(item.getRemoteLocation());
			File file = new File(item.getLocalLocation());

			if (file.exists()) {
				file.delete();
			}

			InputStream inputStream = url.openStream();
			OutputStream outputStream = new FileOutputStream(file);

			saveTo(inputStream, outputStream);

			inputStream.close();
			outputStream.close();

			logger.info("  + Download complete");
			iterator.remove();
		}
	}


	/**
	 * Write an input stream to an output stream
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	private void saveTo(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;

		while ((len = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}
	}

}
