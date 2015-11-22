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
package forestry.apiculture.genetics;

import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeIconProvider;
import forestry.core.render.TextureManager;

public class DefaultBeeIconProvider implements IBeeIconProvider {

	public static final DefaultBeeIconProvider instance = new DefaultBeeIconProvider();

	private DefaultBeeIconProvider() {

	}

	private static final IIcon[][] icons = new IIcon[EnumBeeType.values().length][3];

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		String beeIconDir = "bees/default/";
		IIcon body1 = TextureManager.registerTex(register, beeIconDir + "body1");

		for (int i = 0; i < EnumBeeType.values().length; i++) {
			EnumBeeType beeType = EnumBeeType.values()[i];
			if (beeType == EnumBeeType.NONE) {
				continue;
			}

			String beeTypeNameBase = beeIconDir + beeType.toString().toLowerCase(Locale.ENGLISH);

			icons[i][0] = TextureManager.registerTex(register, beeTypeNameBase + ".outline");
			if (beeType == EnumBeeType.LARVAE) {
				icons[i][1] = TextureManager.registerTex(register, beeTypeNameBase + ".body");
			} else {
				icons[i][1] = body1;
			}
			icons[i][2] = TextureManager.registerTex(register, beeTypeNameBase + ".body2");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(EnumBeeType type, int renderPass) {
		return icons[type.ordinal()][renderPass];
	}
}
