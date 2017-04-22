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
package forestry.core;

import com.google.common.base.Preconditions;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.core.ICamouflagedTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class CamouflageHandlerDoor implements ICamouflageItemHandler {

	@Override
	public boolean canHandle(ItemStack stack) {
		return stack.getItem() instanceof ItemDoor;
	}

	@Override
	public String getType() {
		return CamouflageManager.DOOR;
	}

	@Override
	public float getLightTransmittance(ItemStack stack, ICamouflageHandler camouflageHandler) {
		return 0.25F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Pair<IBlockState, IBakedModel> getModel(ItemStack stack, ICamouflageHandler camouflageHandler, ICamouflagedTile camouflageTile) {
		Preconditions.checkNotNull(camouflageHandler);
		Preconditions.checkArgument(stack.getItem() instanceof ItemDoor, "Item must be a door");

		World world = camouflageHandler.getWorldObj();
		BlockPos pos = camouflageTile.getCoordinates();
		IBlockState blockState = world.getBlockState(pos);
		IBlockState currentState = blockState.getActualState(world, pos);

		ItemDoor itemDoor = (ItemDoor) stack.getItem();
		Block doorBlock = ObfuscationReflectionHelper.getPrivateValue(ItemDoor.class, itemDoor, 0);

		if (doorBlock == null) {
			throw new IllegalStateException("Can't render Door : block is null for" + stack);
		}
		IBlockState doorState = doorBlock.getDefaultState()
				.withProperty(BlockDoor.FACING, currentState.getValue(BlockDoor.FACING))
				.withProperty(BlockDoor.HINGE, currentState.getValue(BlockDoor.HINGE))
				.withProperty(BlockDoor.OPEN, currentState.getValue(BlockDoor.OPEN))
				.withProperty(BlockDoor.POWERED, currentState.getValue(BlockDoor.POWERED))
				.withProperty(BlockDoor.HALF, currentState.getValue(BlockDoor.HALF));

		BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();

		return Pair.of(doorState, modelShapes.getModelForState(doorState));
	}

}
