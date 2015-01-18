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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import forestry.Forestry;
import forestry.core.proxy.Proxies;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * With permission from pahimar.
 *
 * @author Pahimar
 *
 */
public class Version {

	public enum EnumUpdateState {

		CURRENT, OUTDATED, CONNECTION_ERROR
	}
	public static final String VERSION = "@VERSION@";
	public static final String BUILD_NUMBER = "@BUILD_NUMBER@";
	public static final String[] FAILED_CHANGELOG = new String[] { String.format("Unable to retrieve changelog for %s", Defaults.MOD) };
	public static final String FINGERPRINT = "@FINGERPRINT@";
	public static final String FORGEPRINT = "de4cf8a3f3bc15635810044c39240bf96804ea7d";
	private static final String REMOTE_VERSION_FILE = "http://bit.ly/forestryver";
	private static final String REMOTE_CHANGELOG_ROOT = "https://dl.dropbox.com/u/44760587/forestry/changelog/";
	private static final String REMOTE_FINGERPRINT_ROOT = new String(new byte[] { 104, 116, 116, 112, 115, 58, 47, 47, 100, 108, 46, 100, 114, 111, 112, 98,
		111, 120, 46, 99, 111, 109, 47, 117, 47, 52, 52, 55, 54, 48, 53, 56, 55, 47, 102, 111, 114, 101, 115, 116, 114, 121, 47, 102, 105, 110, 103, 101,
		114, 112, 114, 105, 110, 116, 115, 47 });
	public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;
	public static final String FORGE_VERSION = "@FORGE_VERSION@";
	public static String remoteFingerprint;
	public static String remoteForgeprint;
	private static String recommendedVersion;
	private static String[] cachedChangelog = FAILED_CHANGELOG;

	public static String getVersion() {
		return VERSION + " (" + BUILD_NUMBER + ")";
	}

	public static boolean isOutdated() {
		return currentVersion == EnumUpdateState.OUTDATED;
	}

	public static boolean needsUpdateNoticeAndMarkAsSeen() {
		if (!isOutdated())
			return false;

		Property property = Config.config.get("vars.version.seen", Config.CATEGORY_COMMON, VERSION);
		property.comment = "indicates the last version the user has been informed about and will suppress further notices on it.";
		String seenVersion = property.value;

		if (recommendedVersion == null || recommendedVersion.equals(seenVersion))
			return false;

		property.value = recommendedVersion;
		Config.config.save();
		return true;
	}

	public static String getRecommendedVersion() {
		return recommendedVersion;
	}

	public static void updateRemoteFingerprints() {
		remoteFingerprint = retrieveRemoteString(REMOTE_FINGERPRINT_ROOT + new String(new byte[] { 102, 111, 114, 101, 115, 116, 114, 121, 46, 107, 101, 121 })
				+ "1", FINGERPRINT);
		remoteFingerprint = retrieveRemoteString(REMOTE_FINGERPRINT_ROOT + new String(new byte[] { 102, 111, 114, 103, 101, 46, 107, 101, 121 }) + "1",
				FORGEPRINT);
	}

	public static String retrieveRemoteString(String location, String defaultValue) {
		try {

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
			if ((line = reader.readLine()) != null)
				return line;

		} catch (Exception e) {
			e.printStackTrace();
			Proxies.log.warning("Unable to read from remote fingerprint authority.");
		}

		return defaultValue;
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

				String line = null;
				String mcVersion = Proxies.common.getMinecraftVersion();
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(mcVersion) && line.contains(Defaults.MOD)) {
						String[] tokens = line.split(":");
						recommendedVersion = tokens[2];
					}
				}

				if (recommendedVersion == null)
					return;

				int result = FMLCommonHandler.instance().findContainerFor(Forestry.instance).getProcessedVersion().compareTo(new DefaultArtifactVersion(recommendedVersion));
				if (result >= 0) {
					Proxies.log.finer("Using the latest version [" + VERSION + " (build:" + BUILD_NUMBER + ")] for Minecraft " + mcVersion);
					currentVersion = EnumUpdateState.CURRENT;
					return;
				}

				cachedChangelog = grabChangelog(recommendedVersion);

				Proxies.log.warning("Using outdated version [" + VERSION + " (build:" + BUILD_NUMBER + ")] for Minecraft " + mcVersion + ". Consider updating.");
				currentVersion = EnumUpdateState.OUTDATED;

			} catch (Exception e) {
//				e.printStackTrace();
				Proxies.log.warning("Unable to read from remote version authority.");
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
			HttpURLConnection conn = null;
			 do {
				URL url = new URL(location);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
				conn.connect();
				location = conn.getHeaderField("Location");
			} while (location != null && !location.isEmpty());

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = null;
			ArrayList<String> changelog = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				if (line.isEmpty())
					continue;

				changelog.add(line);
			}

			return changelog.toArray(new String[changelog.size()]);

		} catch (Exception ex) {
//			ex.printStackTrace();
			Proxies.log.warning("Unable to read changelog from remote site.");
		}

		return new String[] { String.format("Unable to retrieve changelog for %s %s", Defaults.MOD, version) };
	}
}
