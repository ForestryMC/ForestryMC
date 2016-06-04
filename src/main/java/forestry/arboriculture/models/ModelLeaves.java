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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.models.ModelBlockDefault;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;

public class ModelLeaves extends ModelBlockDefault<BlockForestryLeaves> {

	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}

	@Override
	public void bakeInventoryBlock(@Nonnull BlockForestryLeaves block, @Nonnull ItemStack itemStack, @Nonnull IModelBaker baker) {
		if (!(itemStack.getItem() instanceof ItemBlockLeaves) || block == null) {
			return;
		}

		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();

		TileLeaves leaves = new TileLeaves();
		if (itemStack.hasTagCompound()) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeRoot.treeTemplates.get(0));
		}

		ResourceLocation leafSpriteLocation = leaves.getLeaveSprite(Proxies.render.fancyGraphicsEnabled());
		TextureAtlasSprite leavesIcon = map.getAtlasSprite(leafSpriteLocation.toString());
		if (leavesIcon == null) {
			return;
		}

		baker.addBlockModel(block, Block.FULL_BLOCK_AABB, null, leavesIcon, 0);

		// add fruit
		if (!leaves.hasFruit()) {
			return;
		}

		ResourceLocation fruitSpriteLocation = leaves.getFruitSprite();
		if (fruitSpriteLocation != null) {
			TextureAtlasSprite fruitTexture = map.getAtlasSprite(fruitSpriteLocation.toString());
			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, null, fruitTexture, 1);
		}
	}

	@Override
	public void bakeWorldBlock(@Nonnull BlockForestryLeaves block, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IExtendedBlockState stateExtended, @Nonnull IModelBaker baker) {
		TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);

		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();

		if (tile == null) {
			IAlleleTreeSpecies oakSpecies = TreeDefinition.Oak.getIndividual().getGenome().getPrimary();
			ResourceLocation spriteLocation = oakSpecies.getLeafSpriteProvider().getSprite(false, Proxies.render.fancyGraphicsEnabled());
			TextureAtlasSprite sprite = map.getAtlasSprite(spriteLocation.toString());
			baker.setParticleSprite(sprite);
			return;
		}

		ResourceLocation leafSpriteLocation = tile.getLeaveSprite(Proxies.render.fancyGraphicsEnabled());
		TextureAtlasSprite leafSprite = map.getAtlasSprite(leafSpriteLocation.toString());
		
		// Render the plain leaf block.
		baker.addBlockModel(block, Block.FULL_BLOCK_AABB, null, leafSprite, 0);

		// Render overlay for fruit leaves.
		ResourceLocation fruitSpriteLocation = tile.getFruitSprite();

		if (fruitSpriteLocation != null) {
			TextureAtlasSprite fruitSprite = map.getAtlasSprite(fruitSpriteLocation.toString());
			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, null, fruitSprite, 1);
		}
		
		// Set the particle sprite
		baker.setParticleSprite(leafSprite);
	}
}
