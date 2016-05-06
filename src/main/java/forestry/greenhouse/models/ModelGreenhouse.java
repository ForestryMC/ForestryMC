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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockDefault;
import forestry.core.utils.CamouflageUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;

public class ModelGreenhouse extends ModelBlockDefault<BlockGreenhouse> {

	public ModelGreenhouse() {
		super(BlockGreenhouse.class);
	}

	@Override
	protected void bakeInventoryBlock(@Nonnull BlockGreenhouse block, @Nonnull ItemStack item, @Nonnull IModelBaker baker) {
		bakeBlockModel(block, null, null, null, baker, null);
	}

	@Override
	protected void bakeWorldBlock(@Nonnull BlockGreenhouse block, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IExtendedBlockState stateExtended, @Nonnull IModelBaker baker) {
		ItemStack camouflageStack = CamouflageUtil.getCamouflageBlock(world, pos);
		
		bakeBlockModel(block, world, pos, stateExtended, baker, camouflageStack);
	}
	
	private static void bakeBlockModel(@Nonnull BlockGreenhouse block, @Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable IExtendedBlockState stateExtended, @Nonnull IModelBaker baker, @Nullable ItemStack camouflageStack) {
		if (camouflageStack != null) {
			BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
			
			baker.addBakedModel(modelShapes.getModelForState(Block.getBlockFromItem(camouflageStack.getItem()).getStateFromMeta(camouflageStack.getItemDamage())));
			baker.setParticleSprite(modelShapes.getModelForState(Block.getBlockFromItem(camouflageStack.getItem()).getStateFromMeta(camouflageStack.getItemDamage())).getParticleTexture());
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
				sprite[facing.ordinal()] = BlockGreenhouseType.getSprite(block.getGreenhouseType(), facing, stateExtended, world, pos);
			}
			baker.addBlockModel(block, Block.FULL_BLOCK_AABB, pos, sprite, 101);
		}
	}

}
