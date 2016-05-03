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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.models.ModelBlockOverlay;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

public class ModelDecorativeLeaves extends ModelBlockOverlay<BlockDecorativeLeaves> {

	public ModelDecorativeLeaves() {
		super(BlockDecorativeLeaves.class);
	}

//	@Override
//	public IBakedModel handleBlockState(IBlockState state) {
//		IModelBaker baker = new ModelBaker();
//
//		Block block = state.getBlock();
//		if (!blockClass.isInstance(block)) {
//			return null;
//		}
//		BlockDecorativeLeaves bBlock = blockClass.cast(block);
//		TreeDefinition tree = state.getValue(bBlock.getVariant());
//
//		baker.setRenderBoundsFromBlock(block);
//		bakeBlock(bBlock, tree, baker);
//
//		return latestBlockModel = baker.bakeModel(false);
//	}
//
//	@Override
//	public IBakedModel handleItemState(ItemStack stack) {
//		IModelBaker baker = new ModelBaker();
//		Block block = Block.getBlockFromItem(stack.getItem());
//		if (!blockClass.isInstance(block)) {
//			return null;
//		}
//		BlockDecorativeLeaves bBlock = blockClass.cast(block);
//		TreeDefinition tree = bBlock.getTreeType(stack.getMetadata());
//
//		baker.setRenderBoundsFromBlock(block);
//		bakeBlock(bBlock, tree, baker);
//
//		return latestItemModel = baker.bakeModel(true);
//	}

	@Override
	protected void bakeInventoryBlock(@Nonnull BlockDecorativeLeaves block, @Nonnull ItemStack item, @Nonnull IModelBaker baker) {

	}

	@Override
	protected void bakeWorldBlock(@Nonnull BlockDecorativeLeaves block, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IExtendedBlockState stateExtended, @Nonnull IModelBaker baker) {
	}

	public static void bakeBlock(BlockDecorativeLeaves block, TreeDefinition treeDefinition, IModelBaker baker) {
		if (treeDefinition == null) {
			return;
		}

		ITreeGenome genome = treeDefinition.getGenome();
		IAlleleTreeSpecies species = genome.getPrimary();
		ILeafSpriteProvider leafSpriteProvider = species.getLeafSpriteProvider();
		TextureAtlasSprite leaveSprite = leafSpriteProvider.getSprite(false, Proxies.render.fancyGraphicsEnabled());
		
		// Render the plain leaf block.
		baker.addBlockModel(block, null, leaveSprite, 0);

		// Render overlay for fruit leaves.
		TextureAtlasSprite fruitSprite = TextureManager.getInstance().getSprite(genome.getFruitProvider().getDecorativeSpriteIndex());

		if (fruitSprite != null) {
			baker.addBlockModel(block, null, fruitSprite, 1);
		}
		
		// Set the particle sprite
		baker.setParticleSprite(leaveSprite);
	}
}
