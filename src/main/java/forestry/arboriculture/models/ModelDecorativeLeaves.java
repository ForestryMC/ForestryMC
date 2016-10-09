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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModelDecorativeLeaves extends ModelBlockCached<BlockDecorativeLeaves, TreeDefinition> {
	public ModelDecorativeLeaves() {
		super(BlockDecorativeLeaves.class);
	}

	@Override
	protected TreeDefinition getInventoryKey(@Nonnull ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		if (!(block instanceof BlockDecorativeLeaves)) {
			return null;
		}
		BlockDecorativeLeaves bBlock = (BlockDecorativeLeaves) block;
		return bBlock.getTreeType(stack.getMetadata());
	}

	@Override
	protected TreeDefinition getWorldKey(@Nonnull IBlockState state) {
		Block block = state.getBlock();
		if (!(block instanceof BlockDecorativeLeaves)) {
			return null;
		}
		BlockDecorativeLeaves bBlock = (BlockDecorativeLeaves) block;
		return state.getValue(bBlock.getVariant());
	}

	@Override
	protected void bakeBlock(@Nonnull BlockDecorativeLeaves block, @Nonnull TreeDefinition treeDefinition, @Nonnull IModelBaker baker, boolean inventory) {
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();

		ITreeGenome genome = treeDefinition.getGenome();
		IAlleleTreeSpecies species = genome.getPrimary();
		ILeafSpriteProvider leafSpriteProvider = species.getLeafSpriteProvider();

		ResourceLocation leafSpriteLocation = leafSpriteProvider.getSprite(false, Proxies.render.fancyGraphicsEnabled());
		TextureAtlasSprite leafSprite = map.getAtlasSprite(leafSpriteLocation.toString());

		// Render the plain leaf block.
		baker.addBlockModel(block, Block.FULL_BLOCK_AABB, null, leafSprite, 0);

		// Render overlay for fruit leaves.
		ResourceLocation fruitSpriteLocation = genome.getFruitProvider().getDecorativeSprite();
		if (fruitSpriteLocation != null) {
			TextureAtlasSprite fruitSprite = map.getAtlasSprite(fruitSpriteLocation.toString());
			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, null, fruitSprite, 1);
		}

		// Set the particle sprite
		baker.setParticleSprite(leafSprite);
	}

	@Nullable
	@Override
	protected IBakedModel bakeModel(@Nonnull IBlockState state, @Nonnull TreeDefinition key) {
		if (key == null) {
			return null;
		}

		IModelBaker baker = new ModelBaker();

		Block block = state.getBlock();
		if (!blockClass.isInstance(block)) {
			return null;
		}
		BlockDecorativeLeaves bBlock = blockClass.cast(block);

		baker.setRenderBounds(Block.FULL_BLOCK_AABB);
		bakeBlock(bBlock, key, baker, false);

		blockModel = baker.bakeModel(false);
		onCreateModel(blockModel);
		return blockModel;
	}
}
