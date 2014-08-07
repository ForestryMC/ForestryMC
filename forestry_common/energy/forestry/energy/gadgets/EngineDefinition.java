/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.gadgets.Engine;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.utils.Utils;

public class EngineDefinition extends MachineDefinition {

	public EngineDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		super(meta, teIdent, teClass, renderer, recipes);
	}

	@Override
	public boolean isSolidOnSide(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof Engine)
			return ((Engine) tile).getOrientation().getOpposite().ordinal() == side;
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fXplayerClick, float fY, float fZ) {

		if (player.isSneaking())
			return false;

		Engine tile = (Engine) world.getTileEntity(x, y, z);
		if (player.getCurrentEquippedItem() != null && Utils.canWrench(player, x, y, z)) {
			tile.rotateEngine();
			Utils.useWrench(player, x, y, z);
			return true;
		}

		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		Engine tile = (Engine) world.getTileEntity(x, y, z);
		tile.rotateEngine();
		return true;
	}

}
