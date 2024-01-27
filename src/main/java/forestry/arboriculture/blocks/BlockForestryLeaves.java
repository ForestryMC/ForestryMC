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
package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.core.IToolScoop;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;

public class BlockForestryLeaves extends BlockAbstractLeaves implements BonemealableBlock, EntityBlock {

	public BlockForestryLeaves() {
		super(Block.Properties.of(Material.LEAVES)
				.strength(0.2f)
				.sound(SoundType.GRASS)
				.randomTicks()
				.noOcclusion());
	}

	@Override
	protected ITree getTree(BlockGetter world, BlockPos pos) {
		TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leaves != null) {
			ITree tree = leaves.getTree();
			return tree;
		}

		return null;
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		super.tick(state, world, pos, rand);

		TileLeaves tileLeaves = TileUtil.getTile(world, pos, TileLeaves.class);

		// check leaves tile because they might have decayed
		if (tileLeaves != null && !tileLeaves.isRemoved() && rand.nextFloat() <= 0.1) {
			tileLeaves.onBlockTick(world, pos, state, rand);
		}
	}

	/* TILE ENTITY */
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileLeaves(pos, state);
	}

	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, Level world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune, LootContext.Builder builder) {
		TileLeaves tile = TileUtil.getTile(builder, TileLeaves.class);
		if (tile == null) {
			return;
		}

		ITree tree = tile.getTree();
		if (tree == null) {
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);

		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(TreeManager.treeRoot.getTypes().createStack(sapling, EnumGermlingType.SAPLING));
			}
		}

		// Add fruits
		if (tile.hasFruit()) {
			drops.addAll(tree.produceStacks(world, pos, tile.getRipeningTime()));
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leaves != null) {
			IButterfly caterpillar = leaves.getCaterpillar();
			ItemStack heldItem = player.getItemInHand(hand);
			ItemStack otherHand = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
			if (heldItem.isEmpty() && otherHand.isEmpty()) {
				if (leaves.hasFruit() && leaves.getRipeness() >= 0.9F) {
					PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, state);
					NetworkUtil.sendNetworkPacket(packet, pos, world);
					for (ItemStack fruit : leaves.pickFruit(ItemStack.EMPTY)) {
						ItemHandlerHelper.giveItemToPlayer(player, fruit);
					}
					return InteractionResult.SUCCESS;
				}
			} else if (heldItem.getItem() instanceof IToolScoop && caterpillar != null) {
				ItemStack butterfly = ButterflyManager.butterflyRoot.getTypes().createStack(caterpillar, EnumFlutterType.CATERPILLAR);
				ItemStackUtil.dropItemStackAsEntity(butterfly, world, pos);
				leaves.setCaterpillar(null);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	/* IGrowable */

	@Override
	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
		return leafTile != null && leafTile.hasFruit() && leafTile.getRipeness() < 1.0f;
	}

	@Override
	public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leafTile != null) {
			leafTile.addRipeness(0.5f);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable BlockGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (worldIn != null && pos != null) {
			TileLeaves leaves = TileUtil.getTile(worldIn, pos, TileLeaves.class);
			if (leaves != null) {
				if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
					return leaves.getFruitColour();
				} else {
					Player thePlayer = Minecraft.getInstance().player;
					return leaves.getFoliageColour(thePlayer);
				}
			}
		}
		return ModuleArboriculture.proxy.getFoliageColorDefault();
	}
}
