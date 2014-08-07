/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.genetics;

import java.util.ArrayList;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.core.genetics.Mutation;
import forestry.plugins.PluginApiculture;

public class BeeMutation extends Mutation implements IBeeMutation {

	private final IBeeRoot root;

	boolean requiresDay = false;
	boolean requiresNight = false;

	private final ArrayList<BiomeDictionary.Type> restrictBiomeTypes = new ArrayList<BiomeDictionary.Type>();
	private boolean strictBiomeCheck = false;

	public BeeMutation(IAllele allele0, IAllele allele1, IAllele[] template, int chance) {
		super(allele0, allele1, template, chance);

		root = (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		PluginApiculture.beeInterface.registerMutation(this);
	}

	@Override
	public IBeeRoot getRoot() {
		return root;
	}

	public BeeMutation enableStrictBiomeCheck() {
		strictBiomeCheck = true;
		return this;
	}

	public BeeMutation restrictBiomeType(BiomeDictionary.Type type) {
		restrictBiomeTypes.add(type);
		specialConditions.add(String.format("Is restricted to %s-like environments.", type.toString()));
		return this;
	}

	public BeeMutation requireDay() {
		requiresDay = true;
		requiresNight = false;
		specialConditions.add("Can only occur during the day.");
		return this;
	}

	public BeeMutation requireNight() {
		requiresDay = false;
		requiresNight = true;
		specialConditions.add("Can only occur during the night.");
		return this;
	}

	@Override
	public float getChance(IBeeHousing housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {

		World world = housing.getWorld();
		if (requiresDay && !world.isDaytime())
			return 0;

		if (requiresNight && world.isDaytime())
			return 0;

		// Skip if we are restricted by biomes and this one does not match.
		if (restrictBiomeTypes.size() > 0) {
			boolean noneMatched = true;

			BiomeGenBase biome = BiomeGenBase.getBiome(housing.getBiomeId());
			if(strictBiomeCheck) {
				BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
				if(types.length == 1 && restrictBiomeTypes.contains(types[0]))
					noneMatched = false;
			} else {
				for(BiomeDictionary.Type type : restrictBiomeTypes) {
					if(BiomeDictionary.isBiomeOfType(biome, type)) {
						noneMatched = false;
						break;
					}
				}
			}

			if (noneMatched)
				return 0;
		}

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(housing.getXCoord(), housing.getZCoord());
		if (biome.temperature < minTemperature || biome.temperature > maxTemperature)
			return 0;
		if (biome.rainfall < minRainfall || biome.rainfall > maxRainfall)
			return 0;

		float processedChance = chance * housing.getMutationModifier((IBeeGenome) genome0, (IBeeGenome) genome1, 1f)
				* PluginApiculture.beeInterface.getBeekeepingMode(world).getMutationModifier((IBeeGenome) genome0, (IBeeGenome) genome1, 1f);
		if(processedChance <= 0)
			return 0;

		if (this.allele0.getUID().equals(allele0.getUID()) && this.allele1.getUID().equals(allele1.getUID()))
			return processedChance;
		if (this.allele1.getUID().equals(allele0.getUID()) && this.allele0.getUID().equals(allele1.getUID()))
			return processedChance;

		return 0;

	}

}
