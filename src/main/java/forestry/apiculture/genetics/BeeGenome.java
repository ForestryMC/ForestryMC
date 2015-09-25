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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AlleleArea;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AlleleTolerance;
import forestry.core.utils.vect.Vect;

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
				public BeeGenome load(@Nonnull NBTTagCompound tagCompound) {
					return new BeeGenome(tagCompound);
				}
			});

	public static BeeGenome fromNBT(NBTTagCompound nbtTagCompound) {
		if (nbtTagCompound == null) {
			return null;
		}

		return beeGenomeCache.getUnchecked(nbtTagCompound);
	}

	/* CONSTRUCTOR */
	private BeeGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	public BeeGenome(IChromosome[] chromosomes) {
		super(chromosomes);
	}

	// NBT RETRIEVAL
	public static IAlleleBeeSpecies getSpecies(ItemStack itemStack) {
		if (!BeeManager.beeRoot.isMember(itemStack)) {
			return null;
		}

		IAlleleSpecies species = getSpeciesDirectly(itemStack);
		if (species instanceof IAlleleBeeSpecies) {
			return (IAlleleBeeSpecies) species;
		}

		return (IAlleleBeeSpecies) getActiveAllele(itemStack, EnumBeeChromosome.SPECIES, BeeManager.beeRoot);
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
		return ((IAlleleFloat) getActiveAllele(EnumBeeChromosome.SPEED)).getValue();
	}

	@Override
	public int getLifespan() {
		return ((IAlleleInteger) getActiveAllele(EnumBeeChromosome.LIFESPAN)).getValue();
	}

	@Override
	public int getFertility() {
		return ((IAlleleInteger) getActiveAllele(EnumBeeChromosome.FERTILITY)).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return ((AlleleTolerance) getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE)).getValue();
	}

	@Override
	public boolean getNocturnal() {
		return ((AlleleBoolean) getActiveAllele(EnumBeeChromosome.NOCTURNAL)).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return ((AlleleTolerance) getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE)).getValue();
	}

	@Override
	public boolean getTolerantFlyer() {
		return ((AlleleBoolean) getActiveAllele(EnumBeeChromosome.TOLERANT_FLYER)).getValue();
	}

	@Override
	public boolean getCaveDwelling() {
		return ((AlleleBoolean) getActiveAllele(EnumBeeChromosome.CAVE_DWELLING)).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return ((IAlleleFlowers) getActiveAllele(EnumBeeChromosome.FLOWER_PROVIDER)).getProvider();
	}

	@Override
	public int getFlowering() {
		return ((IAlleleInteger) getActiveAllele(EnumBeeChromosome.FLOWERING)).getValue();
	}

	@Override
	public int[] getTerritory() {
		Vect area = ((AlleleArea) getActiveAllele(EnumBeeChromosome.TERRITORY)).getArea();
		return new int[]{area.x, area.y, area.z};
	}

	@Override
	public IAlleleBeeEffect getEffect() {
		return (IAlleleBeeEffect) getActiveAllele(EnumBeeChromosome.EFFECT);
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return BeeManager.beeRoot;
	}
}
