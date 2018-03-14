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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;

@SideOnly(Side.CLIENT)
public class ModelDefaultLeaves extends ModelBlockCached<BlockDefaultLeaves, TreeDefinition> {
	public ModelDefaultLeaves() {
		super(BlockDefaultLeaves.class);
	}

	@Override
	protected TreeDefinition getInventoryKey(ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		Preconditions.checkArgument(block instanceof BlockDefaultLeaves, "ItemStack must be for default leaves.");
		BlockDefaultLeaves bBlock = (BlockDefaultLeaves) block;
		return bBlock.getTreeType(stack.getMetadata());
	}

	@Override
	protected TreeDefinition getWorldKey(IBlockState state) {
		Block block = state.getBlock();
		Preconditions.checkArgument(block instanceof BlockDefaultLeaves, "state must be for default leaves.");
		BlockDefaultLeaves bBlock = (BlockDefaultLeaves) block;
		TreeDefinition treeDefinition = bBlock.getTreeDefinition(state);
		Preconditions.checkNotNull(treeDefinition);
		return treeDefinition;
	}

	@Override
	protected void bakeBlock(BlockDefaultLeaves block, TreeDefinition treeDefinition, IModelBaker baker, boolean inventory) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();

		ITreeGenome genome = treeDefinition.getGenome();
		IAlleleTreeSpecies species = genome.getPrimary();
		ILeafSpriteProvider leafSpriteProvider = species.getLeafSpriteProvider();

		ResourceLocation leafSpriteLocation = leafSpriteProvider.getSprite(false, Proxies.render.fancyGraphicsEnabled());
		TextureAtlasSprite leafSprite = map.getAtlasSprite(leafSpriteLocation.toString());

		// Render the plain leaf block.
		baker.addBlockModel(null, leafSprite, BlockAbstractLeaves.FOLIAGE_COLOR_INDEX);

		// Render overlay for fruit leaves.
		ResourceLocation fruitSpriteLocation = genome.getFruitProvider().getDecorativeSprite();
		if (fruitSpriteLocation != null) {
			TextureAtlasSprite fruitSprite = map.getAtlasSprite(fruitSpriteLocation.toString());
			baker.addBlockModel(null, fruitSprite, BlockAbstractLeaves.FRUIT_COLOR_INDEX);
		}

		// Set the particle sprite
		baker.setParticleSprite(leafSprite);
	}

	@Override
	protected IBakedModel bakeModel(IBlockState state, TreeDefinition key, BlockDefaultLeaves block) {
		IModelBaker baker = new ModelBaker();

		bakeBlock(block, key, baker, false);

		blockModel = baker.bakeModel(false);
		onCreateModel(blockModel);
		return blockModel;
	}
}
