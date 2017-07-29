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

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IModelBaker;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.ModelBlockDefault;
import forestry.core.tiles.TileUtil;
import forestry.greenhouse.blocks.BlockGreenhouseSprite;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.IBlockCamouflaged;
import forestry.greenhouse.multiblock.GreenhouseController;

// DO NOT CACHE THIS. PLEASE. I BEG YOU. NOT IN THE CURRENT STATE.
@SideOnly(Side.CLIENT)
public class ModelCamouflaged<B extends Block & IBlockCamouflaged> extends ModelBlockDefault<B, ModelCamouflaged.Key> {
	public static final int DEFAULT_COLOR_INDEX = 100;
	public static final int OVERLAY_COLOR_INDEX = 101;
	public final ModelOverlay<B> overlayModel;

	public ModelCamouflaged(Class<B> blockClass) {
		super(blockClass);
		overlayModel = new ModelOverlay(blockClass);
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return BlockGreenhouseSprite.getSprite(BlockGreenhouseType.PLAIN, null, null, -1);
	}

	@Override
	protected Key getInventoryKey(ItemStack stack) {
		return new Key(stack, stack.getItemDamage());
	}

	@Override
	protected Key getWorldKey(IBlockState state) {
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
		return new Key(stateExtended, world, pos, state.getBlock().getMetaFromState(state));
	}

	@Override
	protected void bakeBlock(B block, Key key, IModelBaker baker, boolean inventory) {
		IBlockAccess world = key.world;
		BlockPos pos = key.pos;
		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		addCamouflageModel(block, baker, layer, world, pos);
		/*if (layer == BlockRenderLayer.CUTOUT || layer == null) {
			for (int overlayLayer = 0; overlayLayer < block.getLayers(); overlayLayer++) {
				addOverlaySprite(block, baker, world, pos, key.state, key.meta, overlayLayer);
			}
		}*/
		IBakedModel model;
		if(key.state != null){
			model = overlayModel.getModel(key.state);
		}else{
			model = overlayModel.getModel(key.itemStack, Minecraft.getMinecraft().world);
		}
		baker.addBakedModelPost(key.state, model);
	}

	/*private void addOverlaySprite(B block, IModelBaker baker, IBlockAccess world, BlockPos pos, IBlockState state, int meta, int layer) {
		if (block.hasOverlaySprite(world, pos, meta, layer)) {
			TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
			for (EnumFacing facing : EnumFacing.VALUES) {
				sprite[facing.ordinal()] = block.getOverlaySprite(facing, state, meta, layer);
			}
			baker.addBlockModel(pos, sprite, OVERLAY_COLOR_INDEX + layer);
		}
	}*/

	private void addCamouflageModel(B block, IModelBaker baker, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {
		if (world == null || pos == null) {
			TextureAtlasSprite defaultSprite = block.getDefaultSprite();

			baker.addBlockModel(pos, defaultSprite, DEFAULT_COLOR_INDEX);
			baker.setParticleSprite(defaultSprite);
			ItemStack camouflageStack = GreenhouseController.createDefaultCamouflageBlock();
		} else {
			ItemStack camouflageStack = block.getCamouflageBlock(world, pos);
			Block camouflageBlock = Block.getBlockFromItem(camouflageStack.getItem());
			if (camouflageStack.isEmpty() || camouflageBlock == Blocks.AIR) {
				camouflageStack = GreenhouseController.createDefaultCamouflageBlock();
			}
			ICamouflageItemHandler itemHandler = CamouflageManager.camouflageAccess.getHandler(camouflageStack);
			if (itemHandler != null) {
				ICamouflageHandler camouflageHandler = block.getCamouflageHandler(world, pos);
				if (camouflageHandler != null) {
					ICamouflagedTile camouflageTile = TileUtil.getTile(world, pos, ICamouflagedTile.class);
					if (camouflageTile != null) {
						Pair<IBlockState, IBakedModel> modelPair = itemHandler.getModel(camouflageStack, camouflageHandler, camouflageTile);
						IBlockState blockState = modelPair.getLeft();
						if (camouflageBlock.canRenderInLayer(blockState, layer)) {
							IBakedModel bakedModel = modelPair.getRight();
							baker.addBakedModel(blockState, bakedModel);
							baker.setParticleSprite(bakedModel.getParticleTexture());
						}
					}
				}
			}
		}
	}

	public static class Key {
		@Nullable
		public final IExtendedBlockState state;
		@Nullable
		public final IBlockAccess world;
		@Nullable
		public final BlockPos pos;
		public final ItemStack itemStack;
		public final int meta;

		public Key(@Nullable IExtendedBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int meta) {
			this.state = state;
			this.world = world;
			this.pos = pos;
			this.meta = meta;
			this.itemStack = ItemStack.EMPTY;
		}

		public Key(ItemStack itemStack, int meta) {
			this.state = null;
			this.world = null;
			this.pos = null;
			this.meta = meta;
			this.itemStack = itemStack;
		}
	}
}
