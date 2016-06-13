/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFarmLogic {

	int getFertilizerConsumption();

	int getWaterConsumption(float hydrationModifier);

	boolean isAcceptedResource(ItemStack itemstack);

	boolean isAcceptedGermling(ItemStack itemstack);

	Collection<ItemStack> collect(World world, IFarmHousing farmHousing);

	boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent);

	Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent);

	IFarmLogic setManual(boolean manual);
	
	@SideOnly(Side.CLIENT)
	ResourceLocation getTextureMap();
	
	String getName();
	
	/**
	 * @return the itemStack that represents this farm logic. Used as an icon for the farm logic.
	 */
	ItemStack getIconItemStack();
}
