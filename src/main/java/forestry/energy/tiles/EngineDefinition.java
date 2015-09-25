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
package forestry.energy.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.MachineDefinition;
import forestry.core.tiles.TileEngine;
import forestry.core.utils.PlayerUtil;

public class EngineDefinition extends MachineDefinition {

	public EngineDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		super(meta, teIdent, teClass, renderer, recipes);
	}

	@Override
	public boolean isSolidOnSide(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEngine) {
			return ((TileEngine) tile).getOrientation().getOpposite().ordinal() == side;
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side) {

		if (player.isSneaking()) {
			return false;
		}

		TileEngine tile = (TileEngine) world.getTileEntity(x, y, z);
		if (PlayerUtil.canWrench(player, x, y, z)) {
			tile.rotateEngine();
			PlayerUtil.useWrench(player, x, y, z);
			return true;
		}

		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEngine tile = (TileEngine) world.getTileEntity(x, y, z);
		tile.rotateEngine();
		return true;
	}

}
