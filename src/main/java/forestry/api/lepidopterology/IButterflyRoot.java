/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.ISpeciesRoot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IButterflyRoot extends ISpeciesRoot {

	@Override
	boolean isMember(ItemStack stack);

	@Override
	IButterfly getMember(ItemStack stack);

	@Override
	IButterfly getMember(NBTTagCompound compound);

	/* GENOME CONVERSION */
	@Override
	IButterfly templateAsIndividual(IAllele[] template);

	@Override
	IButterfly templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive);

	@Override
	IButterflyGenome templateAsGenome(IAllele[] template);

	@Override
	IButterflyGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive);

	/* BUTTERFLY SPECIFIC */
	@Override
	ILepidopteristTracker getBreedingTracker(World world, GameProfile player);

	/**
	 * Spawns the given butterfly in the world.
	 * @param butterfly
	 * @return butterfly entity on success, null otherwise.
	 */
	EntityLiving spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z);
	
	boolean plantCocoon(World world, IButterflyNursery nursery, GameProfile owner, int age);

	/**
	 * @return true if passed item is mated.
	 */
	boolean isMated(ItemStack stack);

	/* TEMPLATES */
	@Override
	ArrayList<IButterfly> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	List<IButterflyMutation> getMutations(boolean shuffle);

	@Nullable
	@Override
	EnumFlutterType getType(ItemStack stack);

}
