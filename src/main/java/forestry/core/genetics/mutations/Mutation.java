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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.climate.IClimateProvider;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IMutationBuilder;
import forestry.api.genetics.IMutationCondition;

public abstract class Mutation implements IMutation, IMutationBuilder {

	private final int chance;

	private final IAlleleSpecies species0;
	private final IAlleleSpecies species1;

	private final IAllele[] template;

	private final List<IMutationCondition> mutationConditions = new ArrayList<>();
	private final List<String> specialConditions = new ArrayList<>();

	private boolean isSecret = false;

	protected Mutation(IAlleleSpecies species0, IAlleleSpecies species1, IAllele[] template, int chance) {
		this.species0 = species0;
		this.species1 = species1;
		this.template = template;
		this.chance = chance;
	}

	@Override
	public Collection<String> getSpecialConditions() {
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
	public Mutation restrictBiomeType(BiomeDictionary.Type... types) {
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
	public Mutation requireResource(IBlockState... acceptedBlockStates) {
		IMutationCondition mutationCondition = new MutationConditionRequiresResource(acceptedBlockStates);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation requireResource(String oreName) {
		IMutationCondition mutationCondition = new MutationConditionRequiresResource(oreName);
		return addMutationCondition(mutationCondition);
	}

	@Override
	public Mutation addMutationCondition(IMutationCondition mutationCondition) {
		mutationConditions.add(mutationCondition);
		specialConditions.add(mutationCondition.getDescription());
		return this;
	}

	protected float getChance(World world, BlockPos pos, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
		float mutationChance = chance;
		for (IMutationCondition mutationCondition : mutationConditions) {
			mutationChance *= mutationCondition.getChance(world, pos, allele0, allele1, genome0, genome1, climate);
			if (mutationChance == 0) {
				return 0;
			}
		}
		return mutationChance;
	}

	@Override
	public IAlleleSpecies getAllele0() {
		return species0;
	}

	@Override
	public IAlleleSpecies getAllele1() {
		return species1;
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
		return species0.getUID().equals(allele.getUID()) || species1.getUID().equals(allele.getUID());
	}

	@Override
	public IAllele getPartner(IAllele allele) {
		if (species0.getUID().equals(allele.getUID())) {
			return species1;
		} else if (species1.getUID().equals(allele.getUID())) {
			return species0;
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
			.add("first", species0)
			.add("second", species1)
			.add("result", template[0]);
		if (!specialConditions.isEmpty()) {
			stringHelper.add("conditions", getSpecialConditions());
		}
		return stringHelper.toString();
	}
}
