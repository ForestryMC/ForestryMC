/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.genetics.mutations;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IMutationBuilder;
import forestry.api.genetics.IMutationCondition;
import forestry.api.genetics.ISpeciesMode;
import forestry.api.genetics.ISpeciesRoot;

public abstract class Mutation<C extends IChromosomeType<C>> implements IMutation<C>, IMutationBuilder<C> {

	private final int chance;

	@Nonnull
	private final IAlleleSpecies<C> species0;
	@Nonnull
	private final IAlleleSpecies<C> species1;
	@Nonnull
	private final ImmutableMap<C, IAllele> resultTemplate;

	@Nonnull
	private final List<IMutationCondition> mutationConditions = new ArrayList<>();
	@Nonnull
	private final List<String> specialConditions = new ArrayList<>();

	private boolean isSecret = false;

	protected Mutation(@Nonnull IAlleleSpecies<C> species0, @Nonnull IAlleleSpecies<C> species1, @Nonnull ImmutableMap<C, IAllele> resultTemplate, int chance) {
		this.species0 = species0;
		this.species1 = species1;
		this.resultTemplate = resultTemplate;
		this.chance = chance;
	}

	@Nullable
	public static IMutation<?> create(@Nonnull NBTTagCompound nbt) {
		IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;

		ISpeciesRoot<?> root = alleleRegistry.getSpeciesRoot(nbt.getString("ROT"));
		IAllele allele0 = alleleRegistry.getAllele(nbt.getString("AL0"));
		IAllele allele1 = alleleRegistry.getAllele(nbt.getString("AL1"));
		IAllele result = AlleleManager.alleleRegistry.getAllele(nbt.getString("RST"));
		if (root == null || allele0 == null || allele1 == null || result == null) {
			return null;
		}

		IChromosomeType<?> speciesChromosomeType = root.getKaryotypeKey();
		for (IMutation<?> mutation : root.getCombinations(allele0)) {
			if (mutation.isPartner(allele1)) {
				String mutationSpeciesUid = mutation.getResultTemplate().get(speciesChromosomeType).getUID();
				if (mutationSpeciesUid.equals(result.getUID())) {
					return mutation;
				}
			}
		}

		return null;
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbt) {
		ISpeciesRoot<C> speciesRoot = getRoot();
		nbt.setString("ROT", speciesRoot.getUID());
		nbt.setString("AL0", getSpecies0().getUID());
		nbt.setString("AL1", getSpecies1().getUID());
		nbt.setString("RST", getResultTemplate().get(speciesRoot.getKaryotypeKey()).getUID());
	}

	@Nonnull
	@Override
	public Collection<String> getSpecialConditions() {
		return specialConditions;
	}

	@Override
	public Mutation<C> setIsSecret() {
		isSecret = true;
		return this;
	}

	@Override
	public Mutation<C> restrictTemperature(EnumTemperature temperature) {
		return restrictTemperature(temperature, temperature);
	}

	@Override
	public Mutation<C> restrictTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature) {
		IMutationCondition mutationCondition = new MutationConditionTemperature(minTemperature, maxTemperature);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> restrictHumidity(EnumHumidity humidity) {
		return restrictHumidity(humidity, humidity);
	}

	@Override
	public Mutation<C> restrictHumidity(EnumHumidity minHumidity, EnumHumidity maxHumidity) {
		IMutationCondition mutationCondition = new MutationConditionHumidity(minHumidity, maxHumidity);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> restrictBiomeType(BiomeDictionary.Type... types) {
		IMutationCondition mutationCondition = new MutationConditionBiome(types);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> requireDay() {
		IMutationCondition mutationCondition = new MutationConditionDaytime(true);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> requireNight() {
		IMutationCondition mutationCondition = new MutationConditionDaytime(false);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> restrictDateRange(int startMonth, int startDay, int endMonth, int endDay) {
		IMutationCondition mutationCondition = new MutationConditionTimeLimited(startMonth, startDay, endMonth, endDay);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> requireResource(Block block, int meta) {
		IMutationCondition mutationCondition = new MutationConditionRequiresResource(block, meta);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> requireResource(String oreName) {
		IMutationCondition mutationCondition = new MutationConditionRequiresResourceOreDict(oreName);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation<C> addMutationCondition(IMutationCondition mutationCondition) {
		mutationConditions.add(mutationCondition);
		specialConditions.add(mutationCondition.getDescription());
		return this;
	}

	@Override
	public float getChance(World world, BlockPos pos, IAlleleSpecies<C> species0, IAlleleSpecies<C> species1, IGenome<C> genome0, IGenome<C> genome1) {
		float mutationChance = chance;
		for (IMutationCondition mutationCondition : mutationConditions) {
			mutationChance *= mutationCondition.getChance(world, pos, species0, species1, genome0, genome1);
		}

		ISpeciesMode<C> mode = getRoot().getMode(world);
		mutationChance *= mode.getMutationModifier(genome0, genome1);

		return mutationChance;
	}

	@Nonnull
	@Override
	public IAlleleSpecies<C> getSpecies0() {
		return species0;
	}

	@Nonnull
	@Override
	public IAlleleSpecies<C> getSpecies1() {
		return species1;
	}

	@Override
	public float getBaseChance() {
		return chance;
	}

	@Nonnull
	@Override
	public ImmutableMap<C, IAllele> getResultTemplate() {
		return resultTemplate;
	}

	@Override
	public boolean isPartner(IAllele allele) {
		return species0.getUID().equals(allele.getUID()) || species1.getUID().equals(allele.getUID());
	}

	@Nullable
	@Override
	public IAllele getPartner(IAllele allele) {
		if (species0.getUID().equals(allele.getUID())) {
			return species1;
		} else if (species1.getUID().equals(allele.getUID())) {
			return species0;
		} else {
			return null;
		}
	}

	@Override
	public boolean isSecret() {
		return isSecret;
	}
}
