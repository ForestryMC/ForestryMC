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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;

public class FarmLogicShroom extends FarmLogicArboreal {

	public FarmLogicShroom(IFarmHousing housing) {
		super(housing,
				new ItemStack[]{new ItemStack(Blocks.mycelium)},
				new ItemStack(Blocks.mycelium),
				Farmables.farmables.get("farmShroom").toArray(new IFarmable[0]));
		yOffset = -1;
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Shroom Farm";
		} else {
			return "Managed Shroom Farm";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return getSprite("minecraft", "blocks/mushroom_red");
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (80 * hydrationModifier);
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = new ArrayList<ItemStack>();
		return products;
	}

}
