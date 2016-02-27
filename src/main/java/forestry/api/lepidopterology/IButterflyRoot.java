/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;

public interface IButterflyRoot extends ISpeciesRoot<ButterflyChromosome> {

	@Override
	boolean isMember(ItemStack stack);

	@Override
	IButterfly getMember(ItemStack stack);

	@Override
	IButterfly getMember(NBTTagCompound compound);

	@Override
	ItemStack getMemberStack(IIndividual<ButterflyChromosome> butterfly, int type);

	/* GENOME CONVERSION */
	@Nonnull
	@Override
	IButterfly templateAsIndividual(ImmutableMap<ButterflyChromosome, IAllele> template);

	@Nonnull
	@Override
	IButterfly templateAsIndividual(ImmutableMap<ButterflyChromosome, IAllele> templateActive, ImmutableMap<ButterflyChromosome, IAllele> templateInactive);

	@Override
	IButterflyGenome templateAsGenome(ImmutableMap<ButterflyChromosome, IAllele> template);

	@Override
	IButterflyGenome templateAsGenome(ImmutableMap<ButterflyChromosome, IAllele> templateActive, ImmutableMap<ButterflyChromosome, IAllele> templateInactive);

	@Nonnull
	@Override
	IButterflyGenome chromosomesAsGenome(ImmutableMap<ButterflyChromosome, IChromosome> chromosomes);

	/* BUTTERFLY SPECIFIC */
	@Nonnull
	@Override
	IButterflyTracker getBreedingTracker(@Nonnull World world, @Nonnull GameProfile player);

	/**
	 * Spawns the given butterfly in the world.
	 * @param butterfly
	 * @return butterfly entity on success, null otherwise.
	 */
	EntityLiving spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z);

	/**
	 * @return true if passed item is mated.
	 */
	boolean isMated(ItemStack stack);

	/* TEMPLATES */
	@Override
	List<IButterfly> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	Collection<IButterflyMutation> getMutations(boolean shuffle);

	@Override
	EnumFlutterType getType(ItemStack stack);

	/** Modes */
	@Nonnull
	@Override
	IButterflyMode getMode(@Nonnull World world);

	@Nonnull
	@Override
	List<IButterflyMode> getModes();

	void registerMode(@Nonnull IButterflyMode mode);
}
