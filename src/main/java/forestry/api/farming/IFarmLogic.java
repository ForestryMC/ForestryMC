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

/**
 * The IFarmLogic is used by farm blocks and multi-blocks to cultivate and harvest crops and plants.
 *
 * Every farm block has only ony one logic a multi-block farm has four, one for every direction.
 */
public interface IFarmLogic {

	/**
	 * @return The amount of fertilizer that the {@link IFarmHousing} automatically removes after this logic cultivated
	 * a block or harvested a crop.
	 */
	int getFertilizerConsumption();

	/**
	 * @param hydrationModifier A modifier that depends on the weather and the biome of the farm.
	 *
	 * @return The amount of water that the {@link IFarmHousing} automatically removes after this logic cultivated
	 * a block or harvested a crop.
	 */
	int getWaterConsumption(float hydrationModifier);

	/**
	 * Checks if the given stack is a resource or a soil that this logic uses to grow plants on or to create other
	 * products like peat.
	 */
	boolean isAcceptedResource(ItemStack itemstack);

	/**
	 * Checks if the given stack is a germling (plantable sapling, seed, etc.) for any {@link IFarmable} of this farm.
	 */
	boolean isAcceptedGermling(ItemStack itemstack);

	/**
	 * Collects all items that are laying on the ground and are in the {@link IFarmHousing#getArea()} of the farm.
	 *
	 * @param world The world of the farm.
	 * @param farmHousing The farm that uses this logic.
	 *
	 * @return A collection that contains all items that were collected.
	 */
	NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing);

	/**
	 * Tries to cultivate one or more blocks at the given position and with the given extent.
	 *
	 * @param world The world of the farm.
	 * @param farmHousing The farm that uses this logic.
	 * @param pos The position at that the logic should start to cultivate.
	 * @param direction The direction of the extension.
	 * @param extent How many blocks this logic has to cultivate after it cultivated the block at the given position.
	 *               The positions of the next blocks are having a offset in the given direction.
	 * @return True if the logic has cultivated any block.
	 */
	boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent);

	/**
	 * Tries to harvest one or more blocks at the given position and with the given extent.
	 *
	 * @param world The world of the farm.
	 * @param pos The position at that the logic should start to harvest.
	 * @param direction The direction of the extension.
	 * @param extent How many blocks this logic tries to harvest after it has tried to harvested the block at the given position.
	 *               The positions of the next blocks are having a offset in the given direction.
	 * @return True if the logic has cultivated any block.
	 */
	Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent);

	/**
	 * Returns the {@link IFarmProperties} that created this logic.
	 *
	 * @since Forestry 5.8
	 *
	 * @return Returns the {@link IFarmProperties} that created this logic. Returns a fake instance from
	 * {@link IFarmRegistry#createFakeInstance(IFarmLogic)} if the logic is older that Forestry 5.8.
	 * TODO: Remove "default" from this method in 1.13
	 */
	default IFarmProperties getProperties(){
		return ForestryAPI.farmRegistry.createFakeInstance(this);
	}

	/**
	 * @deprecated Since Forestry 5.8 logic instances are created at the constructor of the {@link IFarmProperties} and
	 * have a immutable manual state. TODO Remove this method in 1.13
	 */
	@Deprecated
	default IFarmLogic setManual(boolean manual){
		return this;
	}

	/**
	 * Use {@link IFarmRegistry#getProperties(String)} to get the {@link IFarmProperties} for the farm logic and register the soil
	 * with {@link IFarmProperties#registerSoil(ItemStack, IBlockState, boolean)}.
	 */
	@Deprecated
	default void addSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData){
		//
	}

	default String getUnlocalizedName(){
		return "";
	}

	/**
	 * @return Localized short, human-readable identifier used in the farm gui and in tooltips.
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
