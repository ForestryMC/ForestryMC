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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.models.ModelBlockOverlay;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;

public class ModelLeaves extends ModelBlockOverlay<BlockForestryLeaves> {

	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}

	@Override
	public void bakeInventoryBlock(BlockForestryLeaves block, ItemStack itemStack, IModelBaker baker) {
		if (!(itemStack.getItem() instanceof ItemBlockLeaves) || block == null) {
			return;
		}

		TileLeaves leaves = new TileLeaves();
		if (itemStack.hasTagCompound()) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeRoot.treeTemplates.get(0));
		}

		TextureAtlasSprite leavesIcon = leaves.getLeaveSprite(Proxies.render.fancyGraphicsEnabled());
		if (leavesIcon == null) {
			return;
		}
		
		baker.addBlockModel(block, null, leavesIcon, 0);

		// add fruit
		if (!leaves.hasFruit()) {
			return;
		}

		TextureAtlasSprite fruitTexture = leaves.getFruitSprite();
		if (fruitTexture == null) {
			return;
		}
		baker.addBlockModel(block, null, fruitTexture, 1);
	}

	@Override
	public void bakeWorldBlock(BlockForestryLeaves block, IBlockAccess world, BlockPos pos, IExtendedBlockState stateExtended, IModelBaker baker) {
		TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (tile == null) {
			return;
		}

		TextureAtlasSprite leaveSprite = tile.getLeaveSprite(Proxies.render.fancyGraphicsEnabled());
		
		// Render the plain leaf block.
		baker.addBlockModel(block, pos, leaveSprite, 0);

		// Render overlay for fruit leaves.
		TextureAtlasSprite fruitSprite = tile.getFruitSprite();

		if (fruitSprite != null) {
			baker.addBlockModel(block, pos, fruitSprite, 1);
		}
		
		// Set the particle sprite
		baker.setParticleSprite(leaveSprite);
	}
}
