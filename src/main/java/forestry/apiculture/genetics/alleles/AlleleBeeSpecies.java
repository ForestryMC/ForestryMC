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
package forestry.apiculture.genetics.alleles;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.classification.IClassification;
import genetics.api.individual.IGenome;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.apiculture.IBeeSpriteColourProvider;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.genetics.IBeeProductProvider;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.api.core.IModelManager;
import forestry.apiculture.genetics.DefaultBeeModelProvider;
import forestry.apiculture.genetics.DefaultBeeSpriteColourProvider;
import forestry.apiculture.genetics.JubilanceDefault;
import forestry.core.genetics.alleles.AlleleForestrySpecies;

public class AlleleBeeSpecies extends AlleleForestrySpecies implements IAlleleBeeSpecies, IAlleleBeeSpeciesBuilder {
	@Nullable
	private Map<ItemStack, Float> productChances;
	@Nullable
	private Map<ItemStack, Float> specialtyChances;
	private final List<IBeeProductProvider> providers = new LinkedList<>();

	private IBeeModelProvider beeModelProvider;
	private IBeeSpriteColourProvider beeSpriteColourProvider;
	private IJubilanceProvider jubilanceProvider;
	private boolean nocturnal = false;

	public AlleleBeeSpecies(String modId, String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(modId, uid, unlocalizedName, authority, unlocalizedDescription, dominant, branch, binomial);

		beeModelProvider = DefaultBeeModelProvider.instance;
		beeSpriteColourProvider = new DefaultBeeSpriteColourProvider(primaryColor, secondaryColor);
		jubilanceProvider = JubilanceDefault.instance;
	}

	@Override
	public IAlleleBeeSpecies build() {
		//AlleleManager.geneticRegistry.registerAllele(this, BeeChromosomes.SPECIES);
		return this;
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public IAlleleBeeSpeciesBuilder addProduct(IBeeProductProvider provider) {
		this.providers.add(provider);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder addProduct(Supplier<ItemStack> product, Float chance) {
		addProduct(new IBeeProductProvider() {
			@Override
			public void addProducts(BiConsumer<ItemStack, Float> registry) {
				registry.accept(product.get(), chance);
			}
		});
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder addSpecialty(Supplier<ItemStack> specialty, Float chance) {
		addProduct(new IBeeProductProvider() {
			@Override
			public void addSpecialties(BiConsumer<ItemStack, Float> registry) {
				registry.accept(specialty.get(), chance);
			}
		});
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setJubilanceProvider(IJubilanceProvider provider) {
		this.jubilanceProvider = provider;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setNocturnal() {
		nocturnal = true;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setCustomBeeModelProvider(IBeeModelProvider beeIconProvider) {
		this.beeModelProvider = beeIconProvider;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setCustomBeeSpriteColourProvider(IBeeSpriteColourProvider beeIconColourProvider) {
		this.beeSpriteColourProvider = beeIconColourProvider;
		return this;
	}

	/* OTHER */
	@Override
	public boolean isNocturnal() {
		return nocturnal;
	}

	@Override
	public Map<ItemStack, Float> getProductChances() {
		if (productChances == null) {
			this.productChances = new HashMap<>();
			BiConsumer<ItemStack, Float> addProduct = (product, chance) -> {
				if (product.isEmpty()) {
					throw new IllegalArgumentException("Tried to add empty product");
				}
				if (chance <= 0.0f || chance > 1.0f) {
					throw new IllegalArgumentException("chance must be in the range (0, 1]");
				}
				productChances.put(product, chance);
			};
			providers.forEach(provider -> provider.addProducts(addProduct));
		}
		return productChances;
	}

	@Override
	public Map<ItemStack, Float> getSpecialtyChances() {
		if (specialtyChances == null) {
			BiConsumer<ItemStack, Float> addProduct = (product, chance) -> {
				if (product.isEmpty()) {
					throw new IllegalArgumentException("Tried to add empty specialty");
				}
				if (chance <= 0.0f || chance > 1.0f) {
					throw new IllegalArgumentException("chance must be in the range (0, 1]");
				}
				specialtyChances.put(product, chance);
			};
			providers.forEach(provider -> provider.addSpecialties(addProduct));
		}
		return specialtyChances;
	}

	@Override
	public boolean isJubilant(IGenome genome, IBeeHousing housing) {
		return jubilanceProvider.isJubilant(this, genome, housing);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager) {
		beeModelProvider.registerModels(item, manager);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ModelResourceLocation getModel(EnumBeeType type) {
		return beeModelProvider.getModel(type);
	}

	@Override
	public int getSpriteColour(int renderPass) {
		return beeSpriteColourProvider.getSpriteColour(renderPass);
	}
}
