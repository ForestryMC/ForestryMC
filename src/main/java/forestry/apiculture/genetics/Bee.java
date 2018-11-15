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
package forestry.apiculture.genetics;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IPollinatable;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.IndividualLiving;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Translator;
import forestry.core.utils.VectUtil;

public class Bee extends IndividualLiving implements IBee {

	private int generation;
	private boolean isNatural = true;

	private final IBeeGenome genome;
	@Nullable
	private IBeeGenome mate;

	/* CONSTRUCTOR */
	public Bee(NBTTagCompound nbt) {
		super(nbt);

		if (nbt.hasKey("NA")) {
			isNatural = nbt.getBoolean("NA");
		}

		if (nbt.hasKey("GEN")) {
			generation = nbt.getInteger("GEN");
		}

		if (nbt.hasKey("Genome")) {
			genome = BeeGenome.fromNBT(nbt.getCompoundTag("Genome"));
		} else {
			genome = BeeDefinition.FOREST.getGenome();
		}

		if (nbt.hasKey("Mate")) {
			mate = BeeGenome.fromNBT(nbt.getCompoundTag("Mate"));
		}
	}

	public Bee(IBeeGenome genome, IBee mate) {
		this(genome);
		this.mate = mate.getGenome();
	}

	public Bee(IBeeGenome genome) {
		this(genome, true, 0);
	}

	private Bee(IBeeGenome genome, boolean isNatural, int generation) {
		super(genome.getLifespan());
		this.genome = genome;
		this.isNatural = isNatural;
		this.generation = generation;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound = super.writeToNBT(nbttagcompound);

		if (!isNatural) {
			nbttagcompound.setBoolean("NA", false);
		}

		if (generation > 0) {
			nbttagcompound.setInteger("GEN", generation);
		}
		return nbttagcompound;
	}

	@Override
	public void setIsNatural(boolean flag) {
		this.isNatural = flag;
	}

	@Override
	public boolean isNatural() {
		return this.isNatural;
	}

	@Override
	public int getGeneration() {
		return generation;
	}

	@Override
	public void mate(IIndividual individual) {
		if (!(individual instanceof IBee)) {
			return;
		}

		IBee drone = (IBee) individual;
		mate = drone.getGenome();
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		storedData[0] = doEffect(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doEffect(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doEffect(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(genome, storedData, housing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IEffectData[] doFX(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		storedData[0] = doFX(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doFX(secondary, storedData[1], housing);

		return storedData;
	}

	@SideOnly(Side.CLIENT)
	private IEffectData doFX(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		return effect.doFX(genome, storedData, housing);
	}

	// / INFORMATION
	@Override
	public IBeeGenome getGenome() {
		return genome;
	}

	@Nullable
	@Override
	public IBeeGenome getMate() {
		return mate;
	}

	@Override
	public IBee copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Bee(nbttagcompound);
	}

	@Override
	public boolean canSpawn() {
		return mate != null;
	}

	@Override
	public Set<IErrorState> getCanWork(IBeeHousing housing) {
		World world = housing.getWorldObj();

		Set<IErrorState> errorStates = new HashSet<>();

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		// / Rain needs tolerant flyers
		if (housing.isRaining() && !canFlyInRain(beeModifier)) {
			errorStates.add(EnumErrorCode.IS_RAINING);
		}

		// / Night or darkness requires nocturnal species
		if (world.isDaytime()) {
			if (!canWorkDuringDay()) {
				errorStates.add(EnumErrorCode.NOT_NIGHT);
			}
		} else {
			if (!canWorkAtNight(beeModifier)) {
				errorStates.add(EnumErrorCode.NOT_DAY);
			}
		}

		if (housing.getBlockLightValue() > Constants.APIARY_MIN_LEVEL_LIGHT) {
			if (!canWorkDuringDay()) {
				errorStates.add(EnumErrorCode.NOT_GLOOMY);
			}
		} else {
			if (!canWorkAtNight(beeModifier)) {
				errorStates.add(EnumErrorCode.NOT_BRIGHT);
			}
		}

		// / Check for the sky, except if in hell
		if (!world.provider.isNether()) {
			if (!housing.canBlockSeeTheSky() && !canWorkUnderground(beeModifier)) {
				errorStates.add(EnumErrorCode.NO_SKY);
			}
		}

		// / And finally climate check
		IAlleleBeeSpecies species = genome.getPrimary();
		{
			EnumTemperature actualTemperature = housing.getTemperature();
			EnumTemperature beeBaseTemperature = species.getTemperature();
			EnumTolerance beeToleranceTemperature = genome.getToleranceTemp();

			if (!AlleleManager.climateHelper.isWithinLimits(actualTemperature, beeBaseTemperature, beeToleranceTemperature)) {
				if (beeBaseTemperature.ordinal() > actualTemperature.ordinal()) {
					errorStates.add(EnumErrorCode.TOO_COLD);
				} else {
					errorStates.add(EnumErrorCode.TOO_HOT);
				}
			}
		}

		{
			EnumHumidity actualHumidity = housing.getHumidity();
			EnumHumidity beeBaseHumidity = species.getHumidity();
			EnumTolerance beeToleranceHumidity = genome.getToleranceHumid();

			if (!AlleleManager.climateHelper.isWithinLimits(actualHumidity, beeBaseHumidity, beeToleranceHumidity)) {
				if (beeBaseHumidity.ordinal() > actualHumidity.ordinal()) {
					errorStates.add(EnumErrorCode.TOO_ARID);
				} else {
					errorStates.add(EnumErrorCode.TOO_HUMID);
				}
			}
		}

		return errorStates;
	}

	private boolean canWorkAtNight(IBeeModifier beeModifier) {
		return genome.getPrimary().isNocturnal() || genome.getNeverSleeps() || beeModifier.isSelfLighted();
	}

	private boolean canWorkDuringDay() {
		return !genome.getPrimary().isNocturnal() || genome.getNeverSleeps();
	}

	private boolean canWorkUnderground(IBeeModifier beeModifier) {
		return genome.getCaveDwelling() || beeModifier.isSunlightSimulated();
	}

	private boolean canFlyInRain(IBeeModifier beeModifier) {
		return genome.getToleratesRain() || beeModifier.isSealed();
	}

	private boolean isSuitableBiome(Biome biome) {
		EnumTemperature temperature = EnumTemperature.getFromBiome(biome);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.getRainfall());
		return isSuitableClimate(temperature, humidity);
	}

	private boolean isSuitableClimate(EnumTemperature temperature, EnumHumidity humidity) {
		return AlleleManager.climateHelper.isWithinLimits(temperature, humidity,
			genome.getPrimary().getTemperature(), genome.getToleranceTemp(),
			genome.getPrimary().getHumidity(), genome.getToleranceHumid());
	}

	@Override
	public List<Biome> getSuitableBiomes() {
		List<Biome> suitableBiomes = new ArrayList<>();
		for (Biome biome : Biome.REGISTRY) {
			if (isSuitableBiome(biome)) {
				suitableBiomes.add(biome);
			}
		}

		return suitableBiomes;
	}

	@Override
	public void addTooltip(List<String> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + Translator.translateToLocal("for.gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();
		if (!isPureBred(EnumBeeChromosome.SPECIES)) {
			list.add(TextFormatting.BLUE + Translator.translateToLocal("for.bees.hybrid").replaceAll("%PRIMARY", primary.getAlleleName()).replaceAll("%SECONDARY", secondary.getAlleleName()));
		}

		if (generation > 0) {
			EnumRarity rarity;
			if (generation >= 1000) {
				rarity = EnumRarity.EPIC;
			} else if (generation >= 100) {
				rarity = EnumRarity.RARE;
			} else if (generation >= 10) {
				rarity = EnumRarity.UNCOMMON;
			} else {
				rarity = EnumRarity.COMMON;
			}

			String generationString = rarity.color + Translator.translateToLocalFormatted("for.gui.beealyzer.generations", generation);
			list.add(generationString);
		}

		IAllele speedAllele = genome.getActiveAllele(EnumBeeChromosome.SPEED);
		IAlleleTolerance tempToleranceAllele = (IAlleleTolerance) getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
		IAlleleTolerance humidToleranceAllele = (IAlleleTolerance) getGenome().getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE);

		String unlocalizedCustomSpeed = "for.tooltip.worker." + speedAllele.getUnlocalizedName().replaceAll("(.*)\\.", "");
		String speed;
		if (Translator.canTranslateToLocal(unlocalizedCustomSpeed)) {
			speed = Translator.translateToLocal(unlocalizedCustomSpeed);
		} else {
			speed = speedAllele.getAlleleName() + ' ' + Translator.translateToLocal("for.gui.worker");
		}

		String lifespan = genome.getActiveAllele(EnumBeeChromosome.LIFESPAN).getAlleleName() + ' ' + Translator.translateToLocal("for.gui.life");
		String tempTolerance = TextFormatting.GREEN + "T: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getTemperature()) + " / " + tempToleranceAllele.getAlleleName();
		String humidTolerance = TextFormatting.GREEN + "H: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getHumidity()) + " / " + humidToleranceAllele.getAlleleName();
		String flowers = genome.getFlowerProvider().getDescription();

		list.add(lifespan);
		list.add(speed);
		list.add(tempTolerance);
		list.add(humidTolerance);
		list.add(flowers);

		if (genome.getNeverSleeps()) {
			list.add(TextFormatting.RED + GenericRatings.rateActivityTime(genome.getNeverSleeps(), false));
		}

		if (genome.getToleratesRain()) {
			list.add(TextFormatting.WHITE + Translator.translateToLocal("for.gui.flyer.tooltip"));
		}
	}

	@Override
	public void age(World world, float housingLifespanModifier) {
		IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);
		IBeeModifier beeModifier = mode.getBeeModifier();
		float finalModifier = housingLifespanModifier * beeModifier.getLifespanModifier(genome, mate, housingLifespanModifier);

		super.age(world, finalModifier);
	}

	// / PRODUCTION
	@Override
	public NonNullList<ItemStack> getProduceList() {
		NonNullList<ItemStack> products = NonNullList.create();

		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();

		products.addAll(primary.getProductChances().keySet());

		Set<ItemStack> secondaryProducts = secondary.getProductChances().keySet();
		// Remove duplicates
		for (ItemStack second : secondaryProducts) {
			boolean skip = false;

			for (ItemStack compare : products) {
				if (second.isItemEqual(compare)) {
					skip = true;
					break;
				}
			}

			if (!skip) {
				products.add(second);
			}

		}

		return products;
	}

	@Override
	public NonNullList<ItemStack> getSpecialtyList() {
		Set<ItemStack> specialties = genome.getPrimary().getSpecialtyChances().keySet();
		NonNullList<ItemStack> specialtyList = NonNullList.create();
		specialtyList.addAll(specialties);
		return specialtyList;
	}

	@Override
	public NonNullList<ItemStack> produceStacks(IBeeHousing housing) {
		World world = housing.getWorldObj();
		IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);

		NonNullList<ItemStack> products = NonNullList.create();

		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();

		IBeeModifier beeHousingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		IBeeModifier beeModeModifier = mode.getBeeModifier();

		// Bee genetic speed * beehousing * beekeeping mode
		float speed = genome.getSpeed() * beeHousingModifier.getProductionModifier(genome, 1f) * beeModeModifier.getProductionModifier(genome, 1f);

		// / Primary Products
		for (Map.Entry<ItemStack, Float> entry : primary.getProductChances().entrySet()) {
			if (world.rand.nextFloat() < entry.getValue() * speed) {
				products.add(entry.getKey().copy());
			}
		}
		// / Secondary Products
		for (Map.Entry<ItemStack, Float> entry : secondary.getProductChances().entrySet()) {
			if (world.rand.nextFloat() < Math.round(entry.getValue() / 2) * speed) {
				products.add(entry.getKey().copy());
			}
		}

		// / Specialty products
		if (primary.isJubilant(genome, housing) && secondary.isJubilant(genome, housing)) {
			for (Map.Entry<ItemStack, Float> entry : primary.getSpecialtyChances().entrySet()) {
				if (world.rand.nextFloat() < entry.getValue() * speed) {
					products.add(entry.getKey().copy());
				}
			}
		}

		BlockPos housingCoordinates = housing.getCoordinates();
		return genome.getFlowerProvider().affectProducts(world, this, housingCoordinates, products);
	}

	/* REPRODUCTION */
	@Override
	@Nullable
	public IBee spawnPrincess(IBeeHousing housing) {

		// We need a mated queen to produce offspring.
		if (mate == null) {
			return null;
		}

		// Fatigued queens do not produce princesses.
		if (BeeManager.beeRoot.getBeekeepingMode(housing.getWorldObj()).isFatigued(this, housing)) {
			return null;
		}

		return createOffspring(housing, mate, getGeneration() + 1);
	}

	@Override
	public List<IBee> spawnDrones(IBeeHousing housing) {

		World world = housing.getWorldObj();

		// We need a mated queen to produce offspring.
		if (mate == null) {
			return Collections.emptyList();
		}

		List<IBee> bees = new ArrayList<>();

		BlockPos housingPos = housing.getCoordinates();
		int toCreate = BeeManager.beeRoot.getBeekeepingMode(world).getFinalFertility(this, world, housingPos);

		if (toCreate <= 0) {
			toCreate = 1;
		}

		for (int i = 0; i < toCreate; i++) {
			IBee offspring = createOffspring(housing, mate, 0);
			offspring.setIsNatural(true);
			bees.add(offspring);
		}

		return bees;
	}

	private IBee createOffspring(IBeeHousing housing, IBeeGenome mate, int generation) {

		World world = housing.getWorldObj();

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occurred.
		IChromosome[] mutated1 = mutateSpecies(housing, genome, mate);
		if (mutated1 != null) {
			parent1 = mutated1;
		}
		IChromosome[] mutated2 = mutateSpecies(housing, mate, genome);
		if (mutated2 != null) {
			parent2 = mutated2;
		}

		for (int i = 0; i < parent1.length; i++) {
			if (parent1[i] != null && parent2[i] != null) {
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);
			}
		}

		IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);
		return new Bee(new BeeGenome(chromosomes), mode.isNaturalOffspring(this), generation);
	}

	@Nullable
	private static IChromosome[] mutateSpecies(IBeeHousing housing, IBeeGenome genomeOne, IBeeGenome genomeTwo) {

		World world = housing.getWorldObj();

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		IBeeGenome genome0;
		IBeeGenome genome1;

		IAlleleBeeSpecies allele0;
		IAlleleBeeSpecies allele1;

		if (world.rand.nextBoolean()) {
			allele0 = (IAlleleBeeSpecies) parent1[EnumBeeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = (IAlleleBeeSpecies) parent2[EnumBeeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = (IAlleleBeeSpecies) parent2[EnumBeeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = (IAlleleBeeSpecies) parent1[EnumBeeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		GameProfile playerProfile = housing.getOwner();
		IApiaristTracker breedingTracker = BeeManager.beeRoot.getBreedingTracker(world, playerProfile);

		List<IMutation> combinations = BeeManager.beeRoot.getCombinations(allele0, allele1, true);
		for (IMutation mutation : combinations) {
			IBeeMutation beeMutation = (IBeeMutation) mutation;

			float chance = beeMutation.getChance(housing, allele0, allele1, genome0, genome1);
			if (chance <= 0) {
				continue;
			}

			// boost chance for researched mutations
			if (breedingTracker.isResearched(beeMutation)) {
				float mutationBoost = chance * (Config.researchMutationBoostMultiplier - 1.0f);
				mutationBoost = Math.min(Config.maxResearchMutationBoostPercent, mutationBoost);
				chance += mutationBoost;
			}

			if (chance > world.rand.nextFloat() * 100) {
				breedingTracker.registerMutation(mutation);
				return BeeManager.beeRoot.templateAsChromosomes(mutation.getTemplate());
			}
		}

		return null;
	}

	/* FLOWERS */
	@Override
	@Nullable
	public IIndividual retrievePollen(IBeeHousing housing) {

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		int chance = Math.round(genome.getFlowering() * beeModifier.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorldObj();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance) {
			return null;
		}

		Vec3i area = getArea(genome, beeModifier);
		Vec3i offset = new Vec3i(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
		BlockPos housingPos = housing.getCoordinates();

		IIndividual pollen = null;

		for (int i = 0; i < 20; i++) {
			BlockPos randomPos = VectUtil.getRandomPositionInArea(random, area);
			BlockPos blockPos = VectUtil.add(housingPos, randomPos, offset);
			ICheckPollinatable pitcher = TileUtil.getTile(world, blockPos, ICheckPollinatable.class);
			if (pitcher != null) {
				if (genome.getFlowerProvider().isAcceptedPollinatable(world, pitcher)) {
					pollen = pitcher.getPollen();
				}
			} else {
				pollen = GeneticsUtil.getPollen(world, blockPos);
			}

			if (pollen != null) {
				return pollen;
			}
		}

		return null;
	}

	@Override
	public boolean pollinateRandom(IBeeHousing housing, IIndividual pollen) {

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		int chance = (int) (genome.getFlowering() * beeModifier.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorldObj();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance) {
			return false;
		}

		Vec3i area = getArea(genome, beeModifier);
		Vec3i offset = new Vec3i(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
		BlockPos housingPos = housing.getCoordinates();

		for (int i = 0; i < 30; i++) {

			BlockPos randomPos = VectUtil.getRandomPositionInArea(random, area);
			BlockPos posBlock = VectUtil.add(housingPos, randomPos, offset);

			ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(world, posBlock);
			if (checkPollinatable == null) {
				continue;
			}

			if (!genome.getFlowerProvider().isAcceptedPollinatable(world, checkPollinatable)) {
				continue;
			}
			if (!checkPollinatable.canMateWith(pollen)) {
				continue;
			}

			IPollinatable realPollinatable = GeneticsUtil.getOrCreatePollinatable(housing.getOwner(), world, posBlock, Config.pollinateVanillaTrees);

			if (realPollinatable != null) {
				realPollinatable.mateWith(pollen);
				return true;
			}
		}

		return false;
	}

	@Override
	public void plantFlowerRandom(IBeeHousing housing) {
		plantFlowerRandom(housing, null);
	}

	@Override
	public BlockPos plantFlowerRandom(IBeeHousing housing, @Nullable List<IBlockState> potentialFlowers) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		int chance = Math.round(genome.getFlowering() * beeModifier.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorldObj();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance) {
			return null;
		}
		// Gather required info
		IFlowerProvider provider = genome.getFlowerProvider();
		Vec3i area = getArea(genome, beeModifier);
		Vec3i offset = new Vec3i(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
		BlockPos housingPos = housing.getCoordinates();

		for (int i = 0; i < 10; i++) {
			BlockPos randomPos = VectUtil.getRandomPositionInArea(random, area);
			BlockPos posBlock = VectUtil.add(housingPos, randomPos, offset);

			if (potentialFlowers != null) {
				if (FlowerManager.flowerRegistry.growFlower(provider.getFlowerType(), world, this, posBlock, potentialFlowers)) {
					return posBlock;
				}
			} else { // legacy
				//noinspection deprecation
				if (FlowerManager.flowerRegistry.growFlower(provider.getFlowerType(), world, this, posBlock)) {
					return posBlock;
				}
			}
		}
		return null;
	}

	private static Vec3i getArea(IBeeGenome genome, IBeeModifier beeModifier) {
		Vec3i genomeTerritory = genome.getTerritory();
		float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
		return VectUtil.scale(genomeTerritory, housingModifier * 3.0f);
	}
}
