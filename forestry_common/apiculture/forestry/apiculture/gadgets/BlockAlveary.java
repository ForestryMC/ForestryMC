/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.gadgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.core.gadgets.BlockStructure;
import forestry.core.render.TextureManager;

public class BlockAlveary extends BlockStructure {

	public BlockAlveary() {
		super(new MaterialBeehive(false));
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 8; i++) {
			if (i == 1)
				continue;
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
		drop.add(new ItemStack(this, 1, metadata != 1 ? metadata : 0));
		return drop;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta != 1 ? meta : 0;
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
		case TileAlvearySwarmer.BLOCK_META:
			return new TileAlvearySwarmer();
		case TileAlvearyFan.BLOCK_META:
			return new TileAlvearyFan();
		case TileAlvearyHeater.BLOCK_META:
			return new TileAlvearyHeater();
		case TileAlvearyHygroregulator.BLOCK_META:
			return new TileAlvearyHygroregulator();
		case TileAlvearyStabiliser.BLOCK_META:
			return new TileAlvearyStabiliser();
		case TileAlvearySieve.BLOCK_META:
			return new TileAlvearySieve();
		default:
			return new TileAlvearyPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, meta);
	}

	/* ICONS */
	public static final int PLAIN = 0;
	public static final int ENTRANCE = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int TX_55_SWOF = 5;
	public static final int TX_56_SWON = 6;
	public static final int TX_57_HTOF = 7;
	public static final int TX_58_HTON = 8;
	public static final int TX_71_FNOF = 9;
	public static final int TX_72_FNON = 10;
	public static final int TX_73_VLVE = 11;
	public static final int STABILISER = 12;
	public static final int SIEVE = 13;

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		icons = new IIcon[14];
		icons[0] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.plain");
		icons[1] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.entrance");
		icons[2] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.bottom");
		icons[3] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.left");
		icons[4] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.right");
		icons[5] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.swarmer.off");
		icons[6] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.swarmer.on");
		icons[7] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.heater.off");
		icons[8] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.heater.on");
		icons[9] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.fan.off");
		icons[10] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.fan.on");
		icons[11] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.valve");
		icons[12] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.stabiliser");
		icons[13] = TextureManager.getInstance().registerTex(register, "apiculture/alveary.sieve");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		if ((metadata <= 1
				|| metadata == TileAlvearySieve.BLOCK_META || metadata == TileAlvearySwarmer.BLOCK_META || metadata == TileAlvearyStabiliser.BLOCK_META)
				&& (side == 1 || side == 0))
			return icons[BOTTOM];

		switch (metadata) {
		case 1:
			return icons[ENTRANCE];
		case TileAlvearySwarmer.BLOCK_META:
			return icons[TX_55_SWOF];
		case TileAlvearyFan.BLOCK_META:
			return icons[TX_71_FNOF];
		case TileAlvearyHeater.BLOCK_META:
			return icons[TX_57_HTOF];
		case TileAlvearyHygroregulator.BLOCK_META:
			return icons[TX_73_VLVE];
		case TileAlvearyStabiliser.BLOCK_META:
			return icons[STABILISER];
		case TileAlvearySieve.BLOCK_META:
			return icons[SIEVE];
		default:
			return icons[PLAIN];
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 1)
			return this.getIcon(side, meta);
		else if (meta > 1)
			return getBlockTextureFromSideAndTile(world, x, y, z, side);

		Block blockXP = world.getBlock(x + 1, y, z);
		Block blockXM = world.getBlock(x - 1, y, z);

		if (blockXP == this && blockXM != this) {

			if (world.getBlockMetadata(x + 1, y, z) == 1) {

				if (world.getBlock(x, y, z + 1) != this)
					return switchForSide(42, side);
				else
					return switchForSide(41, side);

			} else
				return this.getIcon(side, meta);

		} else if (blockXP != this && blockXM == this)
			if (world.getBlockMetadata(x - 1, y, z) == 1) {

				if (world.getBlock(x, y, z + 1) != this)
					return switchForSide(41, side);
				else
					return switchForSide(42, side);

			} else
				return this.getIcon(side, meta);

		return this.getIcon(side, meta);
	}

	@SideOnly(Side.CLIENT)
	private IIcon getBlockTextureFromSideAndTile(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null || !(tile instanceof TileAlveary))
			return getIcon(side, 0);

		return icons[((TileAlveary) tile).getIcon(side, world.getBlockMetadata(x, y, z))];
	}

	@SideOnly(Side.CLIENT)
	private IIcon switchForSide(int textureId, int side) {

		if (side == 4 || side == 5) {
			if (textureId == 41)
				return icons[LEFT];
			else
				return icons[RIGHT];
		} else if (textureId == 41)
			return icons[RIGHT];
		else
			return icons[LEFT];

	}
}
