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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.alleles.IAlleleSpeciesBuilder;
import forestry.core.utils.GeneticsUtil;

import genetics.api.alleles.Allele;
import genetics.api.classification.IClassification;
import genetics.api.organism.IOrganismType;

public abstract class AlleleForestrySpecies extends Allele implements IAlleleForestrySpecies {
	private final String binomial;
	private final String authority;
	private final String description;
	private final String speciesIdentifier;
	private final IClassification branch;

	private final boolean hasEffect;
	private final boolean isSecret;
	private final boolean isCounted;
	private final EnumTemperature climate;
	private final EnumHumidity humidity;
	@Nullable
	private final Integer complexityOverride;

	protected AlleleForestrySpecies(AbstractBuilder<?> builder) {
		super(builder.translationKey, builder.isDominant);
		setRegistryName(new ResourceLocation(builder.modId, builder.uid));

		this.binomial = builder.binomial;
		this.authority = builder.authority;
		this.description = builder.description;
		this.speciesIdentifier = builder.speciesIdentifier;
		this.branch = builder.branch;

		this.hasEffect = builder.hasEffect;
		this.isSecret = builder.isSecret;
		this.isCounted = builder.isCounted;
		this.climate = builder.climate;
		this.humidity = builder.humidity;

		this.complexityOverride = builder.complexityOverride;
	}

	@Override
	public Component getItemName(IOrganismType type) {
		return GeneticsUtil.getItemName(type, this);
	}

	@Override
	public Component getAlyzerName(IOrganismType type) {
		return GeneticsUtil.getAlyzerName(type, this);
	}

	@Override
	public Component getDescription() {
		return Component.translatable(description);
	}

	@Override
	public int getComplexity() {
		if (complexityOverride != null) {
			return complexityOverride;
		}
		return GeneticsUtil.getResearchComplexity(this, getRoot().getKaryotype().getSpeciesType());
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
	public String getSpeciesIdentifier() {
		return speciesIdentifier;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public IClassification getBranch() {
		return this.branch;
	}

	protected String getModID() {
		return getRegistryName().getNamespace();
	}

	public abstract static class AbstractBuilder<B extends IAlleleSpeciesBuilder<?>> implements IAlleleSpeciesBuilder<B> {
		private final String modId;
		private final String uid;
		private final String speciesIdentifier;
		private String translationKey;
		private String binomial;
		private String authority = "Sengir";
		private String description = "";
		private IClassification branch;

		private boolean hasEffect = false;
		private boolean isSecret = false;
		private boolean isCounted = true;
		private boolean isDominant = false;
		private EnumTemperature climate = EnumTemperature.NORMAL;
		private EnumHumidity humidity = EnumHumidity.NORMAL;
		@Nullable
		private Integer complexityOverride = null;

		protected AbstractBuilder(String modId, String uid, String speciesIdentifier) {
			this.modId = modId;
			this.uid = uid;
			this.speciesIdentifier = speciesIdentifier;
		}

		protected static void checkBuilder(AbstractBuilder<?> builder) {
			Preconditions.checkNotNull(builder.branch, "Every forestry species needs a branch");
			Preconditions.checkNotNull(builder.translationKey);
			Preconditions.checkNotNull(builder.binomial);
		}

		@Override
		public B setDominant(boolean isDominant) {
			this.isDominant = isDominant;
			return cast();
		}

		@Override
		public B setAuthority(String authority) {
			this.authority = authority;
			return cast();
		}

		@Override
		public B setBinomial(String binomial) {
			this.binomial = binomial;
			return cast();
		}

		@Override
		public B setBranch(IClassification branch) {
			this.branch = branch;
			return cast();
		}

		@Override
		public B setDescriptionKey(String description) {
			this.description = description;
			return cast();
		}

		@Override
		public B setTranslationKey(String translationKey) {
			this.translationKey = translationKey;
			return cast();
		}

		@Override
		public B setComplexity(int complexity) {
			this.complexityOverride = complexity;
			return cast();
		}

		@Override
		public B setTemperature(EnumTemperature temperature) {
			climate = temperature;
			return cast();
		}

		@Override
		public B setHumidity(EnumHumidity humidity) {
			this.humidity = humidity;
			return cast();
		}

		@Override
		public B setHasEffect() {
			hasEffect = true;
			return cast();
		}

		@Override
		public B setIsSecret() {
			isSecret = true;
			return cast();
		}

		@Override
		public B setIsNotCounted() {
			isCounted = false;
			return cast();
		}
	}
}
