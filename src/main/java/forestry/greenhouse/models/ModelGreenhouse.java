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

import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedBlock;
import forestry.api.core.IModelBaker;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.models.ModelBlockOverlay;

public class ModelGreenhouse<G extends TileEntity & IGreenhouseComponent & ICamouflageHandler & ICamouflagedBlock> extends ModelBlockOverlay<BlockGreenhouse> {

	public ModelGreenhouse() {
		super(BlockGreenhouse.class);
	}

	@Override
	protected void bakeInventoryBlock(BlockGreenhouse block, ItemStack item, IModelBaker baker) {
		bakeBlockModel(block, null, null, null, baker, null);
	}

	@Override
	protected void bakeWorldBlock(BlockGreenhouse block, IBlockAccess world, BlockPos pos, IExtendedBlockState stateExtended, IModelBaker baker) {
		bakeBlockModel(block, world, pos, stateExtended, baker, BlockGreenhouse.getCamouflageBlock(world, pos));
	}
	
	private void bakeBlockModel(@Nonnull BlockGreenhouse block, @Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable IExtendedBlockState stateExtended, @Nonnull IModelBaker baker, @Nullable ItemStack camouflageBlock){
		if(camouflageBlock != null){
			BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
			
			baker.addBakedModel(modelShapes.getModelForState(Block.getBlockFromItem(camouflageBlock.getItem()).getStateFromMeta(camouflageBlock.getItemDamage())));
		}
		
		//Bake the default blocks
		else if(block.getGreenhouseType() == BlockGreenhouseType.GLASS){
			TextureAtlasSprite glassSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null, null, world, pos);
			
			baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null, null, world, pos), 100);
			baker.setParticleSprite(glassSprite);
		}else{
			TextureAtlasSprite plainSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null, null, world, pos);
			
			baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null, null, world, pos), 100);
			baker.setParticleSprite(plainSprite);
		}
		if(block.getGreenhouseType().hasOverlaySprite){
			TextureAtlasSprite[] sprite = new TextureAtlasSprite[6];
			for(EnumFacing facing : EnumFacing.VALUES){
				sprite[facing.ordinal()] =  BlockGreenhouseType.getSprite(block.getGreenhouseType(), facing, stateExtended, world, pos);
			}
			baker.addBlockModel(block, pos, sprite, 101);
		}
	}

}
