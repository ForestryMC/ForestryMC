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

import javax.annotation.Nullable;
import java.util.Objects;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.ModelBlockCached;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

public class ModelLeaves extends ModelBlockCached<BlockForestryLeaves, ModelLeaves.Key> {
	public static class Key {
		public final TextureAtlasSprite leafSprite;
		@Nullable
		public final TextureAtlasSprite fruitSprite;
		public final boolean fancy;
		private final int hashCode;

		public Key(TextureAtlasSprite leafSprite, @Nullable TextureAtlasSprite fruitSprite, boolean fancy) {
			this.leafSprite = leafSprite;
			this.fruitSprite = fruitSprite;
			this.fancy = fancy;
			this.hashCode = Objects.hash(leafSprite, fruitSprite, fancy);
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof Key)) {
				return false;
			} else {
				Key otherKey = (Key) other;
				return otherKey.leafSprite == leafSprite && otherKey.fruitSprite == fruitSprite && otherKey.fancy == fancy;
			}
		}

		@Override
		public int hashCode() {
			return hashCode;
		}
	}

	@Override
	protected Key getInventoryKey(ItemStack itemStack) {
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();

		TileLeaves leaves = new TileLeaves();
		if (itemStack.getTagCompound() != null) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeRoot.treeTemplates.get(0));
		}

		boolean fancy = Proxies.render.fancyGraphicsEnabled();
		ResourceLocation leafLocation = leaves.getLeaveSprite(fancy);
		ResourceLocation fruitLocation = leaves.getFruitSprite();

		return new Key(map.getAtlasSprite(leafLocation.toString()),
				fruitLocation != null ? map.getAtlasSprite(fruitLocation.toString()) : null,
				fancy);
	}

	@Override
	protected Key getWorldKey(IBlockState state) {
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);

		TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);
		boolean fancy = Proxies.render.fancyGraphicsEnabled();

		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();

		if (tile == null) {
			IAlleleTreeSpecies oakSpecies = TreeDefinition.Oak.getIndividual().getGenome().getPrimary();
			ResourceLocation spriteLocation = oakSpecies.getLeafSpriteProvider().getSprite(false, fancy);
			TextureAtlasSprite sprite = map.getAtlasSprite(spriteLocation.toString());
			return new Key(sprite, null, fancy);
		}

		ResourceLocation leafLocation = tile.getLeaveSprite(fancy);
		ResourceLocation fruitLocation = tile.getFruitSprite();

		return new Key(map.getAtlasSprite(leafLocation.toString()),
				fruitLocation != null ? map.getAtlasSprite(fruitLocation.toString()) : null,
				fancy);
	}

	@Override
	protected void bakeBlock(BlockForestryLeaves block, Key key, IModelBaker baker, boolean inventory) {
		// Render the plain leaf block.
		baker.addBlockModel(null, key.leafSprite, 0);

		if (key.fruitSprite != null) {
			baker.addBlockModel(null, key.fruitSprite, 1);
		}

		// Set the particle sprite
		baker.setParticleSprite(key.leafSprite);
	}

	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}
}
