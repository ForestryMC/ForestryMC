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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.blocks.BlockSoil;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;
import forestry.plugins.PluginCore;

public class FarmLogicPeat extends FarmLogicWatered {
	private static final ItemStack bogEarth = PluginCore.blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, 1);

	public FarmLogicPeat(IFarmHousing housing) {
		super(housing, bogEarth, bogEarth);
	}

	@Override
	public boolean isAcceptedGround(ItemStack itemStack) {
		if (super.isAcceptedGround(itemStack)) {
			return true;
		}

		Block block = BlockSoil.getBlockFromItem(itemStack.getItem());
		if (!(block instanceof BlockSoil)) {
			return false;
		}
		BlockSoil blockSoil = (BlockSoil) block;
		BlockSoil.SoilType soilType = blockSoil.getTypeFromMeta(itemStack.getItemDamage());
		return soilType == BlockSoil.SoilType.BOG_EARTH || soilType == BlockSoil.SoilType.PEAT;
	}

	@Override
	public int getFertilizerConsumption() {
		return 2;
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Peat Bog";
		} else {
			return "Managed Peat Bog";
		}
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			ItemStack occupant = VectUtil.getAsItemStack(world, position);

			if (occupant.getItem() == null) {
				continue;
			}

			Block block = Block.getBlockFromItem(occupant.getItem());
			if (!(block instanceof BlockSoil)) {
				continue;
			}

			BlockSoil blockSoil = (BlockSoil) block;
			BlockSoil.SoilType soilType = blockSoil.getTypeFromMeta(occupant.getItemDamage());

			if (soilType == BlockSoil.SoilType.PEAT) {
				crops.push(new CropPeat(world, position));
			}
		}
		return crops;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return PluginCore.items.peat.getIconFromDamage(0);
	}

}
