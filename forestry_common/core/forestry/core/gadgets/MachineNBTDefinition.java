/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class MachineNBTDefinition extends MachineDefinition {

	public MachineNBTDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IRecipe... recipes) {
		super(meta, teIdent, teClass, recipes);
	}

	public MachineNBTDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		super(meta, teIdent, teClass, renderer, recipes);
	}

	/* BLOCK DROPS */
	@Override
	public boolean handlesDrops() {
		return true;
	}

	/* INTERACTION */
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (Proxies.common.isSimulating(world) && block.canHarvestBlock(player, meta)) {
			TileForestry tile = (TileForestry)world.getTileEntity(x, y, z);
			Utils.dropInventory(tile, world, x, y, z);

			ItemStack stack = new ItemStack(block, 1, meta);
			NBTTagCompound compound = new NBTTagCompound();
			tile.writeToNBT(compound);
			stack.setTagCompound(compound);
			StackUtils.dropItemStackAsEntity(stack, world, x, y, z);
		}

		return world.setBlockToAir(x, y, z);
	}

}
