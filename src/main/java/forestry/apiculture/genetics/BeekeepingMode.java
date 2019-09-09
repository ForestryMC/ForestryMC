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
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IBee;

public class BeekeepingMode implements IBeekeepingMode {

	public static final IBeekeepingMode easy = new BeekeepingMode("EASY", 2.0f, 1.0f, 1.0f, false, false);
	public static final IBeekeepingMode normal = new BeekeepingMode("NORMAL", 1.0f, 1.0f, 1.0f, false, true);
	public static final IBeekeepingMode hard = new BeekeepingMode("HARD", 0.75f, 1.5f, 1.0f, false, true);
	public static final IBeekeepingMode hardcore = new BeekeepingMode("HARDCORE", 0.5f, 5.0f, 0.8f, true, true);
	public static final IBeekeepingMode insane = new BeekeepingMode("INSANE", 0.2f, 10.0f, 0.6f, true, true);

	private final Random rand;
	private final String name;
	private final boolean reducesFertility;
	private final boolean canFatigue;
	private final IBeeModifier beeModifier;

	public BeekeepingMode(String name, float mutationModifier, float lifespanModifier, float speedModifier, boolean reducesFertility, boolean canFatigue) {
		this.rand = new Random();
		this.name = name;
		this.reducesFertility = reducesFertility;
		this.canFatigue = canFatigue;
		this.beeModifier = new BeekeepingModeBeeModifier(mutationModifier, lifespanModifier, speedModifier);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public List<String> getDescription() {
		List<String> ret = new ArrayList<>();
		ret.add("beemode." + name.toLowerCase(Locale.ENGLISH) + ".desc");
		return ret;
	}

	@Override
	public float getWearModifier() {
		return 1.0f;
	}

	@Override
	public int getFinalFertility(IBee queen, World world, BlockPos pos) {
		int toCreate = queen.getGenome().getActiveValue(BeeChromosomes.FERTILITY);

		if (reducesFertility) {
			toCreate = new Random().nextInt(toCreate);
		}

		return toCreate;
	}

	@Override
	public boolean isFatigued(IBee queen, IBeeHousing housing) {
		if (!canFatigue) {
			return false;
		}

		if (queen.isNatural()) {
			return false;
		}

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		return queen.getGeneration() > 96 + rand.nextInt(6) + rand.nextInt(6) &&
			rand.nextFloat() < 0.02f * beeModifier.getGeneticDecay(queen.getGenome(), 1f);
	}

	@Override
	public boolean isDegenerating(IBee queen, IBee offspring, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		float mutationModifier = beeModifier.getMutationModifier(queen.getGenome(), queen.getMate().orElse(null), 1.0f);
		if (mutationModifier > 10) {
			return housing.getWorldObj().rand.nextFloat() * 100 < 0.4 * (mutationModifier * mutationModifier - 100);
		}

		return false;
	}

	@Override
	public boolean isNaturalOffspring(IBee queen) {
		return queen.isNatural();
	}

	@Override
	public boolean mayMultiplyPrincess(IBee queen) {
		return true;
	}

	@Override
	public IBeeModifier getBeeModifier() {
		return beeModifier;
	}

	private static class BeekeepingModeBeeModifier extends DefaultBeeModifier {
		private final float mutationModifier;
		private final float lifespanModifier;
		private final float speedModifier;

		public BeekeepingModeBeeModifier(float mutationModifier, float lifespanModifier, float speedModifier) {
			this.mutationModifier = mutationModifier;
			this.lifespanModifier = lifespanModifier;
			this.speedModifier = speedModifier;
		}

		@Override
		public float getMutationModifier(IGenome genome, IGenome mate, float currentModifier) {
			return this.mutationModifier;
		}

		@Override
		public float getLifespanModifier(IGenome genome, @Nullable IGenome mate, float currentModifier) {
			return this.lifespanModifier;
		}

		@Override
		public float getProductionModifier(IGenome genome, float currentModifier) {
			if (this.speedModifier > 16.0f) {
				return 16.0f;
			}
			return this.speedModifier;
		}
	}
}
