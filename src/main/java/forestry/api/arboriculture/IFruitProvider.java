/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import forestry.api.genetics.IFruitFamily;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IFruitProvider {
	IFruitFamily getFamily();

	int getColour(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime);

	/**
	 * return the color to use for decorative leaves. Usually the ripe color.
	 */
	int getDecorativeColor();

	boolean isFruitLeaf(ITreeGenome genome, World world, BlockPos pos);

	int getRipeningPeriod();

	// / Products, Chance
	Map<ItemStack, Float> getProducts();

	// / Specialty, Chance
	Map<ItemStack, Float> getSpecialty();

	NonNullList<ItemStack> getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime);

	/**
	 * @return Short, human-readable identifier used in the treealyzer.
	 */
	String getDescription();

	@Nullable
	String getModelName();

	String getModID();

	/* TEXTURE OVERLAY */

	/**
	 * @param ripeningTime Elapsed ripening time for the fruit.
	 * @return ResourceLocation of the texture to overlay on the leaf block.
	 */
	@Nullable
	ResourceLocation getSprite(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime);

	/**
	 * return the ResourceLocation to display on decorative leaves
	 */
	@Nullable
	ResourceLocation getDecorativeSprite();

	/**
	 * @return true if this fruit provider requires fruit blocks to spawn, false otherwise.
	 */
	boolean requiresFruitBlocks();

	/**
	 * Tries to spawn a fruit block at the potential position when the tree generates.
	 * Spawning a fruit has a random chance of success based on {@link ITreeGenome#getSappiness()}
	 *
	 * @return true if a fruit block was spawned, false otherwise.
	 */
	boolean trySpawnFruitBlock(ITreeGenome genome, World world, Random rand, BlockPos pos);

	@SideOnly(Side.CLIENT)
	void registerSprites();
}
