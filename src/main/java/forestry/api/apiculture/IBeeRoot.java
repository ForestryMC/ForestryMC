/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.ISpeciesRoot;

public interface IBeeRoot extends ISpeciesRoot<BeeChromosome> {

	/**
	 * @return true if passed item is a Forestry bee. Equal to getType(ItemStack stack) != EnumBeeType.NONE
	 */
	@Override
	boolean isMember(ItemStack stack);

	/**
	 * @return {@link IBee} pattern parsed from the passed stack's nbt data.
	 */
	@Override
	IBee getMember(ItemStack stack);

	@Override
	IBee getMember(NBTTagCompound compound);

	/* GENOME CONVERSION */
	@Nonnull
	@Override
	IBee templateAsIndividual(ImmutableMap<BeeChromosome, IAllele> template);

	@Nonnull
	@Override
	IBee templateAsIndividual(ImmutableMap<BeeChromosome, IAllele> templateActive, ImmutableMap<BeeChromosome, IAllele> templateInactive);

	@Override
	IBeeGenome templateAsGenome(ImmutableMap<BeeChromosome, IAllele> template);

	@Override
	IBeeGenome templateAsGenome(ImmutableMap<BeeChromosome, IAllele> templateActive, ImmutableMap<BeeChromosome, IAllele> templateInactive);

	@Nonnull
	@Override
	IBeeGenome chromosomesAsGenome(ImmutableMap<BeeChromosome, IChromosome> chromosomes);

	/* BREEDING TRACKER */

	/**
	 * @param world
	 * @return {@link IApiaristTracker} associated with the passed world.
	 */
	@Override
	@Nonnull
	IApiaristTracker getBreedingTracker(@Nonnull World world, @Nonnull GameProfile player);

	/* BEE SPECIFIC */

	/**
	 * @return type of bee encoded on the itemstack. EnumBeeType.NONE if it isn't a bee.
	 */
	@Override
	EnumBeeType getType(ItemStack stack);

	/**
	 * @return true if passed item is a drone. Equal to getType(ItemStack stack) == EnumBeeType.DRONE
	 */
	boolean isDrone(ItemStack stack);

	/**
	 * @return true if passed item is mated (i.e. a queen)
	 */
	boolean isMated(ItemStack stack);

	/**
	 * @param genome
	 *            Valid {@link IBeeGenome}
	 * @return {@link IBee} from the passed genome
	 */
	IBee getBee(World world, IBeeGenome genome);

	/**
	 * Creates an IBee suitable for a queen containing the necessary second genome for the mate.
	 *
	 * @param genome
	 *            Valid {@link IBeeGenome}
	 * @param mate
	 *            Valid {@link IBee} representing the mate.
	 * @return Mated {@link IBee} from the passed genomes.
	 */
	IBee getBee(World world, IBeeGenome genome, IBee mate);

	/* TEMPLATES */
	@Override
	List<IBee> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	Collection<IBeeMutation> getMutations(boolean shuffle);

	/* GAME MODE */
	void registerMode(@Nonnull IBeekeepingMode mode);

	@Nonnull
	@Override
	List<IBeekeepingMode> getModes();

	@Nonnull
	@Override
	IBeekeepingMode getMode(@Nonnull World world);

	@Nonnull
	@Override
	IBeekeepingMode getMode(@Nonnull String name);

	/* MISC */

	/**
	 * Creates beekeepingLogic for a housing.
	 * Should be used when the housing is created, see IBeekeepingLogic
	 */
	IBeekeepingLogic createBeekeepingLogic(IBeeHousing housing);

	/**
	 * Combines multiple modifiers from an IBeeHousing into one.
	 * Stays up to date with changes to the housing's modifiers.
	 */
	IBeeModifier createBeeHousingModifier(IBeeHousing housing);

	/**
	 * Combines multiple listeners from an IBeeHousing into one.
	 * Stays up to date with changes to the housing's listeners.
	 */
	IBeeListener createBeeHousingListener(IBeeHousing housing);

}
