/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.render;

import forestry.core.utils.Log;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class ColourProperties implements ISelectiveResourceReloadListener {

    public static final ColourProperties INSTANCE;

    static {
        INSTANCE = new ColourProperties();
    }

    private final Properties defaultMappings = new Properties();
    private final Properties mappings = new Properties();

    private ColourProperties() {
    }

    public synchronized int get(String key) {
        return Integer.parseInt(mappings.getProperty(key, defaultMappings.getProperty(key, "d67fff")), 16);
    }

    @Override    //TODO - actually be selective
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> pred) {
        try {
            InputStream defaultFontStream = ColourProperties.class.getResourceAsStream(
                    "/config/forestry/colour.properties");
            mappings.load(defaultFontStream);
            defaultMappings.load(defaultFontStream);

            defaultFontStream.close();
        } catch (IOException e) {
            Log.error("Failed to load colors.properties.", e);
        }
    }

}
