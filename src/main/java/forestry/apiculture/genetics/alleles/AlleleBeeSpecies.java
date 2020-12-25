/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture.genetics.alleles;

import com.google.common.base.Preconditions;
import forestry.api.apiculture.*;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.api.core.ISetupListener;
import forestry.api.genetics.products.IDynamicProductList;
import forestry.apiculture.genetics.DefaultBeeModelProvider;
import forestry.apiculture.genetics.DefaultBeeSpriteColourProvider;
import forestry.apiculture.genetics.JubilanceDefault;
import forestry.core.genetics.ProductListWrapper;
import forestry.core.genetics.alleles.AlleleForestrySpecies;
import genetics.api.individual.IGenome;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AlleleBeeSpecies extends AlleleForestrySpecies implements IAlleleBeeSpecies, ISetupListener {
    private final IBeeModelProvider beeModelProvider;
    private final IBeeSpriteColourProvider beeSpriteColourProvider;
    private final IJubilanceProvider jubilanceProvider;
    private final boolean nocturnal;

    private ProductListWrapper products;
    private ProductListWrapper specialties;

    public AlleleBeeSpecies(Builder builder) {
        super(builder);

        beeModelProvider = builder.beeModelProvider;
        beeSpriteColourProvider = builder.beeSpriteColourProvider;
        jubilanceProvider = builder.jubilanceProvider;
        products = builder.products;
        specialties = builder.specialties;
        nocturnal = builder.nocturnal;
    }

    @Override
    public void onFinishSetup() {
        products = products.bake();
        specialties = specialties.bake();
    }

    @Override
    public IBeeRoot getRoot() {
        return BeeManager.beeRoot;
    }

    /* OTHER */
    @Override
    public boolean isNocturnal() {
        return nocturnal;
    }

    @Override
    public IDynamicProductList getProducts() {
        return products;
    }

    @Override
    public IDynamicProductList getSpecialties() {
        return specialties;
    }

    @Override
    public boolean isJubilant(IGenome genome, IBeeHousing housing) {
        return jubilanceProvider.isJubilant(this, genome, housing);
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

    public static class Builder extends AbstractBuilder<IAlleleBeeSpeciesBuilder> implements IAlleleBeeSpeciesBuilder {
        private final ProductListWrapper products = ProductListWrapper.create();
        private final ProductListWrapper specialties = ProductListWrapper.create();
        private IBeeModelProvider beeModelProvider = DefaultBeeModelProvider.instance;
        @Nullable
        private IBeeSpriteColourProvider beeSpriteColourProvider;
        private IJubilanceProvider jubilanceProvider = JubilanceDefault.instance;
        private boolean nocturnal = false;

        public Builder(String modId, String uid, String speciesIdentifier) {
            super(modId, uid, speciesIdentifier);
        }

        protected static void checkBuilder(Builder builder) {
            AbstractBuilder.checkBuilder(builder);
            Preconditions.checkNotNull(builder.beeSpriteColourProvider);
        }

        @Override
        public IAlleleBeeSpeciesBuilder cast() {
            return this;
        }

        @Override
        public IAlleleBeeSpecies build() {
            checkBuilder(this);
            return new AlleleBeeSpecies(this);
        }

        @Override
        public IAlleleBeeSpeciesBuilder setColour(IBeeSpriteColourProvider colourProvider) {
            this.beeSpriteColourProvider = colourProvider;
            return this;
        }

        @Override
        public IAlleleBeeSpeciesBuilder setColour(int primaryColor, int secondaryColor) {
            beeSpriteColourProvider = new DefaultBeeSpriteColourProvider(primaryColor, secondaryColor);
            return this;
        }

        @Override
        public IAlleleBeeSpeciesBuilder addProduct(Supplier<ItemStack> product, Float chance) {
            products.addProduct(product, chance);
            return this;
        }

        @Override
        public IAlleleBeeSpeciesBuilder addSpecialty(Supplier<ItemStack> specialty, Float chance) {
            specialties.addProduct(specialty, chance);
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
    }
}
