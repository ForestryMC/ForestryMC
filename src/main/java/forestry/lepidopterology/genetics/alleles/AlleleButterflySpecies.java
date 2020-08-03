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
package forestry.lepidopterology.genetics.alleles;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import forestry.api.core.ISetupListener;
import forestry.api.core.ISpriteRegistry;
import forestry.api.genetics.products.IDynamicProductList;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.genetics.IButterflyRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.ProductListWrapper;
import forestry.core.genetics.alleles.AlleleForestrySpecies;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

public class AlleleButterflySpecies extends AlleleForestrySpecies implements IAlleleButterflySpecies,
        ISetupListener {
    private final String texture;
    private final Color serumColour;
    private final float rarity;
    private final float flightDistance;
    private final boolean nocturnal;

    private final Set<BiomeDictionary.Type> spawnBiomes;

    private ProductListWrapper butterflyLoot;
    private ProductListWrapper caterpillarLoot;

    public AlleleButterflySpecies(Builder builder) {
        super(builder);
        this.texture = builder.texture;
        this.serumColour = builder.serumColour;
        this.rarity = builder.rarity;
        this.flightDistance = builder.flightDistance;
        this.nocturnal = builder.nocturnal;

        this.spawnBiomes = builder.spawnBiomes.build();

        this.butterflyLoot = builder.butterflyLoot;
        this.caterpillarLoot = builder.caterpillarLoot;
    }

    @Override
    public void onFinishSetup() {
        butterflyLoot = butterflyLoot.bake();
        caterpillarLoot = caterpillarLoot.bake();
    }

    @Override
    public IButterflyRoot getRoot() {
        return ButterflyManager.butterflyRoot;
    }

    @Override
    public ResourceLocation getEntityTexture() {
        return new ResourceLocation(getModID(), Constants.TEXTURE_PATH_ENTITIES + "/" + texture + ".png");
    }

    @Override
    public ResourceLocation getItemTexture() {
        return new ResourceLocation(getModID(), "item/" + texture);
    }

    @Override
    public Set<BiomeDictionary.Type> getSpawnBiomes() {
        return spawnBiomes;
    }

    @Override
    public boolean strictSpawnMatch() {
        return false;
    }

    /* RESEARCH */
    @Override
    public int getComplexity() {
        return (int) (1.35f / rarity * 1.5);
    }

    /* OTHER */
    @Override
    public boolean isSecret() {
        return rarity < 0.25f;
    }

    @Override
    public float getRarity() {
        return rarity;
    }

    @Override
    public float getFlightDistance() {
        return flightDistance;
    }

    @Override
    public boolean isNocturnal() {
        return nocturnal;
    }

    @Override
    public IDynamicProductList getButterflyLoot() {
        return butterflyLoot;
    }

    @Override
    public IDynamicProductList getCaterpillarLoot() {
        return caterpillarLoot;
    }

    @Override
    public int getSpriteColour(int renderPass) {
        if (renderPass > 0) {
            return serumColour.getRGB() & 0xffffff;
        }
        return 0xffffff;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerSprites(ISpriteRegistry registry) {
        registry.addSprite(getItemTexture());
    }

    public static class Builder extends AbstractBuilder<IAlleleButterflySpeciesBuilder>
            implements IAlleleButterflySpeciesBuilder {

        private final ImmutableSet.Builder<BiomeDictionary.Type> spawnBiomes = new ImmutableSet.Builder<>();
        private final ProductListWrapper butterflyLoot = ProductListWrapper.create();
        private final ProductListWrapper caterpillarLoot = ProductListWrapper.create();

        private String texture;
        private Color serumColour = Color.WHITE;
        private float rarity = 0.1f;
        private float flightDistance = 5.0f;
        private boolean nocturnal = false;

        public Builder(String modId, String uid, String speciesIdentifier) {
            super(modId, uid, speciesIdentifier);
        }

        @Override
        public IAlleleButterflySpeciesBuilder cast() {
            return this;
        }

        @Override
        public IAlleleButterflySpecies build() {
            checkBuilder(this);
            Preconditions.checkNotNull(texture);
            return new AlleleButterflySpecies(this);
        }

        @Override
        public IAlleleButterflySpeciesBuilder setRarity(float rarity) {
            this.rarity = rarity;
            return this;
        }

        @Override
        public IAlleleButterflySpeciesBuilder setTexture(String texture) {
            this.texture = texture;
            return this;
        }

        @Override
        public IAlleleButterflySpeciesBuilder setSerumColour(int serumColour) {
            return setSerumColour(new Color(serumColour));
        }

        @Override
        public IAlleleButterflySpeciesBuilder setSerumColour(Color serumColour) {
            this.serumColour = serumColour;
            return this;
        }

        @Override
        public IAlleleButterflySpeciesBuilder setFlightDistance(float flightDistance) {
            this.flightDistance = flightDistance;
            return this;
        }

        @Override
        public IAlleleButterflySpeciesBuilder setNocturnal() {
            this.nocturnal = true;
            return this;
        }

        public IAlleleButterflySpeciesBuilder addSpawnBiomes(Collection<BiomeDictionary.Type> biomeTags) {
            spawnBiomes.addAll(biomeTags);
            return this;
        }

        public IAlleleButterflySpeciesBuilder addSpawnBiome(BiomeDictionary.Type biomeTag) {
            spawnBiomes.add(biomeTag);
            return this;
        }
    }
}
