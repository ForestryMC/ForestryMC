/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IFruitFamily;

/**
 * Provides all information that is needed to spawn a fruit leaves / pod block in the world.
 */
public interface IFruitProvider {
	/**
	 * @return The fruit family of this fruit.
	 */
	IFruitFamily getFamily();

	/**
	 * Returns the color of the fruit spite based on the ripening time of the fruit.
	 *
	 * @param genome       The genome of the tree of the pod / leaves block.
	 * @param ripeningTime The ripening time of the leaves / pod block. From 0 to {@link #getRipeningPeriod()}.
	 */
	int getColour(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime);

	/**
	 * return the color to use for decorative leaves. Usually the ripe color.
	 */
	int getDecorativeColor();

	/**
	 * Determines if fruit block of this provider is considered a leaf block.
	 *
	 * @param genome The genome of the tree of the pod / leaves block.
	 * @param world  The world in that the pod / leaves block is located.
	 * @param pos    The position of the pod / leaves block.
	 * @return True if this provider provides a fruit leaf for the given genome at the given position.
	 */
	boolean isFruitLeaf(ITreeGenome genome, World world, BlockPos pos);

	/**
	 * The chance that this leaves contains fruits or the chance that a pod block spawns.
	 *
	 * @param genome The genome of the tree of the pod / leaves block.
	 * @return The chance that this leaves contains fruits or the chance that a pod block spawns.
	 */
	default float getFruitChance(ITreeGenome genome, World world, BlockPos pos) {
		ITreeRoot treeRoot = TreeManager.treeRoot;
		if (treeRoot == null) {
			return 0.0F;
		}
		float yieldModifier = treeRoot.getTreekeepingMode(world).getYieldModifier(genome, 1.0F);
		return genome.getYield() * yieldModifier * 2.5F;
	}

	/**
	 * @return How many successful ripening block ticks a fruit needs to be ripe.
	 */
	int getRipeningPeriod();

	/**
	 * A unmodifiable map that contains all products and their associated drop chances.
	 *
	 * @return A unmodifiable map that contains all products and their associated drop chances.
	 */
	Map<ItemStack, Float> getProducts();

	/**
	 * A unmodifiable map that contains all specialties and their associated drop chances.
	 *
	 * @return A unmodifiable map that contains all products and their associated drop chances.
	 */
	Map<ItemStack, Float> getSpecialty();

	/**
	 * Returns all drops of this block if you harvest it.
	 *
	 * @param genome       The genome of the tree of the leaves / pod.
	 * @param ripeningTime The repining time of the block. From 0 to {@link #getRipeningPeriod()}.
	 */
	NonNullList<ItemStack> getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime);

	/**
	 * @return Short, human-readable identifier used in the treealyzer.
	 */
	String getDescription();

	/**
	 * @return The location of the pod model in the "modid:pods/" folder.
	 */
	@Nullable
	String getModelName();

	/**
	 * @return The mod id of that adds this fruit provider. Needed for the allele of this fruit.
	 */
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

	/**
	 * Can be used to register the sprite/s that can be returned with
	 * {@link #getSprite(ITreeGenome, IBlockAccess, BlockPos, int)}.
	 */
	@SideOnly(Side.CLIENT)
	void registerSprites();
}
