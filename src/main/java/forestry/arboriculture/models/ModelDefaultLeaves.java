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
package forestry.arboriculture.models;

import com.google.common.base.Preconditions;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ResourceUtil;
import genetics.api.individual.IGenome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ModelDefaultLeaves extends ModelBlockCached<BlockDefaultLeaves, ModelDefaultLeaves.Key> {
    public ModelDefaultLeaves() {
        super(BlockDefaultLeaves.class);
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    public static class Key {
        public final TreeDefinition definition;
        public final boolean fancy;
        private final int hashCode;

        public Key(TreeDefinition definition, boolean fancy) {
            this.definition = definition;
            this.fancy = fancy;
            this.hashCode = Objects.hash(definition, fancy);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Key)) {
                return false;
            } else {
                Key otherKey = (Key) other;
                return otherKey.definition == definition && otherKey.fancy == fancy;
            }
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    @Override
    protected ModelDefaultLeaves.Key getInventoryKey(ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        Preconditions.checkArgument(block instanceof BlockDefaultLeaves, "ItemStack must be for default leaves.");
        BlockDefaultLeaves bBlock = (BlockDefaultLeaves) block;
        return new Key(bBlock.getTreeDefinition(), Proxies.render.fancyGraphicsEnabled());
    }

    @Override
    protected ModelDefaultLeaves.Key getWorldKey(BlockState state, IModelData extraData) {
        Block block = state.getBlock();
        Preconditions.checkArgument(block instanceof BlockDefaultLeaves, "state must be for default leaves.");
        BlockDefaultLeaves bBlock = (BlockDefaultLeaves) block;
        TreeDefinition treeDefinition = bBlock.getTreeDefinition(state);
        Preconditions.checkNotNull(treeDefinition);
        return new ModelDefaultLeaves.Key(treeDefinition, Proxies.render.fancyGraphicsEnabled());
    }

    @Override
    protected void bakeBlock(
            BlockDefaultLeaves block,
            IModelData extraData,
            Key key,
            ModelBaker baker,
            boolean inventory
    ) {
        TreeDefinition treeDefinition = key.definition;

        IGenome genome = treeDefinition.getGenome();
        IAlleleTreeSpecies species = genome.getActiveAllele(TreeChromosomes.SPECIES);
        ILeafSpriteProvider leafSpriteProvider = species.getLeafSpriteProvider();

        ResourceLocation leafSpriteLocation = leafSpriteProvider.getSprite(false, key.fancy);
        TextureAtlasSprite leafSprite = ResourceUtil.getBlockSprite(leafSpriteLocation);

        // Render the plain leaf block.
        baker.addBlockModel(leafSprite, BlockAbstractLeaves.FOLIAGE_COLOR_INDEX);

        // Set the particle sprite
        baker.setParticleSprite(leafSprite);
    }

    @Override
    protected IBakedModel bakeModel(BlockState state, Key key, BlockDefaultLeaves block, IModelData extraData) {
        ModelBaker baker = new ModelBaker();

        bakeBlock(block, extraData, key, baker, false);

        blockModel = baker.bake(false);
        onCreateModel(blockModel);
        return blockModel;
    }
}
