/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.ISpeciesRoot;

public interface ITreeRoot extends ISpeciesRoot<TreeChromosome> {

	@Override
	boolean isMember(ItemStack itemstack);

	@Override
	ITree getMember(ItemStack itemstack);

	@Override
	ITree getMember(NBTTagCompound compound);

	@Nonnull
	@Override
	ITree templateAsIndividual(ImmutableMap<TreeChromosome, IAllele> template);

	@Nonnull
	@Override
	ITree templateAsIndividual(ImmutableMap<TreeChromosome, IAllele> templateActive, ImmutableMap<TreeChromosome, IAllele> templateInactive);

	@Override
	ITreeGenome templateAsGenome(ImmutableMap<TreeChromosome, IAllele> template);

	@Override
	ITreeGenome templateAsGenome(ImmutableMap<TreeChromosome, IAllele> templateActive, ImmutableMap<TreeChromosome, IAllele> templateInactive);

	@Nonnull
	@Override
	ITreeGenome chromosomesAsGenome(ImmutableMap<TreeChromosome, IChromosome> chromosomes);

	/**
	 * @return {@link IArboristTracker} associated with the passed world.
	 */
	@Nonnull
	@Override
	IArboristTracker getBreedingTracker(@Nonnull World world, @Nonnull GameProfile player);

	/* TREE SPECIFIC */

	/**
	 * Register a leaf tick handler.
	 * @param handler the {@link ILeafTickHandler} to register.
	 */
	void registerLeafTickHandler(ILeafTickHandler handler);

	Collection<ILeafTickHandler> getLeafTickHandlers();

	/**
	 * @return type of tree encoded on the itemstack. EnumGermlingType.NONE if it isn't a tree.
	 */
	@Override
	EnumGermlingType getType(ItemStack stack);

	ITree getTree(World world, BlockPos pos);

	ITree getTree(World world, ITreeGenome genome);

	boolean plantSapling(World world, ITree tree, GameProfile owner, BlockPos pos);

	@Override
	ImmutableMap<TreeChromosome, IChromosome> templateAsChromosomes(ImmutableMap<TreeChromosome, IAllele> template);

	@Override
	ImmutableMap<TreeChromosome, IChromosome> templateAsChromosomes(ImmutableMap<TreeChromosome, IAllele> templateActive, ImmutableMap<TreeChromosome, IAllele> templateInactive);

	boolean setFruitBlock(World world, IAlleleFruit allele, float sappiness, short[] indices, BlockPos pos);

	/* GAME MODE */
	@Nonnull
	@Override
	List<ITreekeepingMode> getModes();

	@Nonnull
	@Override
	ITreekeepingMode getMode(@Nonnull World world);

	@Nonnull
	@Override
	ITreekeepingMode getMode(@Nonnull String name);

	void registerMode(@Nonnull ITreekeepingMode mode);

	/* TEMPLATES */
	@Override
	ArrayList<ITree> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	Collection<ITreeMutation> getMutations(boolean shuffle);

}
