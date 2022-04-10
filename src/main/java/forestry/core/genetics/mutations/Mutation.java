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

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.climate.IClimateProvider;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IMutationBuilder;
import forestry.api.genetics.IMutationCondition;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IGenome;
import genetics.api.mutation.IMutation;

public abstract class Mutation implements IMutation, IMutationBuilder {

	private final int chance;
	private final IAlleleForestrySpecies firstParent;
	private final IAlleleForestrySpecies secondParent;

	private final IAllele[] template;

	private final List<IMutationCondition> mutationConditions = new ArrayList<>();
	private final List<ITextComponent> specialConditions = new ArrayList<>();

	private boolean isSecret = false;

	protected Mutation(IAlleleForestrySpecies firstParent, IAlleleForestrySpecies secondParent, IAllele[] template, int chance) {
		this.firstParent = firstParent;
		this.secondParent = secondParent;
		this.template = template;
		this.chance = chance;
	}

	@Override
	public Collection<ITextComponent> getSpecialConditions() {
		return specialConditions;
	}

	@Override
	public Mutation setIsSecret() {
		isSecret = true;
		return this;
	}

	@Override
	public Mutation restrictTemperature(EnumTemperature temperature) {
		return restrictTemperature(temperature, temperature);
	}

	@Override
	public Mutation restrictTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature) {
		IMutationCondition mutationCondition = new MutationConditionTemperature(minTemperature, maxTemperature);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation restrictHumidity(EnumHumidity humidity) {
		return restrictHumidity(humidity, humidity);
	}

	@Override
	public Mutation restrictHumidity(EnumHumidity minHumidity, EnumHumidity maxHumidity) {
		IMutationCondition mutationCondition = new MutationConditionHumidity(minHumidity, maxHumidity);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation restrictBiomeType(Biome.Category... types) {
		IMutationCondition mutationCondition = new MutationConditionBiome(types);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation requireDay() {
		IMutationCondition mutationCondition = new MutationConditionDaytime(true);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation requireNight() {
		IMutationCondition mutationCondition = new MutationConditionDaytime(false);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation restrictDateRange(int startMonth, int startDay, int endMonth, int endDay) {
		IMutationCondition mutationCondition = new MutationConditionTimeLimited(startMonth, startDay, endMonth, endDay);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation requireResource(BlockState... acceptedBlockStates) {
		IMutationCondition mutationCondition = new MutationConditionRequiresResource(acceptedBlockStates);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation addMutationCondition(IMutationCondition mutationCondition) {
		mutationConditions.add(mutationCondition);
		specialConditions.add(mutationCondition.getDescription());
		return this;
	}

	protected float getChance(World world, BlockPos pos, IAllele firstParent, IAllele secondParent, IGenome firstGenome, IGenome secondGenome, IClimateProvider climate) {
		float mutationChance = chance;
		for (IMutationCondition mutationCondition : mutationConditions) {
			mutationChance *= mutationCondition.getChance(world, pos, firstParent, secondParent, firstGenome, secondGenome, climate);
			if (mutationChance == 0) {
				return 0;
			}
		}
		return mutationChance;
	}

	@Override
	public IAlleleSpecies getFirstParent() {
		return firstParent;
	}

	@Override
	public IAlleleSpecies getSecondParent() {
		return secondParent;
	}

	@Override
	public IAlleleSpecies getResultingSpecies() {
		return (IAlleleSpecies) template[0];//ToDo: More testing ?
	}

	@Override
	public float getBaseChance() {
		return chance;
	}

	@Override
	public IAllele[] getTemplate() {
		return template;
	}

	@Override
	public boolean isPartner(IAllele allele) {
		return firstParent.getRegistryName().equals(allele.getRegistryName()) || secondParent.getRegistryName().equals(allele.getRegistryName());
	}

	@Override
	public IAllele getPartner(IAllele allele) {
		if (firstParent.getRegistryName().equals(allele.getRegistryName())) {
			return secondParent;
		} else if (secondParent.getRegistryName().equals(allele.getRegistryName())) {
			return firstParent;
		} else {
			throw new IllegalArgumentException("Tried to get partner for allele that is not part of this mutation.");
		}
	}

	@Override
	public boolean isSecret() {
		return isSecret;
	}

	@Override
	public String toString() {
		MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(this)
			.add("first", firstParent)
			.add("second", secondParent)
			.add("result", template[0]);
		if (!specialConditions.isEmpty()) {
			stringHelper.add("conditions", getSpecialConditions());
		}
		return stringHelper.toString();
	}
}
