/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.ISpeciesRootPollinatable;

public interface ITreeRoot extends ISpeciesRootPollinatable {

	@Override
	boolean isMember(ItemStack itemstack);

	@Override
	@Nullable
	ITree getMember(ItemStack itemstack);

	@Override
	ITree getMember(NBTTagCompound compound);

	@Override
	ITree templateAsIndividual(IAllele[] template);

	@Override
	ITree templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive);

	@Override
	ITreeGenome templateAsGenome(IAllele[] template);

	@Override
	ITreeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive);

	/**
	 * @return {@link IArboristTracker} associated with the passed world.
	 */
	@Override
	IArboristTracker getBreedingTracker(World world, @Nullable GameProfile player);

	/* TREE SPECIFIC */

	/**
	 * Register a leaf tick handler.
	 *
	 * @param handler the {@link ILeafTickHandler} to register.
	 */
	void registerLeafTickHandler(ILeafTickHandler handler);

	Collection<ILeafTickHandler> getLeafTickHandlers();

	/**
	 * @return type of tree encoded on the itemstack. EnumGermlingType.NONE if it isn't a tree.
	 */
	@Nullable
	@Override
	EnumGermlingType getType(ItemStack stack);

	@Nullable
	ITree getTree(World world, BlockPos pos);

	ITree getTree(World world, ITreeGenome genome);

	boolean plantSapling(World world, ITree tree, GameProfile owner, BlockPos pos);

	@Override
	IChromosome[] templateAsChromosomes(IAllele[] template);

	@Override
	IChromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive);

	boolean setFruitBlock(World world, ITreeGenome genome, IAlleleFruit allele, float sappiness, BlockPos pos);

	/* GAME MODE */
	List<ITreekeepingMode> getTreekeepingModes();

	ITreekeepingMode getTreekeepingMode(World world);

	@Nullable
	ITreekeepingMode getTreekeepingMode(String name);

	void registerTreekeepingMode(ITreekeepingMode mode);

	void setTreekeepingMode(World world, ITreekeepingMode mode);

	/* TEMPLATES */

	@Override
	List<ITree> getIndividualTemplates();

	/* MUTATIONS */
	@Override
	List<ITreeMutation> getMutations(boolean shuffle);

	Collection<IFruitProvider> getFruitProvidersForFruitFamily(IFruitFamily fruitFamily);
}
