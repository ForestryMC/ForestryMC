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
package forestry.core.genetics.alleles;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleSpeciesBuilder;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Translator;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public abstract class AlleleSpecies extends Allele implements IAlleleSpeciesBuilder, IAlleleSpecies {
	private final String binomial;
	private final String authority;
	private final String description;
	private final IClassification branch;

	private boolean hasEffect = false;
	private boolean isSecret = false;
	private boolean isCounted = true;
	private EnumTemperature climate = EnumTemperature.NORMAL;
	private EnumHumidity humidity = EnumHumidity.NORMAL;
	@Nullable
	private Integer complexityOverride = null;

	protected AlleleSpecies(String modId,
		String uid,
		String unlocalizedName,
		String authority,
		String unlocalizedDescription,
		boolean isDominant,
		IClassification branch,
		String binomial) {
		super(modId, uid, unlocalizedName, isDominant);

		this.branch = branch;
		this.binomial = binomial;
		this.description = Translator.translateToLocal(unlocalizedDescription);
		this.authority = authority;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return 0f;
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
			Item item = itemstack.getItem();
			if (beeItems.honeyDrop == item) {
				return 0.5f;
			} else if (beeItems.honeydew == item) {
				return 0.7f;
			} else if (beeItems.beeComb == item) {
				return 0.4f;
			}
		}

		if (getRoot().isMember(itemstack)) {
			return 1.0f;
		}

		for (Map.Entry<ItemStack, Float> entry : getRoot().getResearchCatalysts().entrySet()) {
			if (ItemStackUtil.isIdenticalItem(entry.getKey(), itemstack)) {
				return entry.getValue();
			}
		}

		return 0f;
	}

	@Override
	public int getComplexity() {
		if (complexityOverride != null) {
			return complexityOverride;
		}
		return GeneticsUtil.getResearchComplexity(this, getRoot().getSpeciesChromosomeType());
	}

	@Override
	public void setComplexity(int complexity) {
		this.complexityOverride = complexity;
	}

	@Override
	public NonNullList<ItemStack> getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		if (world.rand.nextFloat() < bountyLevel / 16.0f) {
			List<? extends IMutation> allMutations = getRoot().getCombinations(this);
			if (!allMutations.isEmpty()) {
				List<IMutation> unresearchedMutations = new ArrayList<>();
				IBreedingTracker tracker = individual.getGenome().getSpeciesRoot().getBreedingTracker(world, researcher);
				for (IMutation mutation : allMutations) {
					if (!tracker.isResearched(mutation)) {
						unresearchedMutations.add(mutation);
					}
				}

				IMutation chosenMutation;
				if (!unresearchedMutations.isEmpty()) {
					chosenMutation = unresearchedMutations.get(world.rand.nextInt(unresearchedMutations.size()));
				} else {
					chosenMutation = allMutations.get(world.rand.nextInt(allMutations.size()));
				}

				ItemStack researchNote = AlleleManager.alleleRegistry.getMutationNoteStack(researcher, chosenMutation);
				NonNullList<ItemStack> bounty = NonNullList.create();
				bounty.add(researchNote);
				return bounty;
			}
		}

		return NonNullList.create();
	}

	@Override
	public EnumTemperature getTemperature() {
		return climate;
	}

	@Override
	public EnumHumidity getHumidity() {
		return humidity;
	}

	@Override
	public boolean hasEffect() {
		return hasEffect;
	}

	@Override
	public boolean isSecret() {
		return isSecret;
	}

	@Override
	public boolean isCounted() {
		return isCounted;
	}

	@Override
	public String getBinomial() {
		return binomial;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public IClassification getBranch() {
		return this.branch;
	}

	@Override
	public IAlleleSpeciesBuilder setTemperature(EnumTemperature temperature) {
		climate = temperature;
		return this;
	}

	@Override
	public IAlleleSpeciesBuilder setHumidity(EnumHumidity humidity) {
		this.humidity = humidity;
		return this;
	}

	@Override
	public IAlleleSpeciesBuilder setHasEffect() {
		hasEffect = true;
		return this;
	}

	@Override
	public IAlleleSpeciesBuilder setIsSecret() {
		isSecret = true;
		return this;
	}

	@Override
	public IAlleleSpeciesBuilder setIsNotCounted() {
		isCounted = false;
		return this;
	}
}
