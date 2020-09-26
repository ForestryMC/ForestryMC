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
import forestry.core.items.ItemScoop;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockBeeHive extends ContainerBlock {
    private final HiveType type;

    public BlockBeeHive(HiveType type) {
        super(Properties.create(MaterialBeehive.BEEHIVE_WORLD)
                .setLightLevel((state) -> {
                    return (int) (0.4f * 15);
                })
                .hardnessAndResistance(2.5f)
                .harvestLevel(0)
                .harvestTool(ItemScoop.SCOOP));
        this.type = type;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileHive();
    }

    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        TileUtil.actOnTile(world, pos, IHiveTile.class, tile -> tile.onAttack(world, pos, player));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(world, pos, state, player);
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
        BlockPos pos = builder.assertPresent(LootParameters.BLOCK_ENTITY).getPos();
        ItemStack tool = builder.assertPresent(LootParameters.TOOL);
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
        ServerWorld world = builder.getWorld();
        return getDrops(world, pos, fortune);
    }

    private NonNullList<ItemStack> getDrops(IBlockReader world, BlockPos pos, int fortune) {
        NonNullList<ItemStack> drops = NonNullList.create();
        Random random = world instanceof World ? ((World) world).rand : RANDOM;

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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 5;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 5;
    }
}
