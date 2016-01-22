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
package forestry.core.render.model;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import java.util.Collections;
import java.util.List;

import forestry.api.core.IModelBaker;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;

public abstract class ModelBlockOverlay implements ISmartItemModel, ISmartBlockModel {

	protected static void renderBottomFace(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite textureIndex, int mixedBrightness, float r, float g, float b) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.DOWN)) {
			return;
		}
		
		renderer.renderFaceYNeg(pos, textureIndex);

	}

	protected static void renderTopFace(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite textureIndex, int mixedBrightness, float r, float g, float b) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.UP)) {
			return;
		}


		renderer.renderFaceYPos(pos, textureIndex);

	}

	protected static void renderEastFace(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite textureIndex, int mixedBrightness, float r, float g, float b) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.EAST)) {
			return;
		}

		renderer.renderFaceZNeg(pos, textureIndex);

	}

	protected static void renderWestFace(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite textureIndex, int mixedBrightness, float r, float g, float b) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.WEST)) {
			return;
		}

		renderer.renderFaceZPos(pos, textureIndex);

	}

	protected static void renderNorthFace(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite textureIndex, int mixedBrightness, float r, float g, float b) {

		BlockPos posNEW = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.NORTH)) {
			return;
		}

		renderer.renderFaceXNeg(pos, textureIndex);

	}

	protected static void renderSouthFace(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite textureIndex, int mixedBrightness, float r, float g, float b) {

		BlockPos posNEW = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.SOUTH)) {
			return;
		}

		renderer.renderFaceXPos(pos, textureIndex);

	}

	@Override
	public List getFaceQuads(EnumFacing p_177551_1_) {
		return Collections.emptyList();
	}

	@Override
	public List getGeneralQuads() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		IExtendedBlockState extend = (IExtendedBlockState) state;
		IModelBaker renderer = ModelManager.getInstance().createNewRenderer();
		Block blk = state.getBlock();
		IBlockAccess world = extend.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = extend.getValue(UnlistedBlockPos.POS);
		renderer.setRenderBoundsFromBlock(blk);
		renderInWorld(blk, world, pos, renderer);
		return renderer.bakeModel(false);
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		IModelBaker renderer = ModelManager.getInstance().createNewRenderer();
		Block blk = Block.getBlockFromItem(stack.getItem());
		renderer.setRenderBoundsFromBlock(blk);
		renderInventory(blk, stack, renderer);
		return renderer.bakeModel(true);
	}

	protected abstract void renderInventory(Block block, ItemStack item, IModelBaker renderer);

	protected abstract boolean renderInWorld(Block block, IBlockAccess world, BlockPos pos, IModelBaker renderer);

}
