/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.ISpeciesRoot;

public interface IBeeRoot extends ISpeciesRoot {

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
	@Override
	IBee templateAsIndividual(IAllele[] template);

	@Override
	IBee templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive);

	@Override
	IBeeGenome templateAsGenome(IAllele[] template);

	@Override
	IBeeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive);

	/* BREEDING TRACKER */
	/**
	 * @param world
	 * @return {@link IApiaristTracker} associated with the passed world.
	 */
	IApiaristTracker getBreedingTracker(World world, GameProfile player);

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
	ArrayList<IBee> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	Collection<IBeeMutation> getMutations(boolean shuffle);

	/* GAME MODE */
	void resetBeekeepingMode();

	ArrayList<IBeekeepingMode> getBeekeepingModes();

	IBeekeepingMode getBeekeepingMode(World world);

	IBeekeepingMode getBeekeepingMode(String name);

	void registerBeekeepingMode(IBeekeepingMode mode);

	void setBeekeepingMode(World world, String name);

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
