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
package forestry.greenhouse.models;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.client.MinecraftForgeClient;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockCached;
import forestry.greenhouse.blocks.BlockGreenhouseSprite;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.IBlockCamouflaged;

// DO NOT CACHE THIS. PLEASE. I BEG YOU. NOT IN THE CURRENT STATE.
@SideOnly(Side.CLIENT)
public class ModelOverlay<B extends Block & IBlockCamouflaged> extends ModelBlockCached<B, ModelOverlay.Key> {
	public static final int OVERLAY_COLOR_INDEX = 101;

	public ModelOverlay(Class<B> blockClass) {
		super(blockClass);
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return BlockGreenhouseSprite.getSprite(BlockGreenhouseType.PLAIN, null, null, -1);
	}

	@Override
	public IBakedModel getModel(IBlockState state) {
		return super.getModel(state);
	}

	@Override
	public IBakedModel getModel(ItemStack stack, World world) {
		return super.getModel(stack, world);
	}

	@Override
	protected Key getInventoryKey(ItemStack stack) {
		return new Key(null, stack.getItemDamage());
	}

	@Override
	protected Key getWorldKey(IBlockState state) {
		int meta = state.getBlock().getMetaFromState(state);
		return new Key(state, meta);
	}

	@Override
	protected void bakeBlock(B block, Key key, IModelBaker baker, boolean inventory) {
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		if (layer == BlockRenderLayer.CUTOUT || layer == null) {
			for (int overlayLayer = 0; overlayLayer < block.getLayers(); overlayLayer++) {
				addOverlaySprite(block, baker, key.state, key.meta, overlayLayer);
			}
		}
	}

	private void addOverlaySprite(B block, IModelBaker baker, IBlockState state, int meta, int layer) {
		if (block.hasOverlaySprite(meta, layer)) {
			TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
			for (EnumFacing facing : EnumFacing.VALUES) {
				sprite[facing.ordinal()] = block.getOverlaySprite(facing, state, meta, layer);
			}
			baker.addBlockModel(null, sprite, OVERLAY_COLOR_INDEX + layer);
		}
	}

	public static class Key {
		@Nullable
		public final IBlockState state;
		public final int meta;

		public Key(@Nullable IBlockState state, int meta) {
			this.state = state;
			this.meta = meta;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(meta) * 31 + (state == null ? 0 : state.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key)) {
				return false;
			}
			Key k = (Key) obj;
			return k.meta == meta && k.state == state;
		}
	}
}
