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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.IExtendedBlockState;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockDefault;
import forestry.core.utils.CamouflageUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;

// DO NOT CACHE THIS. PLEASE. I BEG YOU. NOT IN THE CURRENT STATE.
public class ModelGreenhouse extends ModelBlockDefault<BlockGreenhouse, ModelGreenhouse.Key> {
	public static class Key {
		public final IExtendedBlockState state;
		public final IBlockAccess world;
		public final BlockPos pos;

		public Key(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {
			this.state = state;
			this.world = world;
			this.pos = pos;
		}
	}

	public ModelGreenhouse() {
		super(BlockGreenhouse.class);
	}

	@Override
	protected Key getInventoryKey(@Nonnull ItemStack stack) {
		return new Key(null, null, null);
	}

	@Override
	protected Key getWorldKey(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
		return new Key(state instanceof IExtendedBlockState ? ((IExtendedBlockState) state) : null, world, pos);
	}

	@Override
	protected void bakeBlock(@Nonnull BlockGreenhouse block, @Nonnull Key key, @Nonnull IModelBaker baker, boolean inventory) {
		ItemStack camouflageStack = key.world != null ? CamouflageUtil.getCamouflageBlock(key.world, key.pos) : null;
		IBlockAccess world = key.world;
		BlockPos pos = key.pos;

		if (camouflageStack != null) {
			ICamouflageHandler camouflageHandler = CamouflageUtil.getCamouflageHandler(world, pos);
			ICamouflagedTile camouflageTile = (ICamouflagedTile) world.getTileEntity(pos);
			ICamouflageItemHandler itemHandler = CamouflageManager.camouflageAccess.getHandlerFromItem(camouflageStack);
			if(itemHandler != null){
				Pair<IBlockState, IBakedModel> model = itemHandler.getModel(camouflageStack, camouflageHandler, camouflageTile);

				baker.addBakedModel(model.getLeft(), model.getRight());
				baker.setParticleSprite(model.getRight().getParticleTexture());
			}
		}

		//Bake the default blocks
		else if (block.getGreenhouseType() == BlockGreenhouseType.GLASS) {
			TextureAtlasSprite glassSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null, null, world, pos);

			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null, null, world, pos), 100);
			baker.setParticleSprite(glassSprite);
		} else {
			TextureAtlasSprite plainSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null, null, world, pos);

			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null, null, world, pos), 100);
			baker.setParticleSprite(plainSprite);
		}

		if (block.getGreenhouseType().hasOverlaySprite) {
			TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
			for (EnumFacing facing : EnumFacing.VALUES) {
				sprite[facing.ordinal()] = BlockGreenhouseType.getSprite(block.getGreenhouseType(), facing, key.state, world, pos);
			}
			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, pos, sprite, 101);
		}
	}
}
