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

import java.time.chrono.MinguoEra;

import javax.annotation.Nullable;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IModelBaker;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.ModelBlockDefault;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.CamouflageUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

// DO NOT CACHE THIS. PLEASE. I BEG YOU. NOT IN THE CURRENT STATE.
@SideOnly(Side.CLIENT)
public class ModelGreenhouse extends ModelBlockDefault<BlockGreenhouse, ModelGreenhouse.Key> {
	public static class Key {
		@Nullable
		public final IExtendedBlockState state;
		@Nullable
		public final IBlockAccess world;
		@Nullable
		public final BlockPos pos;

		public Key(@Nullable IExtendedBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos) {
			this.state = state;
			this.world = world;
			this.pos = pos;
		}
	}

	public ModelGreenhouse() {
		super(BlockGreenhouse.class);
	}

	@Override
	protected Key getInventoryKey(ItemStack stack) {
		return new Key(null, null, null);
	}

	@Override
	protected Key getWorldKey(IBlockState state) {
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
		return new Key(stateExtended, world, pos);
	}

	@Override
	protected void bakeBlock(BlockGreenhouse block, Key key, IModelBaker baker, boolean inventory) {
		ItemStack camouflageStack = key.world != null ? CamouflageUtil.getCamouflageBlock(key.world, key.pos) : ItemStack.EMPTY;
		IBlockAccess world = key.world;
		BlockPos pos = key.pos;

		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
		if(layer != BlockRenderLayer.CUTOUT){
			if (!camouflageStack.isEmpty()) {
				ICamouflageHandler camouflageHandler = CamouflageUtil.getCamouflageHandler(world, pos);
				ICamouflagedTile camouflageTile = (ICamouflagedTile) TileUtil.getTile(world, pos, TileEntity.class);
				ICamouflageItemHandler itemHandler = CamouflageManager.camouflageAccess.getHandlerFromItem(camouflageStack);
				if (itemHandler != null && camouflageHandler != null && camouflageTile != null) {
					Pair<IBlockState, IBakedModel> model = itemHandler.getModel(camouflageStack, camouflageHandler, camouflageTile);
	
					baker.addBakedModel(model.getLeft(), model.getRight());
					baker.setParticleSprite(model.getRight().getParticleTexture());
				}
			}
	
			//Bake the default blocks
			else if (block.getGreenhouseType() == BlockGreenhouseType.GLASS) {
				TextureAtlasSprite glassSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null, null, world, pos);
	
				baker.addBlockModel(pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null, null, world, pos), 100);
				baker.setParticleSprite(glassSprite);
			} else {
				TextureAtlasSprite plainSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null, null, world, pos);
	
				baker.addBlockModel(pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null, null, world, pos), 100);
				baker.setParticleSprite(plainSprite);
			}
		} else {
			if (block.getGreenhouseType().hasOverlaySprite) {
				TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
				for (EnumFacing facing : EnumFacing.VALUES) {
					sprite[facing.ordinal()] = BlockGreenhouseType.getSprite(block.getGreenhouseType(), facing, key.state, world, pos);
				}
				baker.addBlockModel(pos, sprite, 101);
			}
		}
	}
}
