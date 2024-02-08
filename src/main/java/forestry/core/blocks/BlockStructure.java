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
package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.circuits.ISocketable;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.InventoryUtil;

public abstract class BlockStructure extends BlockForestry {

	protected BlockStructure(Block.Properties properties) {
		super(properties.strength(1f));
	}

	protected long previousMessageTick = 0;

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		if (playerIn.isShiftKeyDown()) { //isSneaking
			return InteractionResult.PASS;
		}

		MultiblockTileEntityForestry part = TileUtil.getTile(worldIn, pos, MultiblockTileEntityForestry.class);
		if (part == null) {
			return InteractionResult.FAIL;
		}
		IMultiblockController controller = part.getMultiblockLogic().getController();

		ItemStack heldItem = playerIn.getItemInHand(hand);
		// If the player's hands are empty and they right-click on a multiblock, they get a
		// multiblock-debugging message if the machine is not assembled.
		if (heldItem.isEmpty()) {
			if (controller != null) {
				if (!controller.isAssembled()) {
					String validationError = controller.getLastValidationError();
					if (validationError != null) {
						long tick = worldIn.getGameTime();
						if (tick > previousMessageTick + 20) {
							playerIn.sendSystemMessage(Component.literal(validationError));
							previousMessageTick = tick;
						}
						return InteractionResult.SUCCESS;
					}
				}
			} else {
				playerIn.sendSystemMessage(Component.translatable("for.multiblock.error.notConnected"));
				return InteractionResult.SUCCESS;
			}
		}

		// Don't open the GUI if the multiblock isn't assembled
		if (controller == null || !controller.isAssembled()) {
			return InteractionResult.PASS;
		}

		if (!worldIn.isClientSide) {
			part.openGui((ServerPlayer) playerIn, pos);    //TODO cast is safe because on server?
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (world.isClientSide) {
			return;
		}

		if (placer instanceof Player) {
			TileUtil.actOnTile(world, pos, MultiblockTileEntityForestry.class, tile -> {
				Player player = (Player) placer;
				GameProfile gameProfile = player.getGameProfile();
				tile.setOwner(gameProfile);
			});
		}
	}

	@Override
	public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		if (world.isClientSide) {
			return;
		}

		TileUtil.actOnTile(world, pos, IMultiblockComponent.class, tile -> {
			// drop inventory if we're the last part remaining
			if (MultiblockUtil.getNeighboringParts(world, tile).isEmpty()) {
				if (tile instanceof Container) {
					Containers.dropContents(world, pos, (Container) tile);
				}
				if (tile instanceof ISocketable) {
					InventoryUtil.dropSockets((ISocketable) tile, world, pos);
				}
			}
		});

		super.playerDestroy(world, player, pos, state, te, stack);
	}

}
