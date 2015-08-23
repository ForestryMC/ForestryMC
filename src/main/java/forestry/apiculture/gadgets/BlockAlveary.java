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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.gadgets.BlockBeehives.BeeHives;
import forestry.core.gadgets.BlockStructure;
import forestry.core.gadgets.BlockResourceStorageBlock.Resources;
import forestry.core.render.TextureManager;

public class BlockAlveary extends BlockStructure {

	public static final PropertyEnum TILES = PropertyEnum.create("tiles", AlvearyTiles.class);
	
	public enum AlvearyTiles implements IStringSerializable
	{
		DEFAULT(0),
		PLAIN(1),
		SWAMER(TileAlvearySwarmer.BLOCK_META),
		FAN(TileAlvearyFan.BLOCK_META),
		HEATER(TileAlvearyHeater.BLOCK_META),
		HYGROREGULATOR(TileAlvearyHygroregulator.BLOCK_META),
		STABILISER(TileAlvearyStabiliser.BLOCK_META),
		SIEVE(TileAlvearySieve.BLOCK_META);
		
		private AlvearyTiles(int meta) {
			this.meta = meta;
		}
		
		private int meta;
		
		public int getMeta()
		{
			return meta;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
	}
	
	public BlockAlveary() {
		super(new MaterialBeehive(false));
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setDefaultState(this.blockState.getBaseState().withProperty(TILES, AlvearyTiles.DEFAULT));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, TILES);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 8; i++) {
			if (i == 1) {
				continue;
			}
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int getRenderType() {
		return 0;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int metadata = getMetaFromState(state);
		ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
		drop.add(new ItemStack(this, 1, metadata != 1 ? metadata : 0));
		return drop;
	}
	
	@Override
	public int getDamageValue(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		int meta = getMetaFromState(state);
		return meta != 1 ? meta : 0;
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch (getMetaFromState(state)) {
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
		return createTileEntity(world, getStateFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((AlvearyTiles)state.getValue(TILES)).getMeta();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TILES, meta);
	}
	
	public static final int PLAIN = 0;
	public static final int ENTRANCE = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int ALVEARY_SWARMER_OFF = 5;
	public static final int ALVEARY_SWARMER_ON = 6;
	public static final int ALVEARY_HEATER_OFF = 7;
	public static final int ALVEARY_HEATER_ON = 8;
	public static final int ALVEARY_FAN_OFF = 9;
	public static final int ALVEARY_FAN_ON = 10;
	public static final int ALVEARY_HYGRO = 11;
	public static final int STABILISER = 12;
	public static final int SIEVE = 13;
	
	@Override
	public String[] getVariants() {
		return new String[]{"default", "plain", "swamer", "fan", "heater", "hygroregulator", "stabiliser", "sieve"};
	}
}
