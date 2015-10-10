/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

import forestry.Forestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;

/**
 * With permission from pahimar.
 *
 * @author Pahimar
 */
public class Version {

	public enum EnumUpdateState {

		CURRENT, OUTDATED, CONNECTION_ERROR
	}

	public static final String VERSION = "@VERSION@";
	public static final String BUILD_NUMBER = "@BUILD_NUMBER@";
	public static final String[] FAILED_CHANGELOG = new String[]{String.format("Unable to retrieve changelog for %s", Constants.MOD)};
	private static final String REMOTE_VERSION_FILE = "http://bit.ly/forestryver";
	private static final String REMOTE_CHANGELOG_ROOT = "https://dl.dropbox.com/u/44760587/forestry/changelog/";
	public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;
	private static String recommendedVersion;
	private static String[] cachedChangelog = FAILED_CHANGELOG;

	public static String getVersion() {
		return VERSION + " (" + BUILD_NUMBER + ")";
	}

	public static boolean isOutdated() {
		return currentVersion == EnumUpdateState.OUTDATED;
	}

	public static boolean needsUpdateNoticeAndMarkAsSeen() {
		if (!isOutdated()) {
			return false;
		}

		File versionFile = new File(Forestry.instance.getConfigFolder(), Config.CATEGORY_COMMON + ".cfg");
		Configuration config = new Configuration(versionFile);
		Property property = config.get("version", "seen", VERSION);
		property.comment = "indicates the last version the user has been informed about and will suppress further notices on it.";
		String seenVersion = property.getString();

		if (recommendedVersion == null || recommendedVersion.equals(seenVersion)) {
			return false;
		}

		property.set(recommendedVersion);
		config.save();
		return true;
	}

	public static String getRecommendedVersion() {
		return recommendedVersion;
	}

	private static class VersionChecker extends Thread {

		@Override
		public void run() {
			try {
				String location = REMOTE_VERSION_FILE;
				HttpURLConnection conn = null;
				while (location != null && !location.isEmpty()) {
					URL url = new URL(location);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
					conn.connect();
					location = conn.getHeaderField("Location");
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				String line;
				String mcVersion = Proxies.common.getMinecraftVersion();
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(mcVersion) && line.contains(Constants.MOD)) {
						String[] tokens = line.split(":");
						recommendedVersion = tokens[2];
					}
				}

				if (recommendedVersion == null) {
					return;
				}

				int result = FMLCommonHandler.instance().findContainerFor(Forestry.instance).getProcessedVersion().compareTo(new DefaultArtifactVersion(recommendedVersion));
				if (result >= 0) {
					Log.finer("Using the latest version [" + VERSION + " (build:" + BUILD_NUMBER + ")] for Minecraft " + mcVersion);
					currentVersion = EnumUpdateState.CURRENT;
					return;
				}

				cachedChangelog = grabChangelog(recommendedVersion);

				Log.warning("Using outdated version [" + VERSION + " (build:" + BUILD_NUMBER + ")] for Minecraft " + mcVersion + ". Consider updating.");
				currentVersion = EnumUpdateState.OUTDATED;

			} catch (Exception e) {
				//				e.printStackTrace();
				Log.warning("Unable to read from remote version authority.");
				currentVersion = EnumUpdateState.CONNECTION_ERROR;
			}
		}
	}

	public static void versionCheck() {
		new VersionChecker().start();
	}

	public static String[] getChangelog() {
		return cachedChangelog;
	}

	public static String[] grabChangelog(String version) {

		try {
			String location = REMOTE_CHANGELOG_ROOT + version;
			HttpURLConnection conn;
			do {
				URL url = new URL(location);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
				conn.connect();
				location = conn.getHeaderField("Location");
			} while (location != null && !location.isEmpty());

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;
			ArrayList<String> changelog = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (line.isEmpty()) {
					continue;
				}

				changelog.add(line);
			}

			return changelog.toArray(new String[changelog.size()]);

		} catch (Exception ex) {
			//			ex.printStackTrace();
			Log.warning("Unable to read changelog from remote site.");
		}

		return new String[]{String.format("Unable to retrieve changelog for %s %s", Constants.MOD, version)};
	}
}
