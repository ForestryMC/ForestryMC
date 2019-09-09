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

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import genetics.api.alleles.Allele;
import genetics.api.classification.IClassification;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IAlleleSpeciesBuilder;
import forestry.core.utils.GeneticsUtil;

public abstract class AlleleForestrySpecies extends Allele implements IAlleleSpeciesBuilder, IAlleleForestrySpecies {
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

	protected AlleleForestrySpecies(String modId,
		String uid,
		String unlocalizedName,
		String authority,
		String unlocalizedDescription,
		boolean isDominant,
		IClassification branch,
		String binomial) {
		super(unlocalizedName, isDominant);
		setRegistryName(new ResourceLocation(modId, uid));

		this.branch = branch;
		this.binomial = binomial;
		this.description = unlocalizedDescription;
		this.authority = authority;
	}

	@Override
	public ITextComponent getDescription() {
		return new TranslationTextComponent(description);
	}

	@Override
	public int getComplexity() {
		if (complexityOverride != null) {
			return complexityOverride;
		}
		return GeneticsUtil.getResearchComplexity(this, getRoot().getKaryotype().getSpeciesType());
	}

	@Override
	public void setComplexity(int complexity) {
		this.complexityOverride = complexity;
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
