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
package forestry.apiculture.blocks;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.config.Constants;
import forestry.modules.features.FeatureBlock;

public class BlockStump extends TorchBlock {

	public BlockStump() {
		super(Block.Properties.of(Material.DECORATION)
				.strength(0.0f)
				.sound(SoundType.WOOD), ParticleTypes.FLAME);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		return useStump(ApicultureBlocks.CANDLE, state, worldIn, pos, playerIn, hand);
	}

	public static InteractionResult useStump(FeatureBlock<?, ?> featureBlock, BlockState oldState, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand) {
		ItemStack heldItem = playerIn.getItemInHand(hand);
		if (BlockCandle.lightingItems.contains(heldItem.getItem())) {
			BlockState activatedState = featureBlock.with(BlockCandle.STATE, BlockCandle.State.ON);
			if (activatedState.hasProperty(WallTorchBlock.FACING)) {
				activatedState = activatedState.setValue(WallTorchBlock.FACING, oldState.getValue(WallTorchBlock.FACING));
			}
			worldIn.setBlock(pos, activatedState, Constants.FLAG_BLOCK_SYNC);
			TileCandle candle = new TileCandle(pos, activatedState);
			candle.setColour(DyeColor.WHITE.getTextColor()); // default to white
			candle.setLit(true);
			worldIn.setBlockEntity(candle);
			worldIn.playSound(playerIn, pos, heldItem.getItem() == Items.FLINT_AND_STEEL ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.75F, worldIn.random.nextFloat() * 0.4F + 0.8F);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		// Empty for remove flame particles
	}
}
