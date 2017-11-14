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

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3i;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeGenomeWrapper;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Genome;

public class BeeGenome extends Genome implements IBeeGenome {
	/**
	 * 0 - Species (determines product)
	 * 1 - Speed
	 * 2 - Lifespan
	 * 3 - Fertility (Maximum number of offspring)
	 * 4 - Preferred temperature Icy: Snow biomes Cold: Tundra/Steppe, Extreme Mountains/Hills? Normal: Plains, Forests, Mountains Hot: Desert Hellish: Nether
	 * 5 - Temperature tolerance (Range +/-)
	 * 6 - Nocturnal
	 * 7 - Preferred humidity (Arid - Normal - Damp)
	 * 8 - Humidity tolerance (Range +/-)
	 * 9 - Flight interference tolerance (stuff falling from the sky/other hindrances -> tolerates dampness + flight interference tolerance => rain resistance)
	 * 10 - Cave dwelling
	 * 11 - Required flowers
	 * 12 - Flower plant chance
	 * 13 - Territory
	 */

	private static final LoadingCache<NBTTagCompound, BeeGenome> beeGenomeCache = CacheBuilder.newBuilder()
			.maximumSize(128)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build(new CacheLoader<NBTTagCompound, BeeGenome>() {
				@Override
				public BeeGenome load(NBTTagCompound tagCompound) {
					return new BeeGenome(tagCompound);
				}
			});

	public static BeeGenome fromNBT(NBTTagCompound nbtTagCompound) {
		return beeGenomeCache.getUnchecked(nbtTagCompound);
	}
	private IBeeGenomeWrapper wrapper;

	/* CONSTRUCTOR */
	private BeeGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
		this.wrapper = BeeManager.beeRoot.getWrapper(this);
	}

	public BeeGenome(IChromosome[] chromosomes) {
		super(chromosomes);
		this.wrapper = BeeManager.beeRoot.getWrapper(this);
	}

	// NBT RETRIEVAL
	public static IAlleleBeeSpecies getSpecies(ItemStack itemStack) {
		Preconditions.checkArgument(BeeManager.beeRoot.isMember(itemStack), "itemStack must be a bee");

		IAlleleSpecies species = getSpeciesDirectly(BeeManager.beeRoot, itemStack);
		if (species instanceof IAlleleBeeSpecies) {
			return (IAlleleBeeSpecies) species;
		}

		return (IAlleleBeeSpecies) getAllele(itemStack, EnumBeeChromosome.SPECIES, true);
	}

	// / INFORMATION RETRIEVAL
	@Override
	public IAlleleBeeSpecies getPrimary() {
		return (IAlleleBeeSpecies) getActiveAllele(EnumBeeChromosome.SPECIES);
	}

	@Override
	public IAlleleBeeSpecies getSecondary() {
		return (IAlleleBeeSpecies) getInactiveAllele(EnumBeeChromosome.SPECIES);
	}

	@Override
	public float getSpeed() {
		return wrapper.getSpeed();
	}

	@Override
	public int getLifespan() {
		return wrapper.getLifespan();
	}

	@Override
	public int getFertility() {
		return wrapper.getFertility();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return wrapper.getToleranceTemp();
	}

	@Override
	public boolean getNeverSleeps() {
		return wrapper.getNeverSleeps();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return wrapper.getToleranceHumid();
	}

	@Override
	public boolean getToleratesRain() {
		return wrapper.getToleratesRain();
	}

	@Override
	public boolean getCaveDwelling() {
		return wrapper.getCaveDwelling();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return wrapper.getFlowerProvider();
	}

	@Override
	public int getFlowering() {
		return wrapper.getFlowering();
	}

	@Override
	public Vec3i getTerritory() {
		return wrapper.getTerritory();
	}

	@Override
	public IAlleleBeeEffect getEffect() {
		return wrapper.getEffect();
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public IGenome getGenome() {
		return this;
	}

	@Override
	public <A extends IAllele> A getActiveAllele(EnumBeeChromosome chromosomeType, Class<A> alleleClass) {
		return wrapper.getActiveAllele(chromosomeType, alleleClass);
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(EnumBeeChromosome chromosomeType, Class<A> alleleClass) {
		return wrapper.getInactiveAllele(chromosomeType, alleleClass);
	}
}
