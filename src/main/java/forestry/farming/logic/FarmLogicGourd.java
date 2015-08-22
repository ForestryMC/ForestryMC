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

import java.util.Collection;
import java.util.Stack;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.vect.Vect;

public class FarmLogicGourd extends FarmLogic {

	private final IFarmable[] seeds;

	public FarmLogicGourd(IFarmHousing housing) {
		super(housing);
		Collection<IFarmable> farmables = Farmables.farmables.get("farmGourd");
		seeds = farmables.toArray(new IFarmable[farmables.size()]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return getSprite("minecraft", "items/melon");
	}

	@Override
	public String getName() {
		return "Gourd Farm";
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (40 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(BlockPos pos, EnumFacing direction, int extent) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(BlockPos pos, EnumFacing direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos.up(), direction, i);
			for (IFarmable seed : seeds) {
				ICrop crop = seed.getCropAt(world, position.toBlockPos());
				if (crop != null) {
					crops.push(crop);
				}
			}
		}
		return crops;
	}

}
