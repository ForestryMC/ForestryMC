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
package forestry.arboriculture.gadgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLog extends Block implements IWoodTyped {

	public enum LogCat {
		CAT0, CAT1, CAT2, CAT3, CAT4, CAT5, CAT6, CAT7
	}

	protected final LogCat cat;

	public BlockLog(LogCat cat) {
		this(cat, Material.wood);
	}

	protected BlockLog(LogCat cat, Material material) {
		super(material);
		this.cat = cat;

		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
	}

	public static int getTypeFromMeta(int damage) {
		return damage & 3;
	}

	@Override
	public int getRenderType() {
		return Blocks.log.getRenderType();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

		byte radius = 4;
		int boundary = radius + 1;

		if (world.checkChunksExist(x - boundary, y - boundary, z - boundary, x + boundary, y + boundary, z + boundary))
			for (int i = -radius; i <= radius; ++i)
				for (int j = -radius; j <= radius; ++j)
					for (int k = -radius; k <= radius; ++k) {
						Block neighbor = world.getBlock(x + i, y + j, z + k);

						neighbor.beginLeavesDecay(world, x + i, y + j, z + k);
					}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float par6, float par7, float par8, int meta) {
		int type = getTypeFromMeta(meta);
		byte b0 = 0;

		switch (side) {
		case 0:
		case 1:
			b0 = 0;
			break;
		case 2:
		case 3:
			b0 = 8;
			break;
		case 4:
		case 5:
			b0 = 4;
		}

		return type | b0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		if(cat == LogCat.CAT6) {
			itemList.add(new ItemStack(this, 1, 0));
			return;
		}

		for (int i = 0; i < 4; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		WoodType.registerIcons(register);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {

		int oriented = meta & 12;

		WoodType type = getWoodType(meta);
		if (type == null)
			return null;

		switch (oriented) {
		case 4:
			if (side > 3)
				return type.getHeartIcon();
			else
				return type.getBarkIcon();
		case 8:
			if (side == 2 || side == 3)
				return type.getHeartIcon();
			else
				return type.getBarkIcon();
		case 0:
		default:
			if (side < 2)
				return type.getHeartIcon();
			else
				return type.getBarkIcon();
		}
	}

	@Override
	public int damageDropped(int meta) {
		return getTypeFromMeta(meta);
	}

	@Override
	protected ItemStack createStackedBlock(int meta) {
		return new ItemStack(this, 1, getTypeFromMeta(meta));
	}

	/* PROPERTIES */
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return getWoodType(world.getBlockMetadata(x, y, z)).getHardness();
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 20;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if (face == ForgeDirection.DOWN)
			return 20;
		else if (face != ForgeDirection.UP)
			return 10;
		else
			return 5;
	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean isWood(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public WoodType getWoodType(int meta) {
		meta = getTypeFromMeta(meta);
		if(meta + cat.ordinal() * 4 < WoodType.VALUES.length)
			return WoodType.VALUES[meta + cat.ordinal() * 4];
		else
			return null;
	}

	@Override
	public String getBlockKind() {
		return "log";
	}

}
