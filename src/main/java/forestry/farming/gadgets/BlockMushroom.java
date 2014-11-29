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
package forestry.farming.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import forestry.core.IItemTyped;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.worldgen.WorldGenBigMushroom;

public class BlockMushroom extends BlockSapling implements IItemTyped {

	public enum MushroomType {
		BROWN, RED
	}

	private final WorldGenerator[] generators;
	private final ItemStack[] drops;

	public BlockMushroom() {
		super();
		setHardness(0.0f);
		this.generators = new WorldGenerator[] { new WorldGenBigMushroom(Blocks.brown_mushroom_block), new WorldGenBigMushroom(Blocks.red_mushroom_block) };
		this.drops = new ItemStack[] { new ItemStack(Blocks.brown_mushroom), new ItemStack(Blocks.red_mushroom) };
		setCreativeTab(null);
		setTickRandomly(true);
	}

	@Override
	public boolean getTickRandomly() {
		return true;
	}

	// / DROPS
	@Override
	public ArrayList<ItemStack> getDrops(World world, int X, int Y, int Z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		MushroomType type = getTypeFromMeta(metadata);

		ret.add(drops[type.ordinal()]);

		return ret;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.mycelium;
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {

		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(i, j, k);
		MushroomType type = getTypeFromMeta(meta);
		int maturity = meta >> 2;

		tickGermling(world, i, j, k, random, type, maturity);
	}

	private void tickGermling(World world, int i, int j, int k, Random random, MushroomType type, int maturity) {

		int lightvalue = world.getBlockLightValue(i, j + 1, k);

		if (random.nextInt(2) != 0)
			return;

		if (maturity != 3) {
			maturity = 3;
			int matX = maturity << 2;
			int meta = (matX | type.ordinal());
			world.setBlockMetadataWithNotify(i, j, k, meta, Defaults.FLAG_BLOCK_SYNCH);
		} else if (lightvalue <= 7)
			func_149878_d(world, i, j, k, random);
	}

	@Override
	public void func_149878_d(World world, int i, int j, int k, Random random) {
		int type = world.getBlockMetadata(i, j, k) & 0x03;

		if(type >= generators.length)
			return;

		world.setBlockToAir(i, j, k);
		if (!generators[type].generate(world, random, i, j, k))
			world.setBlock(i, j, k, this, type, 0);
	}

	@Override
	public MushroomType getTypeFromMeta(int meta) {
		meta %= 3;
		if (meta >= MushroomType.values().length)
			return null;
		return MushroomType.values()[meta];
	}

	/* ICONS */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		MushroomType type = getTypeFromMeta(meta);

		if (type == MushroomType.BROWN)
			return Blocks.brown_mushroom.getIcon(side, meta);
		else if (type == MushroomType.RED)
			return Blocks.red_mushroom.getIcon(side, meta);
		else
			return null;
	}

}
