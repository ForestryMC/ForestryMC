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
package forestry.core.utils;

import javax.annotation.Nullable;

import net.minecraftforge.fml.common.API;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;

public abstract class ModUtil {
	public static boolean isModLoaded(String modname) {
		return Loader.isModLoaded(modname);
	}

	public static boolean isModLoaded(String modname, @Nullable String versionRangeString) {
		if (!isModLoaded(modname)) {
			return false;
		}

		if (versionRangeString != null) {
			ModContainer mod = Loader.instance().getIndexedModList().get(modname);
			ArtifactVersion modVersion = mod.getProcessedVersion();

			VersionRange versionRange = VersionParser.parseRange(versionRangeString);
			DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion(modname, versionRange);

			return requiredVersion.containsVersion(modVersion);
		}

		return true;
	}

	/**
	 * Checks to see if a specific API is loaded.
	 *
	 * @param apiName the package name of the package-info.java file to check. (for example "cofh.api.energy")
	 */
	public static boolean isAPILoaded(String apiName) {
		return isAPILoaded(apiName, null);
	}

	/**
	 * Checks to see if an API within a specific version range is loaded.
	 *
	 * @param apiName            the package name of the package-info.java file to check. (for example "cofh.api.energy")
	 * @param versionRangeString the version range, as defined in {@link VersionParser}.
	 */
	public static boolean isAPILoaded(String apiName, @Nullable String versionRangeString) {
		Package apiPackage = Package.getPackage(apiName);
		if (apiPackage == null) {
			return false;
		}

		API apiAnnotation = apiPackage.getAnnotation(API.class);
		if (apiAnnotation == null) {
			return false;
		}

		if (versionRangeString != null) {
			String apiVersionString = apiAnnotation.apiVersion();
			if (apiVersionString == null) {
				return false;
			}

			VersionRange versionRange = VersionParser.parseRange(versionRangeString);

			DefaultArtifactVersion givenVersion = new DefaultArtifactVersion(apiName, apiVersionString);
			DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion(apiName, versionRange);

			return requiredVersion.containsVersion(givenVersion);
		}

		return true;
	}
}
