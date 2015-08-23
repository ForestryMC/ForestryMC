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
package forestry.apiculture.gadgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IHiveDrop;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.config.Config;
import forestry.core.gadgets.BlockResourceStorageBlock.Resources;
import forestry.core.inventory.InvTools;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginApiculture;

public class BlockBeehives extends BlockContainer implements IModelObject, IVariantObject {

	public static final PropertyEnum HIVES = PropertyEnum.create("hives", BeeHives.class);
	
	public enum BeeHives implements IStringSerializable
	{
		LEGACY,
		FOREST,
		MEADOWS,
		DESERT,
		JUNGLE,
		END,
		SNOW,
		SWAMP;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
	}
	
	public BlockBeehives() {
		super(new MaterialBeehive(true));
		setLightLevel(0.8f);
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("scoop", 0);
		setDefaultState(this.blockState.getBaseState().withProperty(HIVES, BeeHives.LEGACY));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, HIVES);
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
					for (ItemStack beeStack : InvTools.getStacks(swarm.contained)) {
						if (beeStack != null) {
							StackUtils.dropItemStackAsEntity(beeStack, world, pos);
						}
					}
				}
			}
		}

		return world.setBlockToAir(pos);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();

		int meta = getMetaFromState(state);
		
		// Handle legacy block
		if (meta == 0) {
			ret.add(new ItemStack(this));
			return ret;
		}

		List<IHiveDrop> dropList = getDropsForHive(state);

		Collections.shuffle(dropList);
		// Grab a princess
		int tries = 0;
		boolean hasPrincess = false;

		while (tries <= 10 && !hasPrincess) {
			tries++;

			for (IHiveDrop drop : dropList) {
				if (RANDOM.nextInt(100) < drop.getChance(world, pos)) {
					ret.add(drop.getPrincess(world, pos, fortune));
					hasPrincess = true;
					break;
				}
			}
		}

		// Grab drones
		for (IHiveDrop drop : dropList) {
			if (RANDOM.nextInt(100) < drop.getChance(world, pos)) {
				ret.addAll(drop.getDrones(world, pos, fortune));
				break;
			}
		}
		// Grab anything else on offer
		for (IHiveDrop drop : dropList) {
			if (RANDOM.nextInt(100) < drop.getChance(world, pos)) {
				ret.addAll(drop.getAdditional(world, pos, fortune));
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

	private List<IHiveDrop> getDropsForHive(IBlockState state) {
		String hiveName = getHiveNameForState(state);
		if (hiveName == null) {
			return Collections.emptyList();
		}
		return PluginApiculture.hiveRegistry.getDrops(hiveName);
	}

	private String getHiveNameForState(IBlockState state) {
		int meta = getMetaFromState(state);
		switch (meta) {
			case 1:
				return HiveRegistry.forest;
			case 2:
				return HiveRegistry.meadows;
			case 3:
				return HiveRegistry.desert;
			case 4:
				return HiveRegistry.jungle;
			case 5:
				return HiveRegistry.end;
			case 6:
				return HiveRegistry.snow;
			case 7:
				return HiveRegistry.swamp;
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
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((BeeHives)state.getValue(HIVES)).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(HIVES, meta);
	}

	@Override
	public String[] getVariants() {
		return new String[]{"legacy", "forest", "meadows", "desert", "jungle", "end", "snow", "swamp"};
	}

	@Override
	public ModelType getModelType() {
		return ModelType.META;
	}

}
