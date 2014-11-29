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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.core.config.Config;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBeehives extends BlockContainer {

	public BlockBeehives() {
		super(new MaterialBeehive(true));
		setLightLevel(0.8f);
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileSwarm();
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		if (canHarvestBlock(player, world.getBlockMetadata(x, y, z))) {
			// Handle TE'd beehives
			TileEntity tile = world.getTileEntity(x, y, z);

			if (tile instanceof TileSwarm) {
				TileSwarm swarm = (TileSwarm) tile;
				if (swarm.containsBees())
					for (ItemStack beeStack : swarm.contained.getStacks())
						if (beeStack != null)
							StackUtils.dropItemStackAsEntity(beeStack, world, x, y, z);
			}
		}

		return world.setBlockToAir(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		// Handle legacy block
		if (metadata == 0) {
			ret.add(new ItemStack(this));
			return ret;
		}

		List<IHiveDrop> dropList = getDropsForHive(metadata);

		Collections.shuffle(dropList);
		// Grab a princess
		int tries = 0;
		boolean hasPrincess = false;
		while (tries <= 10 && !hasPrincess) {
			tries++;

			for (IHiveDrop drop : dropList)
				if (world.rand.nextInt(100) < drop.getChance(world, x, y, z)) {
					ret.add(drop.getPrincess(world, x, y, z, fortune));
					hasPrincess = true;
					break;
				}
		}

		// Grab drones
		for (IHiveDrop drop : dropList)
			if (world.rand.nextInt(100) < drop.getChance(world, x, y, z)) {
				ret.addAll(drop.getDrones(world, x, y, z, fortune));
				break;
			}
		// Grab anything else on offer
		for (IHiveDrop drop : dropList)
			if (world.rand.nextInt(100) < drop.getChance(world, x, y, z)) {
				ret.addAll(drop.getAdditional(world, x, y, z, fortune));
				break;
			}

		return ret;
	}

	// / CREATIVE INVENTORY
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	private List<IHiveDrop> getDropsForHive(int meta) {
		if (meta == 8) //TODO: make a real hive for swarm
			return BeeManager.hiveDrops[meta];

		String hiveName = getHiveNameForMeta(meta);
		if (hiveName == null)
			return Collections.emptyList();
		return HiveManager.get(hiveName).getDrops();
	}

	private String getHiveNameForMeta(int meta) {
		switch (meta) {
			case 1: return HiveManager.forest;
			case 2: return HiveManager.meadows;
			case 3: return HiveManager.desert;
			case 4: return HiveManager.jungle;
			case 5: return HiveManager.end;
			case 6: return HiveManager.snow;
			case 7: return HiveManager.swamp;
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
		itemList.add(new ItemStack(this, 1, 4));
		if (Config.isDebug) {
			itemList.add(new ItemStack(this, 1 ,5));
		}
		itemList.add(new ItemStack(this, 1, 6));
		itemList.add(new ItemStack(this, 1, 7));
		// Swarm hive not added
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		icons = new IIcon[18];
		for (int i = 1; i < 9; i++) {
			icons[i * 2] = TextureManager.getInstance().registerTex(register, "beehives/beehive." + i + ".top");
			icons[(i * 2) + 1] = TextureManager.getInstance().registerTex(register, "beehives/beehive." + i + ".side");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j) {
		if (j == 0 || j >= 8)
			return null;

		if (i == 0 || i == 1) {
			if (j * 2 < icons.length && icons[j * 2] != null)
				return icons[j * 2];
			else
				return icons[2];
		} else if (j * 2 + 1 < icons.length && icons[j * 2 + 1] != null)
			return icons[j * 2 + 1];
		else
			return icons[3];
	}

}
