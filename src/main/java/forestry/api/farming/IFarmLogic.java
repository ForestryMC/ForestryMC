/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;

public interface IFarmLogic {

	/**
	 * @return The amount of fertilizer that the {@link IFarmHousing} automatically removes after this logic cultivated
	 * a block or harvested a crop.
	 */
	int getFertilizerConsumption();

	/**
	 * @return The amount of water that the {@link IFarmHousing} automatically removes after this logic cultivated
	 * a block or harvested a crop.
	 */
	int getWaterConsumption(float hydrationModifier);

	boolean isAcceptedResource(ItemStack itemstack);

	boolean isAcceptedGermling(ItemStack itemstack);

	NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing);

	boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent);

	Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent);

	/**
	 * @deprecated Since Forestry 5.8 logic instances are created at the constructor of the {@link IFarmProperties} and
	 * have a immutable manual state. TODO Remove this method in 1.13
	 */
	@Deprecated
	default IFarmLogic setManual(boolean manual){
		return this;
	}

	/**
	 * @since Forestry 5.8
	 *
	 * @return Returns the {@link IFarmProperties} that created this logic. Returns a fake instance from
	 * {@link IFarmRegistry#createFakeInstance(IFarmLogic)} if the logic is older that Forestry 5.8.
	 */
	default IFarmProperties getInstance(){
		return ForestryAPI.farmRegistry.createFakeInstance(this);
	}

	/**
	 * Use {@link IFarmRegistry#getProperties(String)} to get the {@link IFarmProperties} for the farm logic and register the soil
	 * with {@link IFarmProperties#registerSoil(ItemStack, IBlockState, boolean)}.
	 */
	@Deprecated
	default void addSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData){
		//
	}

	/**
	 * @return A short localized name for this logic.
	 */
	String getName();

	/* GUI ONLY */
	/**
	 * @deprecated No longer needed because mc always uses the same map for blocks and items.
	 * TODO remove this method in 1.13
	 */
	@Deprecated
	@SideOnly(Side.CLIENT)
	default ResourceLocation getTextureMap(){
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	/**
	 * @return the itemStack that represents this farm logic. Used as an icon for the farm logic.
	 */
	ItemStack getIconItemStack();
}
