/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.genetics;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.genetics.IForestrySpeciesRoot;

public interface IBeeRoot extends IForestrySpeciesRoot<IBee> {

	/**
	 * @return true if passed item is a Forestry bee. Equal to getType(ItemStack stack) != null
	 */
	@Override
	boolean isMember(ItemStack stack);

	/* BREEDING TRACKER */

	/**
	 * @return {@link IApiaristTracker} associated with the passed world.
	 */
	@Override
	IApiaristTracker getBreedingTracker(IWorld world, @Nullable GameProfile player);

	/* BEE SPECIFIC */

	/**
	 * @return true if passed item is a drone. Equal to getType(ItemStack stack) == EnumBeeType.DRONE
	 */
	boolean isDrone(ItemStack stack);

	/**
	 * @return true if passed item is mated (i.e. a queen)
	 */
	boolean isMated(ItemStack stack);

	/**
	 * Creates an IBee suitable for a queen containing the necessary second genome for the mate.
	 *
	 * @param genome Valid {@link IGenome}
	 * @param mate   Valid {@link IBee} representing the mate.
	 * @return Mated {@link IBee} from the passed genomes.
	 */
	IBee getBee(World world, IGenome genome, IBee mate);

	/* GAME MODE */
	void resetBeekeepingMode();

	List<IBeekeepingMode> getBeekeepingModes();

	IBeekeepingMode getBeekeepingMode(World world);

	@Nullable
	IBeekeepingMode getBeekeepingMode(String name);

	void registerBeekeepingMode(IBeekeepingMode mode);

	void setBeekeepingMode(World world, IBeekeepingMode mode);

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
