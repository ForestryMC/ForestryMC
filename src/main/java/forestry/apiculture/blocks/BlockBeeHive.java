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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveDrop;
import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.apiculture.hives.IHiveTile;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.tiles.TileHive;
import forestry.core.tiles.TileUtil;

import org.jetbrains.annotations.Nullable;

public class BlockBeeHive extends BaseEntityBlock {

	private final HiveType type;

	public BlockBeeHive(HiveType type) {
		super(Properties.of(MaterialBeehive.BEEHIVE_WORLD)
				.lightLevel((state) -> 7)
				.strength(2.5f));
		this.type = type;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileHive(pos, state);
	}

	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		super.attack(state, world, pos, player);
		TileUtil.actOnTile(world, pos, IHiveTile.class, tile -> tile.onAttack(world, pos, player));
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		super.playerWillDestroy(world, pos, state, player);
		boolean canHarvest = canHarvestBlock(state, world, pos, player);
		TileUtil.actOnTile(world, pos, IHiveTile.class, tile -> tile.onBroken(world, pos, player, canHarvest));
	}

	private List<IHiveDrop> getDropsForHive() {
		String hiveName = type.getHiveUid();
		if (hiveName.equals(IHiveRegistry.HiveType.SWARM.getHiveUid())) {
			return Collections.emptyList();
		}
		return ModuleApiculture.getHiveRegistry().getDrops(hiveName);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		BlockPos pos = new BlockPos(builder.getParameter(LootContextParams.ORIGIN));
		ItemStack tool = builder.getParameter(LootContextParams.TOOL);
		int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
		ServerLevel world = builder.getLevel();
		return getDrops(world, pos, fortune);
	}

	private NonNullList<ItemStack> getDrops(BlockGetter world, BlockPos pos, int fortune) {
		NonNullList<ItemStack> drops = NonNullList.create();
		Random random = world instanceof Level ? ((Level) world).getRandom() : RANDOM;

		List<IHiveDrop> hiveDrops = getDropsForHive();
		Collections.shuffle(hiveDrops);

		// Grab a princess
		int tries = 0;
		boolean hasPrincess = false;
		while (tries <= 10 && !hasPrincess) {
			tries++;

			for (IHiveDrop drop : hiveDrops) {
				if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
					IBee bee = drop.getBeeType(world, pos);
					if (random.nextFloat() < drop.getIgnobleChance(world, pos, fortune)) {
						bee.setIsNatural(false);
					}

					ItemStack princess = BeeManager.beeRoot.getTypes().createStack(bee, EnumBeeType.PRINCESS);
					drops.add(princess);
					hasPrincess = true;
					break;
				}
			}
		}

		// Grab drones
		for (IHiveDrop drop : hiveDrops) {
			if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
				IBee bee = drop.getBeeType(world, pos);
				ItemStack drone = BeeManager.beeRoot.getTypes().createStack(bee, EnumBeeType.DRONE);
				drops.add(drone);
				break;
			}
		}

		// Grab anything else on offer
		for (IHiveDrop drop : hiveDrops) {
			if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
				drops.addAll(drop.getExtraItems(world, pos, fortune));
				break;
			}
		}
		return drops;
	}

	public HiveType getType() {
		return type;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 5;
	}
}
