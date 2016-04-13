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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.tiles.TileSwarm;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.config.Config;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockBeeHives extends BlockContainer implements IItemModelRegister, IBlockWithMeta {
	private static final PropertyEnum<HiveType> HIVE_TYPES = PropertyEnum.create("hive", HiveType.class);
	
	public BlockBeeHives() {
		super(new MaterialBeehive(true));
		setLightLevel(0.8f);
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("scoop", 0);
		setDefaultState(this.blockState.getBaseState().withProperty(HIVE_TYPES, HiveType.FOREST));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, HIVE_TYPES);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(HIVE_TYPES).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(HIVE_TYPES, HiveType.VALUES[meta]);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileSwarm();
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity) {
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (canHarvestBlock(world, pos, player)) {
			// Handle TE'd beehives
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof TileSwarm) {
				TileSwarm swarm = (TileSwarm) tile;
				if (swarm.containsBees()) {
					for (ItemStack beeStack : InventoryUtil.getStacks(swarm.contained)) {
						if (beeStack != null) {
							ItemStackUtil.dropItemStackAsEntity(beeStack, world, pos);
						}
					}
				}
			}
		}

		return world.setBlockToAir(pos);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<>();

		Random random = world instanceof World ? ((World) world).rand : RANDOM;

		List<IHiveDrop> hiveDrops = getDropsForHive(getMetaFromState(state));
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

					ItemStack princess = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.PRINCESS);
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
				ItemStack drone = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.DRONE);
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

	// / CREATIVE INVENTORY
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	private static List<IHiveDrop> getDropsForHive(int meta) {
		String hiveName = getHiveNameForMeta(meta);
		if (hiveName == null) {
			return Collections.emptyList();
		}
		return PluginApiculture.hiveRegistry.getDrops(hiveName);
	}

	private static String getHiveNameForMeta(int meta) {
		return HiveType.VALUES[meta].getHiveName();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
		if (Config.isDebug) {
			itemList.add(new ItemStack(this, 1, 4));
		}
		itemList.add(new ItemStack(this, 1, 5));
		itemList.add(new ItemStack(this, 1, 6));
		// Swarm hive not added
	}

	/* ITEM MODELS */
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for(int i = 0;i < HiveType.VALUES.length;i++){
			manager.registerItemModel(item, i, "beehives/" + HiveType.VALUES[i].getName());
		}
	}
	
	@Override
	public int getRenderType() {
		return 3;
	}
	
	@Override
	public String getNameFromMeta(int meta) {
		return HiveType.VALUES[meta].getName();
	}

}
