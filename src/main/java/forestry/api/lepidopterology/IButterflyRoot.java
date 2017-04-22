/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import javax.annotation.Nullable;
import java.util.List;

import com.mojang.authlib.GameProfile;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.ISpeciesRoot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IButterflyRoot extends ISpeciesRoot {

	@Override
	boolean isMember(ItemStack stack);

	@Override
	@Nullable
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
	ILepidopteristTracker getBreedingTracker(World world, @Nullable GameProfile player);

	/**
	 * Spawns the given butterfly in the world.
	 *
	 * @return butterfly entity on success, null otherwise.
	 */
	EntityLiving spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z);

	BlockPos plantCocoon(World world, BlockPos pos, IButterfly caterpillar, GameProfile owner, int age, boolean createNursery);

	/**
	 * @return true if passed item is mated.
	 */
	boolean isMated(ItemStack stack);

	/* TEMPLATES */
	@Override
	List<IButterfly> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	List<IButterflyMutation> getMutations(boolean shuffle);

	@Nullable
	@Override
	EnumFlutterType getType(ItemStack stack);

}
