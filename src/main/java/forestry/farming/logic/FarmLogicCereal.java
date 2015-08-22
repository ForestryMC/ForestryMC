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
package forestry.farming.logic;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;

public class FarmLogicCereal extends FarmLogicCrops {

	public FarmLogicCereal(IFarmHousing housing) {
		super(housing, Farmables.farmables.get("farmWheat").toArray(new IFarmable[0]));
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Farm";
		} else {
			return "Managed Farm";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return getSprite("minecraft", "items/wheat");
	}

}
