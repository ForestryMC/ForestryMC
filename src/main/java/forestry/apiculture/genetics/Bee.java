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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.IndividualLiving;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.StringUtil;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.plugins.PluginApiculture;

public class Bee extends IndividualLiving implements IBee {

	public IBeeGenome genome;
	public IBeeGenome mate;

	/* CONSTRUCTOR */
	public Bee(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	public Bee(World world, IBeeGenome genome, IBee mate) {
		this(world, genome);
		this.mate = mate.getGenome();
		this.isIrregularMating = mate.isNatural() != this.isNatural;
	}

	public Bee(World world, IBeeGenome genome) {
		this(world, genome, true, 0);
	}

	public Bee(IBeeGenome genome) {
		this(genome, true, 0);
	}

	public Bee(World world, IBeeGenome genome, boolean isNatural, int generation) {
		super(genome.getLifespan(), isNatural, generation);
		this.genome = genome;
	}

	public Bee(IBeeGenome genome, boolean isNatural, int generation) {
		super(genome.getLifespan(), isNatural, generation);
		this.genome = genome;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		if (nbttagcompound == null) {
			this.genome = PluginApiculture.beeInterface.templateAsGenome(BeeTemplates.getForestTemplate());
			return;
		}

		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("Genome"))
			genome = new BeeGenome(nbttagcompound.getCompoundTag("Genome"));
		else
			genome = PluginApiculture.beeInterface.templateAsGenome(BeeTemplates.getForestTemplate());
		if (nbttagcompound.hasKey("Mate"))
			mate = new BeeGenome(nbttagcompound.getCompoundTag("Mate"));

	}

	@Override
	public void mate(IIndividual individual) {
		if(!(individual instanceof IBee))
			return;

		IBee drone = (IBee)individual;
		mate = drone.getGenome();
		this.isIrregularMating = drone.isNatural() != this.isNatural;
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		if (effect == null)
			return null;

		storedData[0] = doEffect(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable())
			return storedData;

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT);
		if (!secondary.isCombinable())
			return storedData;

		storedData[1] = doEffect(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doEffect(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(genome, storedData, housing);
	}

	@Override
	public IEffectData[] doFX(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		if (effect == null)
			return null;

		storedData[0] = doFX(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable())
			return storedData;

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(EnumBeeChromosome.EFFECT);
		if (!secondary.isCombinable())
			return storedData;

		storedData[1] = doFX(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doFX(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		return effect.doFX(genome, storedData, housing);
	}

	// / INFORMATION
	@Override
	public IBeeGenome getGenome() {
		return genome;
	}

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
	public int isWorking(IBeeHousing housing) {
		return canWork(housing).ordinal();
	}

	@Override
	public EnumErrorCode canWork(IBeeHousing housing) {

		World world = housing.getWorld();
		// / Rain needs tolerant flyers
		if (world.isRaining() && !genome.getTolerantFlyer() && BiomeHelper.canRainOrSnow(housing.getBiomeId()) && !housing.isSealed()) {
			return EnumErrorCode.ISRAINING;
		}

		// / Night or darkness requires nocturnal species
		if(world.isDaytime()) {
			if(!canWorkDuringDay())
				return EnumErrorCode.NOTNIGHT;
		} else if (!canWorkAtNight() && !housing.isSelfLighted())
			return EnumErrorCode.NOTDAY;

		if (world.getBlockLightValue(housing.getXCoord(), housing.getYCoord() + 2, housing.getZCoord()) > Defaults.APIARY_MIN_LEVEL_LIGHT) {
			if(!canWorkDuringDay())
				return EnumErrorCode.NOTGLOOMY;
		} else if(!canWorkAtNight() && !housing.isSelfLighted())
			return EnumErrorCode.NOTLUCID;

		// / No sky, except if in hell
		BiomeGenBase biome = BiomeGenBase.getBiome(housing.getBiomeId());
		if(biome == null)
			return EnumErrorCode.NOSKY;
		if (!BiomeHelper.isBiomeHellish(biome) && !world.canBlockSeeTheSky(housing.getXCoord(), housing.getYCoord() + 3, housing.getZCoord())
				&& !genome.getCaveDwelling() && !housing.isSunlightSimulated())
			return EnumErrorCode.NOSKY;

		// / And finally climate check
		if (!checkSuitableClimate(housing.getTemperature(), housing.getHumidity()))
			return EnumErrorCode.INVALIDBIOME;

		return EnumErrorCode.OK;
	}

	private boolean canWorkAtNight() {
		return genome.getPrimary().isNocturnal() || genome.getNocturnal();
	}

	private boolean canWorkDuringDay() {
		return !genome.getPrimary().isNocturnal() || genome.getNocturnal();
	}

	private boolean checkBiomeHazard(BiomeGenBase biome) {

		EnumTemperature temperature = EnumTemperature.getFromValue(biome.temperature);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);
		return AlleleManager.climateHelper.isWithinLimits(temperature, humidity,
				genome.getPrimary().getTemperature(), genome.getToleranceTemp(),
				genome.getPrimary().getHumidity(), genome.getToleranceHumid());
	}

	private boolean checkSuitableClimate(EnumTemperature temperature, EnumHumidity humidity) {

		return AlleleManager.climateHelper.isWithinLimits(temperature, humidity,
				genome.getPrimary().getTemperature(), genome.getToleranceTemp(),
				genome.getPrimary().getHumidity(), genome.getToleranceHumid());
	}

	@Override
	public boolean hasFlower(IBeeHousing housing) {

		IFlowerProvider provider = genome.getFlowerProvider();

		Vect housingPos = new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord());
		Vect area = getArea(genome, housing);
		Vect offset = new Vect(-area.x / 2, -area.y / 2, -area.z / 2);

		boolean hasFlower = false;

		MutableVect posCurrent = new MutableVect(0, 0, 0);
		while (posCurrent.advancePositionInArea(area)) {

			Vect posBlock = Vect.add(housingPos, posCurrent, offset);

			if (provider.isAcceptedFlower(housing.getWorld(), this, posBlock.x, posBlock.y, posBlock.z)) {
				hasFlower = true;
				break;
			}

		}

		return hasFlower;
	}

	@Override
	public ArrayList<Integer> getSuitableBiomeIds() {
		ArrayList<Integer> suitableBiomes = new ArrayList<Integer>();
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
			if (checkBiomeHazard(biome))
				suitableBiomes.add(biome.biomeID);

		return suitableBiomes;
	}

	@Override
	public ArrayList<BiomeGenBase> getSuitableBiomes() {
		ArrayList<BiomeGenBase> suitableBiomes = new ArrayList<BiomeGenBase>();
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
			if (checkBiomeHazard(biome))
				suitableBiomes.add(biome);

		return suitableBiomes;
	}

	@Override
	public void addTooltip(List<String> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + StringUtil.localize("gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();
		if (!isPureBred(EnumBeeChromosome.SPECIES))
			list.add("\u00A79" + StringUtil.localize("bees.hybrid").replaceAll("%PRIMARY",primary.getName()).replaceAll("%SECONDARY",secondary.getName()));

		IAllele speed = genome.getActiveAllele(EnumBeeChromosome.SPEED);
		String customWorker = "tooltip.worker." + speed.getUnlocalizedName().replaceFirst("gui.", "");
		if (StringUtil.canTranslate(customWorker))
			list.add(StringUtil.localize(customWorker));
		else
			list.add(speed.getName() + " " + StringUtil.localize("gui.worker"));

		list.add(genome.getActiveAllele(EnumBeeChromosome.LIFESPAN).getName() + " " + StringUtil.localize("gui.life"));

		IAlleleTolerance tempTolerance = (IAlleleTolerance)getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
		list.add("\u00A7aT: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getTemperature()) + " / " + tempTolerance.getName());

		IAlleleTolerance humidTolerance = (IAlleleTolerance)getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
		list.add("\u00A7aH: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getHumidity()) + " / " + humidTolerance.getName());

		list.add(genome.getFlowerProvider().getDescription());
		if (genome.getNocturnal())
			list.add("\u00A7c" + GenericRatings.rateActivityTime(genome.getNocturnal(), false));
	}

	@Override
	public void age(World world, float housingLifespanModifier) {
		IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(world);
		float finalModifier = housingLifespanModifier * mode.getLifespanModifier(genome, mate, housingLifespanModifier);

		super.age(world, finalModifier);
	}

	// / PRODUCTION
	@Override
	public ItemStack[] getProduceList() {
		ArrayList<ItemStack> products = new ArrayList<ItemStack>();

		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();

		products.addAll(primary.getProducts().keySet());

		Set<ItemStack> secondaryProducts = secondary.getProducts().keySet();
		// Remove duplicates
		for (ItemStack second : secondaryProducts) {
			boolean skip = false;

			for (ItemStack compare : products)
				if (second.isItemEqual(compare)) {
					skip = true;
					break;
				}

			if (!skip)
				products.add(second);

		}

		return products.toArray(new ItemStack[products.size()]);
	}

	@Override
	public ItemStack[] getSpecialtyList() {
		Set<ItemStack> specialties = genome.getPrimary().getSpecialty().keySet();
		return specialties.toArray(new ItemStack[specialties.size()]);
	}

	@Override
	public ItemStack[] produceStacks(IBeeHousing housing) {
		if (!hasFlower(housing))
			return null;

		if (housing == null) {
			Proxies.log.warning("Failed to produce in an apiary because the beehousing was null.");
			return null;
		}
		IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(housing.getWorld());
		if (mode == null) {
			Proxies.log.warning("Failed to produce in an apiary because the beekeeping mode was null.");
			return null;
		}

		ArrayList<ItemStack> products = new ArrayList<ItemStack>();

		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();

		// All work and no play makes queen a dull girl.
		if(mode.isOverworked(this, housing))
			setIsNatural(false);

		// Bee genetic speed * beehousing * beekeeping mode
		float speed = genome.getSpeed() * housing.getProductionModifier(genome, 1f) * mode.getProductionModifier(genome, 1f);

		// / Primary Products
		for (Map.Entry<ItemStack, Integer> entry : primary.getProducts().entrySet())
			if (housing.getWorld().rand.nextInt(100) < entry.getValue() * speed)
				products.add(entry.getKey().copy());
		// / Secondary Products
		for (Map.Entry<ItemStack, Integer> entry : secondary.getProducts().entrySet())
			if (housing.getWorld().rand.nextInt(100) < Math.round(entry.getValue() / 2) * speed)
				products.add(entry.getKey().copy());

		// We are done if the we are not jubilant.
		if (!primary.isJubilant(genome, housing) || !secondary.isJubilant(genome, housing))
			return products.toArray(new ItemStack[products.size()]);

		// / Specialty products
		for (Map.Entry<ItemStack, Integer> entry : primary.getSpecialty().entrySet())
			if (housing.getWorld().rand.nextInt(100) < entry.getValue() * speed)
				products.add(entry.getKey().copy());

		return genome.getFlowerProvider().affectProducts(housing.getWorld(), this, housing.getXCoord(), housing.getYCoord(), housing.getZCoord(),
				products.toArray(new ItemStack[products.size()]));
	}

	/* REPRODUCTION */
	@Override
	public IBee spawnPrincess(IBeeHousing housing) {

		// We need a mated queen to produce offspring.
		if (mate == null)
			return null;

		// Fatigued queens do not produce princesses.
		if (PluginApiculture.beeInterface.getBeekeepingMode(housing.getWorld()).isFatigued(this, housing))
			return null;

		return createOffspring(housing, getGeneration() + 1);
	}

	@Override
	public IBee[] spawnDrones(IBeeHousing housing) {

		World world = housing.getWorld();

		// We need a mated queen to produce offspring.
		if (mate == null)
			return null;

		ArrayList<IBee> bees = new ArrayList<IBee>();

		int toCreate = PluginApiculture.beeInterface.getBeekeepingMode(world).getFinalFertility(this, world, housing.getXCoord(), housing.getYCoord(),
				housing.getZCoord());

		if (toCreate <= 0)
			toCreate = 1;

		for (int i = 0; i < toCreate; i++) {
			IBee offspring = createOffspring(housing, -1);
			if (offspring != null) {
				offspring.setIsNatural(true);
				bees.add(offspring);
			}
		}

		if (bees.size() > 0)
			return bees.toArray(new IBee[bees.size()]);
		else
			return null;
	}

	private IBee createOffspring(IBeeHousing housing, int generation) {

		World world = housing.getWorld();

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated1 = mutateSpecies(housing, genome, mate);
		if (mutated1 != null)
			parent1 = mutated1;
		IChromosome[] mutated2 = mutateSpecies(housing, mate, genome);
		if (mutated2 != null)
			parent2 = mutated2;

		for (int i = 0; i < parent1.length; i++)
			if (parent1[i] != null && parent2[i] != null)
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);

		IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(world);
		IBee offspring = new Bee(world, new BeeGenome(chromosomes), mode.isNaturalOffspring(this), generation);

		/* Disabling the mutation rate nerf
		// All mutation and no play makes queen a dull girl.
		if(mode.isDegenerating(this, offspring, housing))
			offspring.setIsNatural(false);*/

		return offspring;
	}

	private IChromosome[] mutateSpecies(IBeeHousing housing, IGenome genomeOne, IGenome genomeTwo) {

		World world = housing.getWorld();

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		IGenome genome0;
		IGenome genome1;
		IAllele allele0;
		IAllele allele1;

		if (world.rand.nextBoolean()) {
			allele0 = parent1[EnumBeeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent2[EnumBeeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = parent2[EnumBeeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent1[EnumBeeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		for (IBeeMutation mutation : PluginApiculture.beeInterface.getMutations(true)) {
			float chance = mutation.getChance(housing, allele0, allele1, genome0, genome1);
			if (chance > world.rand.nextFloat() * 100) {
				IApiaristTracker breedingTracker = PluginApiculture.beeInterface.getBreedingTracker(world, housing.getOwnerName());
				breedingTracker.registerMutation(mutation);
				return PluginApiculture.beeInterface.templateAsChromosomes(mutation.getTemplate());
			}
		}

		return null;
	}

	/* FLOWERS */
	@Override
	public IIndividual retrievePollen(IBeeHousing housing) {

		int chance = Math.round(genome.getFlowering() * housing.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorld();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance)
			return null;

		Vect area = getArea(genome, housing);
		Vect offset = new Vect(-area.x / 2, -area.y / 4, -area.z / 2);
		Vect housingPos = new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord());

		IIndividual pollen = null;

		for (int i = 0; i < 20; i++) {
			Vect randomPos = Vect.getRandomPositionInArea(random, area);
			Vect blockPos = Vect.add(housingPos, randomPos, offset);
			TileEntity tile = world.getTileEntity(blockPos.x, blockPos.y, blockPos.z);

			if (tile instanceof IPollinatable) {
				IPollinatable pitcher = (IPollinatable) tile;
				if (genome.getFlowerProvider().isAcceptedPollinatable(world, pitcher))
					pollen = pitcher.getPollen();
			} else {
				pollen = GeneticsUtil.getErsatzPollen(world, blockPos.x, blockPos.y, blockPos.z);
			}

			if (pollen != null)
				return pollen;
		}

		return null;
	}

	@Override
	public boolean pollinateRandom(IBeeHousing housing, IIndividual pollen) {

		int chance = (int) (genome.getFlowering() * housing.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorld();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance)
			return false;

		Vect area = getArea(genome, housing);
		Vect offset = new Vect(-area.x / 2, -area.y / 4, -area.z / 2);
		Vect housingPos = new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord());

		for (int i = 0; i < 30; i++) {

			Vect randomPos = Vect.getRandomPositionInArea(random, area);
			Vect posBlock = Vect.add(housingPos, randomPos, offset);

			IPollinatable receiver = GeneticsUtil.getOrCreatePollinatable(housing.getOwnerName(), world, posBlock.x, posBlock.y, posBlock.z);
			if(receiver == null)
				continue;

			if (!genome.getFlowerProvider().isAcceptedPollinatable(world, receiver))
				continue;
			if (!receiver.canMateWith(pollen))
				continue;

			receiver.mateWith(pollen);
			return true;
		}

		return false;
	}

	@Override
	public void plantFlowerRandom(IBeeHousing housing) {

		int chance = Math.round(genome.getFlowering() * housing.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorld();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance)
			return;

		// Gather required info
		IFlowerProvider provider = genome.getFlowerProvider();
		Vect area = getArea(genome, housing);
		Vect offset = new Vect(-area.x / 2, -area.y / 4, -area.z / 2);
		Vect housingPos = new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord());

		for (int i = 0; i < 10; i++) {
			Vect randomPos = Vect.getRandomPositionInArea(random, area);
			Vect posBlock = Vect.add(housingPos, randomPos, offset);

			if (provider.growFlower(world, this, posBlock.x, posBlock.y, posBlock.z))
				break;
		}
	}

	private static Vect getArea(IBeeGenome genome, IBeeHousing housing) {
		int[] genomeTerritory = genome.getTerritory();
		float housingModifier = housing.getTerritoryModifier(genome, 1f);
		return new Vect(genomeTerritory).multiply(housingModifier * 3.0f);
	}
}
