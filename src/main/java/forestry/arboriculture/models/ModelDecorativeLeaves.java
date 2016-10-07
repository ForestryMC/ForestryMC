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
import java.util.Collections;
import java.util.List;

import forestry.arboriculture.genetics.Tree;
import forestry.core.models.ModelBlockCached;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.models.ModelBlockDefault;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;

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
	protected TreeDefinition getWorldKey(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
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
}
