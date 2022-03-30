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
package forestry.farming.logic.farmables;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableSapling implements IFarmable {
	protected final ItemStack germling;
	protected final Block saplingBlock;
	protected final ItemStack[] windfall;

	public FarmableSapling(final ItemStack germling, final ItemStack[] windfall) {
		this.germling = germling;
		this.windfall = windfall;
		this.saplingBlock = ItemStackUtil.getBlock(germling);
	}

	@Override
	public boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos) {
		ItemStack copy = germling.copy();
		player.setItemInHand(InteractionHand.MAIN_HAND, copy);
		BlockHitResult result = new BlockHitResult(Vec3.ZERO, Direction.UP, pos.below(), true);    //TODO isInside
		InteractionResult actionResult = copy.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, result));
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		if (actionResult.consumesAction()) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.SoundFXType.BLOCK_PLACE, pos, Blocks.OAK_SAPLING.defaultBlockState());
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			return true;
		}
		return false;
	}

	@Override
	public boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == this.saplingBlock;
	}

	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		if (!blockState.is(BlockTags.LOGS)) {
			return null;
		}

		return new CropDestroy(world, blockState, pos, null);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		//if (ignoreMetadata) {
		return ItemStack.isSame(germling, new ItemStack((itemstack.getItem())));
		/*}
		return ItemStack.isSame(germling, itemstack);*/
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		NonNullList<ItemStack> germlings = NonNullList.create();
		//if (ignoreMetadata) {
		Item germlingItem = germling.getItem();
		CreativeModeTab tab = germlingItem.getItemCategory();
		if (tab != null) {
			germlingItem.fillItemCategory(tab, germlings);
		}
		//}
		if (germlings.isEmpty()) {
			germlings.add(germling);
		}
		info.addSeedlings(germlings);
		info.addProducts(windfall);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack drop : windfall) {
			if (drop.sameItem(itemstack)) {
				return true;
			}
		}
		return false;
	}
}
