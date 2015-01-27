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

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.render.SpriteSheet;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public abstract class FarmLogic implements IFarmLogic {

	protected final IFarmHousing housing;

	protected boolean isManual;

	private static final HashSet<Block> breakable = new HashSet<Block>();

	public FarmLogic(IFarmHousing housing) {
		this.housing = housing;
	}

	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}

	public boolean canBreakGround(World world, Vect position) {
		Block block = VectUtil.getBlock(world, position);
		if (breakable.isEmpty()) {
			breakable.add(Blocks.air);
			breakable.add(Blocks.dirt);
			breakable.add(Blocks.grass);
			breakable.add(Blocks.sand);
			breakable.add(Blocks.farmland);
			breakable.add(Blocks.mycelium);
			breakable.add(Blocks.soul_sand);
			breakable.add(Blocks.water);
			breakable.add(Blocks.flowing_water);
			breakable.add(ForestryBlock.soil.block());
		}
		return breakable.contains(block) || block.isReplaceable(world, position.getX(), position.getY(), position.getZ());
	}

	protected World getWorld() {
		return housing.getWorld();
	}

	@Override
	public ResourceLocation getSpriteSheet() {
		return SpriteSheet.ITEMS.getLocation();
	}

	protected final boolean isAirBlock(Block block) {
		return block.getMaterial() == Material.air;
	}

	protected final boolean isWaterSourceBlock(World world, Vect position) {
		return world.getBlock(position.x, position.y, position.z) == Blocks.water &&
				world.getBlockMetadata(position.x, position.y, position.z) == 0;
	}

	protected final Vect translateWithOffset(int x, int y, int z, ForgeDirection direction, int step) {
		return new Vect(x + direction.offsetX * step, y + direction.offsetY * step, z + direction.offsetZ * step);
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		getWorld().setBlock(position.x, position.y, position.z, block, meta, Defaults.FLAG_BLOCK_UPDATE | Defaults.FLAG_BLOCK_SYNCH);
	}

}
