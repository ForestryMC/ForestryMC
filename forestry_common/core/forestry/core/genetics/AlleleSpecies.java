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
package forestry.core.genetics;

import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.config.ForestryItem;
import forestry.core.utils.Localization;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;

public abstract class AlleleSpecies extends Allele implements IAlleleSpecies {

	private final String name;
	private final String binomial;
	private String description = null;
	private int complexity = 3;
	private boolean hasEffect = false;
	private boolean isSecret = false;
	private boolean isCounted = true;
	private IClassification branch = null;
	private EnumTemperature climate = EnumTemperature.NORMAL;
	private EnumHumidity humidity = EnumHumidity.NORMAL;

	public AlleleSpecies(String uid, boolean isDominant, String name, IClassification branch, String binomial) {
		super(uid, isDominant, true);

		this.branch = branch;
		this.name = name;
		this.binomial = binomial;

		if (Localization.instance.hasMapping("description." + uid))
			description = StringUtil.localize("description." + uid);

		AlleleManager.alleleRegistry.registerAllele(this);
	}

	@Override
	public String getName() {
		return StringUtil.localize(name);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getComplexity() {
		return complexity;
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack == null)
			return 0f;

		if (ForestryItem.honeyDrop.isItemEqual(itemstack))
			return 0.5f;
		else if (ForestryItem.honeydew.isItemEqual(itemstack))
			return 0.7f;
		else if (ForestryItem.beeComb.isItemEqual(itemstack))
			return 0.4f;
		else if (AlleleManager.alleleRegistry.isIndividual(itemstack))
			return 1.0f;

		for (Map.Entry<ItemStack, Float> entry : getRoot().getResearchCatalysts().entrySet()) {
			if (StackUtils.isIdenticalItem(entry.getKey(), itemstack))
				return entry.getValue();
		}

		return 0f;
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {

		ItemStack research = null;
		if (world.rand.nextFloat() < ((float) 10 / bountyLevel)) {
			Collection<? extends IMutation> combinations = getRoot().getCombinations(this);
			if (combinations.size() > 0) {
				IMutation[] candidates = combinations.toArray(new IMutation[0]);
				research = AlleleManager.alleleRegistry.getMutationNoteStack(researcher, candidates[world.rand.nextInt(candidates.length)]);
			}
		}

		if (research != null)
			return new ItemStack[] { research };
		else
			return StackUtils.EMPTY_STACK_ARRAY;

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
		return "Sengir";
	}

	@Override
	public IClassification getBranch() {
		return this.branch;
	}

	public AlleleSpecies setComplexity(int complexity) {
		this.complexity = complexity;
		return this;
	}

	public AlleleSpecies setTemperature(EnumTemperature temperature) {
		climate = temperature;
		return this;
	}

	public AlleleSpecies setHumidity(EnumHumidity humidity) {
		this.humidity = humidity;
		return this;
	}

	public AlleleSpecies setHasEffect() {
		hasEffect = true;
		return this;
	}

	public AlleleSpecies setIsSecret() {
		isSecret = true;
		return this;
	}

	public AlleleSpecies setIsNotCounted() {
		isCounted = false;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public abstract IIconProvider getIconProvider();
}
