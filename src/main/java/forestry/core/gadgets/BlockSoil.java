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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import forestry.core.IItemTyped;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

/**
 * Humus, bog earth, peat
 */
public class BlockSoil extends Block implements IItemTyped {

	public enum SoilType {
		HUMUS, BOG_EARTH, PEAT
	}

	private static final int degradeDelimiter = 3;

	public BlockSoil() {
		super(Material.sand);
		setTickRandomly(true);
		setHardness(0.5f);
		setStepSound(soundTypeGrass);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public int tickRate(World world) {
		return 500;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		SoilType type = getTypeFromMeta(metadata);

		if (type == SoilType.PEAT) {
			ret.add(ForestryItem.peat.getItemStack());
			ret.add(new ItemStack(Blocks.dirt));
		} else if (type == SoilType.HUMUS)
			ret.add(new ItemStack(Blocks.dirt));
		else
			ret.add(new ItemStack(this, 1, SoilType.BOG_EARTH.ordinal()));

		return ret;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return (world.getBlockMetadata(x, y, z) & 0x03);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(i, j, k);

		SoilType type = getTypeFromMeta(meta);

		if (type == SoilType.HUMUS)
			updateTickHumus(world, i, j, k, random);
		else if (type == SoilType.BOG_EARTH)
			updateTickBogEarth(world, i, j, k, random);
	}

	private void updateTickHumus(World world, int i, int j, int k, Random random) {
		if (isEnrooted(world, i, j, k))
			degradeSoil(world, i, j, k);
	}

	private void updateTickBogEarth(World world, int i, int j, int k, Random random) {
		if (isMoistened(world, i, j, k))
			matureBog(world, i, j, k);
	}

	/**
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private boolean isEnrooted(World world, int x, int y, int z) {

		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) {
				Block block = world.getBlock(x + i, y + 1, z + j);
				if (block == Blocks.log || block == Blocks.sapling || block == ForestryBlock.saplingGE.block())
					// We are not returning true if we are the base of a
					// sapling.
					if (i == 0 && j == 0)
						return false;
					else
						return true;
			}

		return false;
	}

	/**
	 * If a tree or sapling is in the vicinity, there is a chance, that the soil will degrade.
	 * 
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 */
	private void degradeSoil(World world, int i, int j, int k) {

		if (world.rand.nextInt(140) != 0)
			return;

		int meta = world.getBlockMetadata(i, j, k);

		// Unpack first
		int type = meta & 0x03;
		int grade = meta >> 2;

		// Increment (de)gradation
		grade++;

		// Repackage in format TTGG
		meta = (grade << 2 | type);

		if (grade >= degradeDelimiter)
			world.setBlock(i, j, k, Blocks.sand, 0, Defaults.FLAG_BLOCK_SYNCH);
		else
			world.setBlockMetadataWithNotify(i, j, k, meta, Defaults.FLAG_BLOCK_SYNCH);
		world.markBlockForUpdate(i, j, k);
	}

	public static boolean isMoistened(World world, int x, int y, int z) {

		for (int i = -2; i < 3; i++)
			for (int j = -2; j < 3; j++) {
				Block block = world.getBlock(x + i, y, z + j);
				if (block == Blocks.water || block == Blocks.flowing_water)
					return true;
			}

		return false;
	}

	private void matureBog(World world, int i, int j, int k) {

		if (world.rand.nextInt(13) != 0)
			return;

		int meta = world.getBlockMetadata(i, j, k);

		// Unpack first

		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (maturity >= this.degradeDelimiter)
			return;

		// Increment (de)gradation
		maturity++;

		meta = (maturity << 2 | type);
		world.setBlockMetadataWithNotify(i, j, k, meta, Defaults.FLAG_BLOCK_SYNCH);
		world.markBlockForUpdate(i, j, k);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plant) {
		EnumPlantType plantType = plant.getPlantType(world, x, y, z);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains)
			return false;

		int meta = world.getBlockMetadata(x, y, z);
		SoilType type = getTypeFromMeta(meta);

		return type == SoilType.HUMUS;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	public SoilType getTypeFromMeta(int meta) {
		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (type == 1)
			if (maturity < degradeDelimiter)
				return SoilType.BOG_EARTH;
			else
				return SoilType.PEAT;
		else
			return SoilType.HUMUS;
	}

	// / CREATIVE INVENTORY
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon iconHumus;
	@SideOnly(Side.CLIENT)
	private IIcon iconBogEarth;
	@SideOnly(Side.CLIENT)
	private IIcon iconPeat;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		iconHumus = TextureManager.getInstance().registerTex(register, "soil/humus");
		iconBogEarth = TextureManager.getInstance().registerTex(register, "soil/bog");
		iconPeat = TextureManager.getInstance().registerTex(register, "soil/peat");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {

		SoilType type = getTypeFromMeta(meta);

		switch (type) {
			case HUMUS: return iconHumus;
			case BOG_EARTH: return iconBogEarth;
			case PEAT: return iconPeat;
		}
		return null;
	}

}
