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

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ResourceUtil;

@OnlyIn(Dist.CLIENT)
public class ModelLeaves extends ModelBlockCached<BlockForestryLeaves, ModelLeaves.Key> {
	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	protected Key getInventoryKey(ItemStack itemStack) {
		TileLeaves leaves = new TileLeaves();
		if (itemStack.getTag() != null) {
			leaves.read(leaves.getBlockState(), itemStack.getTag());
		} else {
			leaves.setTree(TreeHelper.getRoot().getIndividualTemplates().get(0));
		}
		return getKey(leaves.getModelData());
	}

	@Override
	protected Key getWorldKey(BlockState state, IModelData extraData) {
		return getKey(extraData);
	}

	@Override
	protected void bakeBlock(BlockForestryLeaves block, IModelData extraData, Key key, ModelBaker baker, boolean inventory) {
		// Render the plain leaf block.
		baker.addBlockModel(key.leafSprite, BlockAbstractLeaves.FOLIAGE_COLOR_INDEX);

		if (key.fruitSprite != null) {
			baker.addBlockModel(key.fruitSprite, BlockAbstractLeaves.FRUIT_COLOR_INDEX);
		}

		// Set the particle sprite
		baker.setParticleSprite(key.leafSprite);
	}

	private Key getKey(IModelData extraData) {
		boolean fancy = Proxies.render.fancyGraphicsEnabled();

		ResourceLocation leafLocation = TileLeaves.getLeaveSprite(extraData, fancy);
		ResourceLocation fruitLocation = TileLeaves.getFruitSprite(extraData);

		return new Key(ResourceUtil.getBlockSprite(leafLocation), fruitLocation != null ? ResourceUtil.getBlockSprite(fruitLocation) : null, fancy);
	}

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
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Key)) {
				return false;
			} else {
				Key otherKey = (Key) other;
				return otherKey.leafSprite == leafSprite && otherKey.fruitSprite == fruitSprite && otherKey.fancy == fancy;
			}
		}
	}
}
