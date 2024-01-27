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
package forestry.arboriculture.models;

import com.google.common.base.Preconditions;

import java.util.Objects;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ResourceUtil;

import genetics.api.individual.IGenome;

@OnlyIn(Dist.CLIENT)
public class ModelDecorativeLeaves extends ModelBlockCached<BlockDecorativeLeaves, ModelDecorativeLeaves.Key> {
	public ModelDecorativeLeaves() {
		super(BlockDecorativeLeaves.class);
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
			if (other == null || !(other instanceof Key otherKey)) {
				return false;
			} else {
				return otherKey.definition == definition && otherKey.fancy == fancy;
			}
		}

		@Override
		public int hashCode() {
			return hashCode;
		}
	}

	@Override
	protected Key getInventoryKey(ItemStack stack) {
		Block block = Block.byItem(stack.getItem());
		Preconditions.checkArgument(block instanceof BlockDecorativeLeaves, "ItemStack must be for decorative leaves.");
		BlockDecorativeLeaves bBlock = (BlockDecorativeLeaves) block;
		return new Key(bBlock.getDefinition(), Proxies.render.fancyGraphicsEnabled());
	}

	@Override
	protected Key getWorldKey(BlockState state, ModelData extraData) {
		Block block = state.getBlock();
		Preconditions.checkArgument(block instanceof BlockDecorativeLeaves, "state must be for decorative leaves.");
		BlockDecorativeLeaves bBlock = (BlockDecorativeLeaves) block;
		return new Key(bBlock.getDefinition(), Proxies.render.fancyGraphicsEnabled());
	}

	@Override
	protected void bakeBlock(BlockDecorativeLeaves block, ModelData extraData, Key key, ModelBaker baker, boolean inventory) {
		TreeDefinition treeDefinition = key.definition;

		IGenome genome = treeDefinition.getGenome();
		IAlleleTreeSpecies species = genome.getActiveAllele(TreeChromosomes.SPECIES);
		ILeafSpriteProvider leafSpriteProvider = species.getLeafSpriteProvider();

		ResourceLocation leafSpriteLocation = leafSpriteProvider.getSprite(false, key.fancy);
		TextureAtlasSprite leafSprite = ResourceUtil.getBlockSprite(leafSpriteLocation);

		// Render the plain leaf block.
		baker.addBlockModel(leafSprite, BlockAbstractLeaves.FOLIAGE_COLOR_INDEX);

		// Render overlay for fruit leaves.
		ResourceLocation fruitSpriteLocation = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider().getDecorativeSprite();
		if (fruitSpriteLocation != null) {
			TextureAtlasSprite fruitSprite = ResourceUtil.getBlockSprite(fruitSpriteLocation);
			baker.addBlockModel(fruitSprite, BlockAbstractLeaves.FRUIT_COLOR_INDEX);
		}

		// Set the particle sprite
		baker.setParticleSprite(leafSprite);
	}

	@Override
	protected BakedModel bakeModel(BlockState state, Key key, BlockDecorativeLeaves block, ModelData extraData) {
		ModelBaker baker = new ModelBaker();

		bakeBlock(block, extraData, key, baker, false);

		blockModel = baker.bake(false);
		onCreateModel(blockModel);
		return blockModel;
	}
}
