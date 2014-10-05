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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.Vect;

public abstract class FarmLogic implements IFarmLogic {

	IFarmHousing housing;

	boolean isManual;

	public FarmLogic(IFarmHousing housing) {
		this.housing = housing;
	}

	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}

	protected World getWorld() {
		return housing.getWorld();
	}

	@Override
	public ResourceLocation getSpriteSheet() {
		return SpriteSheet.ITEMS.getLocation();
	}

	protected final boolean isAirBlock(Vect position) {
		return getWorld().isAirBlock(position.x, position.y, position.z);
	}

	protected final boolean isWaterBlock(Vect position) {
		return getWorld().getBlock(position.x, position.y, position.z) == Blocks.water;
	}

	protected final boolean isWaterBlock(Block block) {
		return block == Blocks.water;
	}

	protected final boolean isWoodBlock(Vect position) {
		Block block = getBlock(position);
		return block.isWood(getWorld(), position.x, position.y, position.z);
	}

	protected final Block getBlock(Vect position) {
		return getWorld().getBlock(position.x, position.y, position.z);
	}

	protected final int getBlockMeta(Vect position) {
		return getWorld().getBlockMetadata(position.x, position.y, position.z);
	}

	protected final ItemStack getAsItemStack(Vect position) {
		return new ItemStack(getBlock(position), 1, getBlockMeta(position));
	}

	protected final Vect translateWithOffset(int x, int y, int z, ForgeDirection direction, int step) {
		return new Vect(x + direction.offsetX * step, y + direction.offsetY * step, z + direction.offsetZ * step);
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		getWorld().setBlock(position.x, position.y, position.z, block, meta, Defaults.FLAG_BLOCK_SYNCH);
	}

}
