/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IToolGrafter {
	/**
	 * Called by leaves to determine the increase in sapling droprate.
	 * 
	 * @param stack ItemStack containing the grafter.
	 * @param world Minecraft world the player and the target block inhabit.
	 * @param x x-Coordinate of the broken leaf block.
	 * @param y y-Coordinate of the broken leaf block.
	 * @param z z-Coordinate of the broken leaf block.
	 * @return Float representing the factor the usual drop chance is to be multiplied by.
	 */
	float getSaplingModifier(ItemStack stack, World world, EntityPlayer player, int x, int y, int z);
}
