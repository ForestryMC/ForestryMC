/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFarmLogic {

	int getFertilizerConsumption();

	int getWaterConsumption(float hydrationModifier);

	boolean isAcceptedResource(ItemStack itemstack);

	boolean isAcceptedGermling(ItemStack itemstack);

	Collection<ItemStack> collect();

	boolean cultivate(BlockPos pos, FarmDirection direction, int extent);

	Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent);

	IFarmLogic setManual(boolean manual);
	
	@SideOnly(Side.CLIENT)
	ResourceLocation getTextureMap();
	
	String getName();
	
	@SideOnly(Side.CLIENT)
	ItemStack getStack();
}
