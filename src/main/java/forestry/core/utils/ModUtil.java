/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import cpw.mods.fml.common.API;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public abstract class ModUtil {

    public static boolean isModLoaded(String modname) {
        return Loader.isModLoaded(modname);
    }

    public static boolean isModLoaded(String modname, String versionRangeString) {
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

    public static boolean isAPILoaded(String apiName) {
        return isAPILoaded(apiName, null);
    }

    public static boolean isAPILoaded(String apiName, String versionRangeString) {
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
