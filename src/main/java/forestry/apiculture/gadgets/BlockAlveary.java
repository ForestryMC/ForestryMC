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
import net.minecraft.block.properties.IProperty;
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
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyFan;
import forestry.apiculture.multiblock.TileAlvearyHeater;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearyStabiliser;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.core.gadgets.BlockStructure;
import forestry.core.multiblock.MultiblockRegistry;

public class BlockAlveary extends BlockStructure {
	
	public static final PropertyEnum ALVEARYTYPE = PropertyEnum.create("alveary", AlvearyTypes.class) ;
	
	public enum AlvearyTypes implements IStringSerializable{
		PLAIN,
		ENTRANCE,
		BOTTOM,
		LEFT,
		RIGHT,
		ALVEARY_SWARMER_OFF,
		ALVEARY_SWARMER_ON,
		ALVEARY_HEATER_OFF,
		ALVEARY_HEATER_ON,
		ALVEARY_FAN_OFF,
		ALVEARY_FAN_ON,
		ALVEARY_HYGRO,
		STABILISER,
		SIEVE;
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public BlockAlveary() {
		super(new MaterialBeehive(false));
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setDefaultState(this.blockState.getBaseState().withProperty(ALVEARYTYPE, AlvearyTypes.PLAIN));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ALVEARYTYPE, AlvearyTypes.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((AlvearyTypes)state.getValue(ALVEARYTYPE)).ordinal();
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{ALVEARYTYPE});
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
		return 3;
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
		drop.add(new ItemStack(this, 1, getMetaFromState(state) != 1 ? getMetaFromState(state) : 0));
		return drop;
	}
	
	@Override
	public int getDamageValue(World world, BlockPos pos) {
		int meta = getMetaFromState(world.getBlockState(pos));
		return meta != 1 ? meta : 0;
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch (getMetaFromState(state)) {
			case TileAlveary.SWARMER_META:
				return new TileAlvearySwarmer();
			case TileAlveary.FAN_META:
				return new TileAlvearyFan();
			case TileAlveary.HEATER_META:
				return new TileAlvearyHeater();
			case TileAlveary.HYGRO_META:
				return new TileAlvearyHygroregulator();
			case TileAlveary.STABILIZER_META:
				return new TileAlvearyStabiliser();
			case TileAlveary.SIEVE_META:
				return new TileAlvearySieve();
			default:
				return new TileAlvearyPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, getStateFromMeta(meta));
	}

	/* ICONS */
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
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "blocks", "apiculture/alveary.plain");
		manager.registerItemModel(item, 1, "blocks", "apiculture/alveary.entrance");
		manager.registerItemModel(item, 2, "blocks", "apiculture/alveary.bottom");
		manager.registerItemModel(item, 3, "blocks", "apiculture/alveary.left");
		manager.registerItemModel(item, 4, "blocks", "apiculture/alveary.right");
		manager.registerItemModel(item, 5, "blocks", "apiculture/alveary.swarmer.off");
		manager.registerItemModel(item, 6, "blocks", "apiculture/alveary.swarmer.on");
		manager.registerItemModel(item, 7, "blocks", "apiculture/alveary.heater.off");
		manager.registerItemModel(item, 8, "blocks", "apiculture/alveary.heater.on");
		manager.registerItemModel(item, 9, "blocks", "apiculture/alveary.fan.off");
		manager.registerItemModel(item, 10, "blocks", "apiculture/alveary.fan.on");
		manager.registerItemModel(item, 11, "blocks", "apiculture/alveary.valve");
		manager.registerItemModel(item, 12, "blocks", "apiculture/alveary.stabiliser");
		manager.registerItemModel(item, 13, "blocks", "apiculture/alveary.sieve");
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		super.onNeighborBlockChange(world, pos, state, block);
		
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileAlveary) {
			TileAlveary tileAlveary = (TileAlveary) tileEntity;

			// We must check that the slabs on top were not removed
			MultiblockRegistry.addDirtyController(world, tileAlveary.getMultiblockController());
		}
	}
}
