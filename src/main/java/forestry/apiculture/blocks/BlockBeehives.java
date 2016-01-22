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

import net.minecraft.block.BlockContainer;
import net.minecraft.block.properties.IProperty;
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

import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.core.IModelManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.tiles.TileSwarm;
import forestry.core.config.Config;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginApiculture;

public class BlockBeehives extends BlockContainer implements IItemModelRegister {
	private static final PropertyEnum HIVETYPES = PropertyEnum.create("hive", HiveType.class);
	
	public BlockBeehives() {
		super(new MaterialBeehive(true));
		setLightLevel(0.8f);
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("scoop", 0);
		setDefaultState(this.blockState.getBaseState().withProperty(HIVETYPES, HiveType.FOREST));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { HIVETYPES });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((HiveType) state.getValue(HIVETYPES)).ordinal() + 1;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(HIVETYPES, HiveType.values()[meta - 1]);
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
		ArrayList<ItemStack> ret = new ArrayList<>();

		List<IHiveDrop> dropList = getDropsForHive(getMetaFromState(state));

		Collections.shuffle(dropList);
		// Grab a princess
		int tries = 0;
		boolean hasPrincess = false;
		while (tries <= 10 && !hasPrincess) {
			tries++;

			for (IHiveDrop drop : dropList) {
				if (RANDOM.nextInt(100) < drop.getChance((World) world, pos)) {
					ret.add(drop.getPrincess((World) world, pos, fortune));
					hasPrincess = true;
					break;
				}
			}
		}

		// Grab drones
		for (IHiveDrop drop : dropList) {
			if (RANDOM.nextInt(100) < drop.getChance((World) world, pos)) {
				ret.addAll(drop.getDrones((World) world, pos, fortune));
				break;
			}
		}
		// Grab anything else on offer
		for (IHiveDrop drop : dropList) {
			if (RANDOM.nextInt(100) < drop.getChance((World) world, pos)) {
				ret.addAll(drop.getAdditional((World) world, pos, fortune));
				break;
			}
		}

		return ret;
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
		switch (meta) {
			case 1:
				return HiveType.FOREST.getHiveName();
			case 2:
				return HiveType.MEADOWS.getHiveName();
			case 3:
				return HiveType.DESERT.getHiveName();
			case 4:
				return HiveType.JUNGLE.getHiveName();
			case 5:
				return HiveType.END.getHiveName();
			case 6:
				return HiveType.SNOW.getHiveName();
			case 7:
				return HiveType.SWAMP.getHiveName();
		}
		return null;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
		itemList.add(new ItemStack(this, 1, 4));
		if (Config.isDebug) {
			itemList.add(new ItemStack(this, 1, 5));
		}
		itemList.add(new ItemStack(this, 1, 6));
		itemList.add(new ItemStack(this, 1, 7));
		// Swarm hive not added
	}

	/* IETM MODELS */
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 1, "beehives/" + HiveType.FOREST.getName());
		manager.registerItemModel(item, 2, "beehives/" + HiveType.MEADOWS.getName());
		manager.registerItemModel(item, 3, "beehives/" + HiveType.DESERT.getName());
		manager.registerItemModel(item, 4, "beehives/" + HiveType.JUNGLE.getName());
		manager.registerItemModel(item, 5, "beehives/" + HiveType.END.getName());
		manager.registerItemModel(item, 6, "beehives/" + HiveType.SNOW.getName());
		manager.registerItemModel(item, 7, "beehives/" + HiveType.SWAMP.getName());
	}
	
    @Override
	public int getRenderType()
    {
        return 3;
    }

}
