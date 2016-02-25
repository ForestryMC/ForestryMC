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
package forestry.core.models;

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
import forestry.core.models.baker.ModelBaker;

/**
 * A overlay block model to make a block with 2 or more texture layers
 */
public abstract class ModelBlockOverlay<B extends Block> implements ISmartItemModel, ISmartBlockModel {

	protected void renderBottomFace(IBlockAccess world, B block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.DOWN)) {
			return;
		}
		
		baker.renderFaceYNeg(pos, sprite);

	}

	protected void renderTopFace(IBlockAccess world, B block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.UP)) {
			return;
		}


		baker.renderFaceYPos(pos, sprite);

	}

	protected void renderEastFace(IBlockAccess world, B block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.EAST)) {
			return;
		}

		baker.renderFaceZNeg(pos, sprite);

	}

	protected void renderWestFace(IBlockAccess world, B block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {

		BlockPos posNEW = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.WEST)) {
			return;
		}

		baker.renderFaceZPos(pos, sprite);

	}

	protected void renderNorthFace(IBlockAccess world, B block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {

		BlockPos posNEW = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.NORTH)) {
			return;
		}

		baker.renderFaceXNeg(pos, sprite);

	}

	protected void renderSouthFace(IBlockAccess world, B block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {

		BlockPos posNEW = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());

		if (!block.shouldSideBeRendered(world, posNEW, EnumFacing.SOUTH)) {
			return;
		}

		baker.renderFaceXPos(pos, sprite);

	}

	@Override
	public List getFaceQuads(EnumFacing face) {
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
		IModelBaker baker = ModelBaker.getInstance();
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		
		IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
		Block block = state.getBlock();
		
		baker.setRenderBoundsFromBlock(block);
		try{
			renderInWorld((B) block, world, pos, baker);
		}catch(Exception e){
			return null;
		}
		
		return baker.bakeModel(false);
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		IModelBaker baker = ModelBaker.getInstance();
		Block block = Block.getBlockFromItem(stack.getItem());
		
		block.setBlockBoundsForItemRender();
		baker.setRenderBoundsFromBlock(block);
		try{
			renderInventory((B) block, stack, baker);
		}catch(Exception e){
			return null;
		}
		
		return baker.bakeModel(true);
	}
	
	protected abstract void renderInventory(B block, ItemStack item, IModelBaker baker);

	protected abstract boolean renderInWorld(B block, IBlockAccess world, BlockPos pos, IModelBaker baker);

}
