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
package forestry.arboriculture.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.models.ModelBlockOverlay;
import forestry.core.proxy.Proxies;

public class ModelLeaves extends ModelBlockOverlay<BlockForestryLeaves> {

	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}

	@Override
	public void renderInventory(BlockForestryLeaves block, ItemStack itemStack, IModelBaker baker) {
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
	public boolean renderInWorld(BlockForestryLeaves block, IBlockAccess world, BlockPos pos, IModelBaker baker) {
		TileLeaves tile = BlockForestryLeaves.getLeafTile(world, pos);
		if (tile == null) {
			return false;
		}

		// Render the plain leaf block.
		baker.addBlockModel(block, pos, tile.getLeaveSprite(Proxies.render.fancyGraphicsEnabled()), 0);

		// Render overlay for fruit leaves.
		TextureAtlasSprite fruitSprite = tile.getFruitSprite();

		if (fruitSprite != null) {
			baker.addBlockModel(block, pos, fruitSprite, 1);
		}

		return true;
	}
}
