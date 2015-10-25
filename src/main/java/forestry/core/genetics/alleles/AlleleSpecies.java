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

import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IModelProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpeciesCustom;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.config.ForestryItem;
import forestry.core.utils.StackUtils;

public abstract class AlleleSpecies extends Allele implements IAlleleSpeciesCustom {

	private final String binomial;
	private final String authority;
	private final String description;
	private final IClassification branch;

	private boolean hasEffect = false;
	private boolean isSecret = false;
	private boolean isCounted = true;
	private EnumTemperature climate = EnumTemperature.NORMAL;
	private EnumHumidity humidity = EnumHumidity.NORMAL;

	protected AlleleSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription,
			boolean isDominant, IClassification branch, String binomial, boolean doRegister) {
		super(uid, unlocalizedName, isDominant, false);

		this.branch = branch;
		this.binomial = binomial;
		this.description = StatCollector.translateToLocal(unlocalizedDescription);
		this.authority = authority;

		// FIXME: do not register here
		if (doRegister) {
			AlleleManager.alleleRegistry.registerAllele(this);
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack == null) {
			return 0f;
		}

		if (ForestryItem.honeyDrop.isItemEqual(itemstack)) {
			return 0.5f;
		} else if (ForestryItem.honeydew.isItemEqual(itemstack)) {
			return 0.7f;
		} else if (ForestryItem.beeComb.isItemEqual(itemstack)) {
			return 0.4f;
		} else if (getRoot().isMember(itemstack)) {
			return 1.0f;
		}

		for (Map.Entry<ItemStack, Float> entry : getRoot().getResearchCatalysts().entrySet()) {
			if (StackUtils.isIdenticalItem(entry.getKey(), itemstack)) {
				return entry.getValue();
			}
		}

		return 0f;
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {

		ItemStack research = null;
		if (world.rand.nextFloat() < ((float) 10 / bountyLevel)) {
			Collection<? extends IMutation> combinations = getRoot().getCombinations(this);
			if (combinations.size() > 0) {
				IMutation[] candidates = combinations.toArray(new IMutation[combinations.size()]);
				research = AlleleManager.alleleRegistry.getMutationNoteStack(researcher,
						candidates[world.rand.nextInt(candidates.length)]);
			}
		}

		if (research != null) {
			return new ItemStack[] { research };
		} else {
			return StackUtils.EMPTY_STACK_ARRAY;
		}

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
	public IAlleleSpeciesCustom setTemperature(EnumTemperature temperature) {
		climate = temperature;
		return this;
	}

	@Override
	public IAlleleSpeciesCustom setHumidity(EnumHumidity humidity) {
		this.humidity = humidity;
		return this;
	}

	@Override
	public IAlleleSpeciesCustom setHasEffect() {
		hasEffect = true;
		return this;
	}

	@Override
	public IAlleleSpeciesCustom setIsSecret() {
		isSecret = true;
		return this;
	}

	@Override
	public IAlleleSpeciesCustom setIsNotCounted() {
		isCounted = false;
		return this;
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <T extends AlleleSpecies> T setTemperatureDeprecated(EnumTemperature temperature) {
		climate = temperature;
		return (T) this;
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public <T extends AlleleSpecies> T setHumidityDeprecated(EnumHumidity humidity) {
		this.humidity = humidity;
		return (T) this;
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public abstract ISpriteProvider getIconProvider();
	 */

	@Override
	@SideOnly(Side.CLIENT)
	public abstract IModelProvider getModelProvider();
}
