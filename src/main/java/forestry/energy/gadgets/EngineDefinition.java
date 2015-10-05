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
package forestry.energy.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.core.gadgets.Engine;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.utils.Utils;

public class EngineDefinition extends MachineDefinition {

	public EngineDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		super(meta, teIdent, teClass, renderer, recipes);
	}
	
	@Override
	public boolean isSolidOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof Engine) {
			return ((Engine) tile).getOrientation().getOpposite() == side;
		}
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
		if (player.isSneaking()) {
			return false;
		}

		Engine tile = (Engine) world.getTileEntity(pos);
		if (Utils.canWrench(player, pos)) {
			tile.rotateEngine();
			Utils.useWrench(player, pos);
			return true;
		}

		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		Engine tile = (Engine) world.getTileEntity(pos);
		tile.rotateEngine();
		return true;
	}

}
