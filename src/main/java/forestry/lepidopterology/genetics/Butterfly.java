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
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyMutation;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.core.errors.EnumErrorCode;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.IndividualLiving;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.Translator;
import forestry.lepidopterology.ModuleLepidopterology;

public class Butterfly extends IndividualLiving implements IButterfly {
	private static final Random rand = new Random();

	private final IButterflyGenome genome;
	@Nullable
	private IButterflyGenome mate;

	/* CONSTRUCTOR */
	public Butterfly(NBTTagCompound nbt) {
		super(nbt);

		if (nbt.hasKey("Genome")) {
			genome = new ButterflyGenome(nbt.getCompoundTag("Genome"));
		} else {
			genome = ButterflyManager.butterflyRoot.templateAsGenome(ButterflyManager.butterflyRoot.getDefaultTemplate());
		}

		if (nbt.hasKey("Mate")) {
			mate = new ButterflyGenome(nbt.getCompoundTag("Mate"));
		}
	}

	public Butterfly(IButterflyGenome genome) {
		super(genome.getLifespan());
		this.genome = genome;
	}

	@Override
	public void addTooltip(List<String> list) {
		IAlleleButterflySpecies primary = genome.getPrimary();
		IAlleleButterflySpecies secondary = genome.getSecondary();
		if (!isPureBred(EnumButterflyChromosome.SPECIES)) {
			list.add(TextFormatting.BLUE + Translator.translateToLocal("for.butterflies.hybrid").replaceAll("%PRIMARY", primary.getAlleleName()).replaceAll("%SECONDARY", secondary.getAlleleName()));
		}

		if (getMate() != null) {
			list.add(TextFormatting.RED + Translator.translateToLocal("for.gui.fecundated").toUpperCase(Locale.ENGLISH));
		}
		list.add(TextFormatting.YELLOW + genome.getActiveAllele(EnumButterflyChromosome.SIZE).getAlleleName());
		list.add(TextFormatting.DARK_GREEN + genome.getActiveAllele(EnumButterflyChromosome.SPEED).getAlleleName());
		list.add(genome.getActiveAllele(EnumButterflyChromosome.LIFESPAN).getAlleleName() + ' ' + Translator.translateToLocal("for.gui.life"));

		IAlleleTolerance tempTolerance = (IAlleleTolerance) getGenome().getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE);
		list.add(TextFormatting.GREEN + "T: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getTemperature()) + " / " + tempTolerance.getAlleleName());

		IAlleleTolerance humidTolerance = (IAlleleTolerance) getGenome().getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE);
		list.add(TextFormatting.GREEN + "H: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getHumidity()) + " / " + humidTolerance.getAlleleName());

		list.add(TextFormatting.RED + GenericRatings.rateActivityTime(genome.getNocturnal(), genome.getPrimary().isNocturnal()));

		if (genome.getTolerantFlyer()) {
			list.add(TextFormatting.WHITE + Translator.translateToLocal("for.gui.flyer.tooltip"));
		}
	}

	@Override
	public IButterfly copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Butterfly(nbttagcompound);
	}

	@Override
	public IButterflyGenome getGenome() {
		return genome;
	}

	@Nullable
	@Override
	public IButterflyGenome getMate() {
		return mate;
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
		IAlleleButterflySpecies species = genome.getPrimary();
		EnumTemperature actualTemperature = nursery.getTemperature();
		EnumTemperature baseTemperature = species.getTemperature();
		EnumTolerance toleranceTemperature = genome.getToleranceTemp();
		EnumHumidity actualHumidity = nursery.getHumidity();
		EnumHumidity baseHumidity = species.getHumidity();
		EnumTolerance toleranceHumidity = genome.getToleranceHumid();
		ClimateUtil.addClimateErrorStates(actualTemperature, actualHumidity, baseTemperature, toleranceTemperature, baseHumidity, toleranceHumidity, errorStates);

		return errorStates;
	}

	@Override
	public Set<IErrorState> getCanGrow(IButterflyNursery nursery, @Nullable IButterflyCocoon cocoon) {
		World world = nursery.getWorldObj();

		Set<IErrorState> errorStates = new HashSet<>();

		// / And finally climate check
		IAlleleButterflySpecies species = genome.getPrimary();
		EnumTemperature actualTemperature = nursery.getTemperature();
		EnumTemperature baseTemperature = species.getTemperature();
		EnumTolerance toleranceTemperature = genome.getToleranceTemp();
		EnumHumidity actualHumidity = nursery.getHumidity();
		EnumHumidity baseHumidity = species.getHumidity();
		EnumTolerance toleranceHumidity = genome.getToleranceHumid();
		ClimateUtil.addClimateErrorStates(actualTemperature, actualHumidity, baseTemperature, toleranceTemperature, baseHumidity, toleranceHumidity, errorStates);

		return errorStates;
	}

	@Override
	public boolean canSpawn(World world, double x, double y, double z) {
		if (!canFly(world)) {
			return false;
		}

		Biome biome = world.getBiome(new BlockPos(x, 0, z));
		if (!getGenome().getPrimary().getSpawnBiomes().isEmpty()) {
			boolean noneMatched = true;

			if (getGenome().getPrimary().strictSpawnMatch()) {
				Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
				if (types.size() == 1 && getGenome().getPrimary().getSpawnBiomes().containsAll(types)) {
					noneMatched = false;
				}
			} else {
				for (BiomeDictionary.Type type : getGenome().getPrimary().getSpawnBiomes()) {
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
		return (!world.isRaining() || getGenome().getTolerantFlyer()) &&
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
		EnumHumidity biomeHumidity = EnumHumidity.getFromValue(biome.getRainfall());
		return AlleleManager.climateHelper.isWithinLimits(biomeTemperature, biomeHumidity,
			getGenome().getPrimary().getTemperature(), getGenome().getToleranceTemp(),
			getGenome().getPrimary().getHumidity(), getGenome().getToleranceHumid());
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
				chromosomes[i] = Chromosome.inheritChromosome(rand, parent1[i], parent2[i]);
			}
		}

		return new Butterfly(new ButterflyGenome(chromosomes));
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
			allele0 = parent1[EnumButterflyChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent2[EnumButterflyChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = parent2[EnumButterflyChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent1[EnumButterflyChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		for (IButterflyMutation mutation : ButterflyManager.butterflyRoot.getMutations(true)) {
			float chance = mutation.getChance(world, nursery, allele0, allele1, genome0, genome1);
			if (chance > rand.nextFloat() * 100) {
				return ButterflyManager.butterflyRoot.templateAsChromosomes(mutation.getTemplate());
			}
		}

		return null;
	}

	private boolean isActiveThisTime(boolean isDayTime) {
		if (getGenome().getNocturnal()) {
			return true;
		}

		return isDayTime != getGenome().getPrimary().isNocturnal();
	}

	@Override
	public float getSize() {
		return getGenome().getSize();
	}

	@Override
	public void mate(IIndividual individual) {
		if (!(individual instanceof IButterfly)) {
			return;
		}

		mate = ((IButterfly) individual).getGenome();
	}

	@Override
	public NonNullList<ItemStack> getLootDrop(IEntityButterfly entity, boolean playerKill, int lootLevel) {
		NonNullList<ItemStack> drop = NonNullList.create();

		EntityCreature creature = entity.getEntity();
		float metabolism = (float) getGenome().getMetabolism() / 10;

		for (Map.Entry<ItemStack, Float> entry : getGenome().getPrimary().getButterflyLoot().entrySet()) {
			if (creature.world.rand.nextFloat() < entry.getValue() * metabolism) {
				drop.add(entry.getKey().copy());
			}
		}

		return drop;
	}

	@Override
	public NonNullList<ItemStack> getCaterpillarDrop(IButterflyNursery nursery, boolean playerKill, int lootLevel) {
		NonNullList<ItemStack> drop = NonNullList.create();
		float metabolism = (float) getGenome().getMetabolism() / 10;

		for (Map.Entry<ItemStack, Float> entry : getGenome().getPrimary().getCaterpillarLoot().entrySet()) {
			if (rand.nextFloat() < entry.getValue() * metabolism) {
				drop.add(entry.getKey().copy());
			}
		}

		return drop;
	}

	@Override
	public NonNullList<ItemStack> getCocoonDrop(IButterflyCocoon cocoon) {
		NonNullList<ItemStack> drop = NonNullList.create();
		float metabolism = (float) getGenome().getMetabolism() / 10;

		for (Map.Entry<ItemStack, Float> entry : getGenome().getCocoon().getCocoonLoot().entrySet()) {
			if (rand.nextFloat() < entry.getValue() * metabolism) {
				drop.add(entry.getKey().copy());
			}
		}

		if (ModuleLepidopterology.getSerumChance() > 0) {
			if (rand.nextFloat() < ModuleLepidopterology.getSerumChance() * metabolism) {
				ItemStack stack = ButterflyManager.butterflyRoot.getMemberStack(this, EnumFlutterType.SERUM);
				if (ModuleLepidopterology.getSecondSerumChance() > 0) {
					if (rand.nextFloat() < ModuleLepidopterology.getSecondSerumChance() * metabolism) {
						stack.setCount(2);
					}
				}
				drop.add(ButterflyManager.butterflyRoot.getMemberStack(this, EnumFlutterType.SERUM));
			}
		}

		if (cocoon.isSolid()) {
			drop.add(ButterflyManager.butterflyRoot.getMemberStack(this, EnumFlutterType.BUTTERFLY));
		}

		return drop;
	}

}
