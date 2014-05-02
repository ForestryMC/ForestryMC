/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
