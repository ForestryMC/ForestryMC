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
package forestry.lepidopterology.genetics;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IGenome;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.ComponentKeys;

import genetics.individual.Genome;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.api.lepidopterology.genetics.IButterflyMutation;
import forestry.core.errors.EnumErrorCode;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.IndividualLiving;
import forestry.core.utils.ClimateUtil;
import forestry.lepidopterology.ModuleLepidopterology;

public class Butterfly extends IndividualLiving implements IButterfly {
	private static final Random rand = new Random();

	/* CONSTRUCTOR */
	public Butterfly(CompoundNBT nbt) {
		super(nbt);
	}

	public Butterfly(IGenome genome) {
		super(genome, null, genome.getActiveValue(ButterflyChromosomes.LIFESPAN));
	}

	public Butterfly(IGenome genome, @Nullable IGenome mate) {
		super(genome, mate);
	}

	@Override
	public IIndividualRoot getRoot() {
		return ButterflyHelper.getRoot();
	}

	@Override
	public void addTooltip(List<ITextComponent> list) {
		IAlleleButterflySpecies primary = genome.getActiveAllele(ButterflyChromosomes.SPECIES);
		IAlleleButterflySpecies secondary = genome.getInactiveAllele(ButterflyChromosomes.SPECIES);
		if (!isPureBred(ButterflyChromosomes.SPECIES)) {
			list.add(new TranslationTextComponent("for.butterflies.hybrid", primary.getDisplayName(), secondary.getDisplayName()).applyTextStyle(TextFormatting.BLUE));
		}

		if (getMate().isPresent()) {
			list.add(new TranslationTextComponent("for.gui.fecundated").applyTextStyle(TextFormatting.RED));//TODO ITextComponent.toUpperCase(Locale.ENGLISH));
		}
		list.add(genome.getActiveAllele(ButterflyChromosomes.SIZE).getDisplayName().applyTextStyle(TextFormatting.YELLOW));
		list.add(genome.getActiveAllele(ButterflyChromosomes.SPEED).getDisplayName().applyTextStyle(TextFormatting.DARK_GREEN));
		list.add(genome.getActiveAllele(ButterflyChromosomes.LIFESPAN).getDisplayName().appendSibling(new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("for.gui.life")));

		IAlleleValue<EnumTolerance> tempTolerance = getGenome().getActiveAllele(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
		list.add(new StringTextComponent("T: " + AlleleManager.climateHelper.toDisplay(primary.getTemperature()) + " / " + tempTolerance.getDisplayName()).applyTextStyle(TextFormatting.GREEN));

		IAlleleValue<EnumTolerance> humidTolerance = getGenome().getActiveAllele(ButterflyChromosomes.HUMIDITY_TOLERANCE);
		list.add(new StringTextComponent("H: " + AlleleManager.climateHelper.toDisplay(primary.getHumidity()) + " / " + humidTolerance.getDisplayName()).applyTextStyle(TextFormatting.GREEN));

		list.add(new StringTextComponent(GenericRatings.rateActivityTime(genome.getActiveValue(ButterflyChromosomes.NOCTURNAL), primary.isNocturnal())).applyTextStyle(TextFormatting.RED));

		if (genome.getActiveValue(ButterflyChromosomes.TOLERANT_FLYER)) {
			list.add(new TranslationTextComponent("for.gui.flyer.tooltip").applyTextStyle(TextFormatting.WHITE));
		}
	}

	@Override
	public IButterfly copy() {
		CompoundNBT compoundNBT = new CompoundNBT();
		this.write(compoundNBT);
		return new Butterfly(compoundNBT);
	}

	@Override
	public ITextComponent getDisplayName() {
		return genome.getPrimary().getDisplayName();
	}

	@Override
	public Set<IErrorState> getCanSpawn(IButterflyNursery nursery, @Nullable IButterflyCocoon cocoon) {
		World world = nursery.getWorldObj();

		Set<IErrorState> errorStates = new HashSet<>();
		// / Night or darkness requires nocturnal species
		boolean isDaytime = world.isDaytime();
		if (!isActiveThisTime(isDaytime)) {
			if (isDaytime) {
				errorStates.add(EnumErrorCode.NOT_NIGHT);
			} else {
				errorStates.add(EnumErrorCode.NOT_DAY);
			}
		}

		// / And finally climate check
		IAlleleButterflySpecies species = genome.getActiveAllele(ButterflyChromosomes.SPECIES);
		EnumTemperature actualTemperature = nursery.getTemperature();
		EnumTemperature baseTemperature = species.getTemperature();
		EnumTolerance toleranceTemperature = genome.getActiveValue(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
		EnumHumidity actualHumidity = nursery.getHumidity();
		EnumHumidity baseHumidity = species.getHumidity();
		EnumTolerance toleranceHumidity = genome.getActiveValue(ButterflyChromosomes.HUMIDITY_TOLERANCE);
		ClimateUtil.addClimateErrorStates(actualTemperature, actualHumidity, baseTemperature, toleranceTemperature, baseHumidity, toleranceHumidity, errorStates);

		return errorStates;
	}

	@Override
	public Set<IErrorState> getCanGrow(IButterflyNursery nursery, @Nullable IButterflyCocoon cocoon) {
		Set<IErrorState> errorStates = new HashSet<>();

		// / And finally climate check
		IAlleleButterflySpecies species = genome.getActiveAllele(ButterflyChromosomes.SPECIES);
		EnumTemperature actualTemperature = nursery.getTemperature();
		EnumTemperature baseTemperature = species.getTemperature();
		EnumTolerance toleranceTemperature = genome.getActiveValue(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
		EnumHumidity actualHumidity = nursery.getHumidity();
		EnumHumidity baseHumidity = species.getHumidity();
		EnumTolerance toleranceHumidity = genome.getActiveValue(ButterflyChromosomes.HUMIDITY_TOLERANCE);
		ClimateUtil.addClimateErrorStates(actualTemperature, actualHumidity, baseTemperature, toleranceTemperature, baseHumidity, toleranceHumidity, errorStates);

		return errorStates;
	}

	@Override
	public boolean canSpawn(World world, double x, double y, double z) {
		if (!canFly(world)) {
			return false;
		}

		Biome biome = world.getBiome(new BlockPos(x, 0, z));
		IAlleleButterflySpecies species = getGenome().getActiveAllele(ButterflyChromosomes.SPECIES);
		if (!species.getSpawnBiomes().isEmpty()) {
			boolean noneMatched = true;

			if (species.strictSpawnMatch()) {
				Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
				if (types.size() == 1 && species.getSpawnBiomes().containsAll(types)) {
					noneMatched = false;
				}
			} else {
				for (BiomeDictionary.Type type : species.getSpawnBiomes()) {
					if (BiomeDictionary.hasType(biome, type)) {
						noneMatched = false;
						break;
					}
				}
			}

			if (noneMatched) {
				return false;
			}
		}

		return isAcceptedEnvironment(world, x, y, z);
	}

	@Override
	public boolean canTakeFlight(World world, double x, double y, double z) {
		return canFly(world) &&
			isAcceptedEnvironment(world, x, y, z);
	}

	private boolean canFly(World world) {
		return (!world.isRaining() || getGenome().getActiveValue(ButterflyChromosomes.TOLERANT_FLYER)) &&
			isActiveThisTime(world.isDaytime());
	}

	@Override
	public boolean isAcceptedEnvironment(World world, double x, double y, double z) {
		return isAcceptedEnvironment(world, (int) x, (int) y, (int) z);
	}

	private boolean isAcceptedEnvironment(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		Biome biome = world.getBiome(pos);
		EnumTemperature biomeTemperature = EnumTemperature.getFromBiome(biome, pos);
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.getDownfall());
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity,
			getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getTemperature(), getGenome().getActiveValue(ButterflyChromosomes.TEMPERATURE_TOLERANCE),
			getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getHumidity(), getGenome().getActiveValue(ButterflyChromosomes.HUMIDITY_TOLERANCE));
	}

	@Override
	@Nullable
	public IButterfly spawnCaterpillar(World world, IButterflyNursery nursery) {
		// We need a mated queen to produce offspring.
		if (mate == null) {
			return null;
		}

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated1 = mutateSpecies(world, nursery, genome, mate);
		if (mutated1 != null) {
			parent1 = mutated1;
		}
		IChromosome[] mutated2 = mutateSpecies(world, nursery, mate, genome);
		if (mutated2 != null) {
			parent2 = mutated2;
		}

		for (int i = 0; i < parent1.length; i++) {
			if (parent1[i] != null && parent2[i] != null) {
				chromosomes[i] = parent1[i].inheritChromosome(rand, parent2[i]);
			}
		}

		return new Butterfly(new Genome(ButterflyHelper.getRoot().getKaryotype(), chromosomes));
	}

	@Nullable
	private static IChromosome[] mutateSpecies(World world, IButterflyNursery nursery, IGenome genomeOne, IGenome genomeTwo) {

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		IGenome genome0;
		IGenome genome1;
		IAllele allele0;
		IAllele allele1;

		if (rand.nextBoolean()) {
			allele0 = parent1[ButterflyChromosomes.SPECIES.ordinal()].getActiveAllele();
			allele1 = parent2[ButterflyChromosomes.SPECIES.ordinal()].getInactiveAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = parent2[ButterflyChromosomes.SPECIES.ordinal()].getActiveAllele();
			allele1 = parent1[ButterflyChromosomes.SPECIES.ordinal()].getInactiveAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		IMutationContainer<IButterfly, IButterflyMutation> container = ButterflyHelper.getRoot().getComponent(ComponentKeys.MUTATIONS);
		for (IButterflyMutation mutation : container.getMutations(true)) {
			float chance = mutation.getChance(world, nursery, allele0, allele1, genome0, genome1);
			if (chance > rand.nextFloat() * 100) {
				return ButterflyManager.butterflyRoot.getKaryotype().templateAsChromosomes(mutation.getTemplate());
			}
		}

		return null;
	}

	private boolean isActiveThisTime(boolean isDayTime) {
		if (getGenome().getActiveValue(ButterflyChromosomes.NOCTURNAL)) {
			return true;
		}

		return isDayTime != getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).isNocturnal();
	}

	@Override
	public float getSize() {
		return getGenome().getActiveValue(ButterflyChromosomes.SIZE);
	}

	@Override
	public NonNullList<ItemStack> getLootDrop(IEntityButterfly entity, boolean playerKill, int lootLevel) {
		NonNullList<ItemStack> drop = NonNullList.create();

		CreatureEntity creature = entity.getEntity();
		float metabolism = (float) getGenome().getActiveValue(ButterflyChromosomes.METABOLISM) / 10;

		for (Map.Entry<ItemStack, Float> entry : getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getButterflyLoot().entrySet()) {
			if (creature.world.rand.nextFloat() < entry.getValue() * metabolism) {
				drop.add(entry.getKey().copy());
			}
		}

		return drop;
	}

	@Override
	public NonNullList<ItemStack> getCaterpillarDrop(IButterflyNursery nursery, boolean playerKill, int lootLevel) {
		NonNullList<ItemStack> drop = NonNullList.create();
		float metabolism = (float) getGenome().getActiveValue(ButterflyChromosomes.METABOLISM) / 10;

		for (Map.Entry<ItemStack, Float> entry : getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getCaterpillarLoot().entrySet()) {
			if (rand.nextFloat() < entry.getValue() * metabolism) {
				drop.add(entry.getKey().copy());
			}
		}

		return drop;
	}

	@Override
	public NonNullList<ItemStack> getCocoonDrop(IButterflyCocoon cocoon) {
		NonNullList<ItemStack> drop = NonNullList.create();
		float metabolism = (float) getGenome().getActiveValue(ButterflyChromosomes.METABOLISM) / 10;

		for (Map.Entry<ItemStack, Float> entry : getGenome().getActiveAllele(ButterflyChromosomes.COCOON).getCocoonLoot().entrySet()) {
			if (rand.nextFloat() < entry.getValue() * metabolism) {
				drop.add(entry.getKey().copy());
			}
		}

		if (ModuleLepidopterology.getSerumChance() > 0) {
			if (rand.nextFloat() < ModuleLepidopterology.getSerumChance() * metabolism) {
				ItemStack stack = ButterflyManager.butterflyRoot.getTypes().createStack(this, EnumFlutterType.SERUM);
				if (ModuleLepidopterology.getSecondSerumChance() > 0) {
					if (rand.nextFloat() < ModuleLepidopterology.getSecondSerumChance() * metabolism) {
						stack.setCount(2);
					}
				}
				drop.add(ButterflyManager.butterflyRoot.getTypes().createStack(this, EnumFlutterType.SERUM));
			}
		}

		if (cocoon.isSolid()) {
			drop.add(ButterflyManager.butterflyRoot.getTypes().createStack(this, EnumFlutterType.BUTTERFLY));
		}

		return drop;
	}

}
