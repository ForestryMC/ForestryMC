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
package forestry.greenhouse.camouflage;


import com.google.common.base.Preconditions;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;

public class CamouflageHandlerBlock implements ICamouflageItemHandler {
	@Override
	public boolean canHandle(ItemStack stack) {
		if (stack.isEmpty() || Block.getBlockFromItem(stack.getItem()) == Blocks.AIR) {
			return false;
		}
		Block block = Block.getBlockFromItem(stack.getItem());
		IBlockState stateFromMeta = block.getStateFromMeta(stack.getItemDamage());

		return stateFromMeta.isOpaqueCube() && !block.hasTileEntity(stateFromMeta) && stateFromMeta.isNormalCube() && stateFromMeta.isFullCube() && stateFromMeta.isBlockNormalCube();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Pair<IBlockState, IBakedModel> getModel(ItemStack stack, ICamouflageHandler camouflageHandler, ICamouflagedTile camouflageTile) {
		Preconditions.checkNotNull(camouflageHandler);
		Preconditions.checkArgument(!stack.isEmpty(), "Stack cannot be empty");

		BlockRendererDispatcher rendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockModelShapes modelShapes = rendererDispatcher.getBlockModelShapes();
		Block block = Block.getBlockFromItem(stack.getItem());
		Preconditions.checkArgument(block != Blocks.AIR, "Block cannot be AIR");

		IBlockState state = block.getStateFromMeta(stack.getItemDamage());

		return Pair.of(state, modelShapes.getModelForState(state));
	}

}
