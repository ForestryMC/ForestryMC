/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.render;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import forestry.api.arboriculture.EnumLeafType;
import forestry.core.render.TextureManager;

public class TextureLeaves {

    private static final Map<EnumLeafType, TextureLeaves> leafTextures = new EnumMap<>(EnumLeafType.class);

    static {
        for (EnumLeafType leafType : EnumLeafType.values()) {
            leafTextures.put(leafType, new TextureLeaves(leafType));
        }
    }

    public static TextureLeaves get(EnumLeafType leafType) {
        return leafTextures.get(leafType);
    }

    public static void registerAllIcons(IIconRegister register) {
        for (TextureLeaves leafTexture : leafTextures.values()) {
            leafTexture.registerIcons(register);
        }
    }

    private final EnumLeafType leafType;

    private IIcon plain;
    private IIcon pollinated;
    private IIcon fancy;

    private TextureLeaves(EnumLeafType enumLeafType) {
        this.leafType = enumLeafType;
    }

    private void registerIcons(IIconRegister register) {
        String ident = leafType.toString().toLowerCase(Locale.ENGLISH);

        plain = TextureManager.registerTex(register, "leaves/" + ident + ".plain");
        pollinated = TextureManager.registerTex(register, "leaves/" + ident + ".changed");
        fancy = TextureManager.registerTex(register, "leaves/" + ident + ".fancy");
    }

    public IIcon getPlain() {
        return plain;
    }

    public IIcon getPollinated() {
        return pollinated;
    }

    public IIcon getFancy() {
        return fancy;
    }
}
