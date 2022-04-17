/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IFarmLogic {

	int getFertilizerConsumption();

	int getWaterConsumption(float hydrationModifier);

	boolean isAcceptedResource(ItemStack itemstack);

	boolean isAcceptedGermling(ItemStack itemstack);

	Collection<ItemStack> collect();

	boolean cultivate(int x, int y, int z, FarmDirection direction, int extent);

	Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent);

	IFarmLogic setManual(boolean manual);

	@SideOnly(Side.CLIENT)
	IIcon getIcon();

	ResourceLocation getSpriteSheet();
	
	String getName();
}
