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
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyClimatiser;
import forestry.apiculture.multiblock.TileAlvearyFan;
import forestry.apiculture.multiblock.TileAlvearyHeater;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearyStabiliser;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.core.blocks.BlockStructure;
import forestry.core.proxy.Proxies;

public class BlockAlveary extends BlockStructure implements IStateMapperRegister {
	private static final PropertyEnum TYPE = PropertyEnum.create("alveary", Type.class);
	private static final PropertyEnum STATE = PropertyEnum.create("state", State.class);
	private static final PropertyEnum LEVEL = PropertyEnum.create("type", Level.class);
	
	private enum State implements IStringSerializable {
		ON, OFF;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	private enum Level implements IStringSerializable {
		BOTTOM, LEFT, RIGHT;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public enum Type implements IStringSerializable {
		PLAIN,
		ENTRANCE,
		SWARMER,
		FAN,
		HEATER,
		HYGRO,
		STABILISER,
		SIEVE;

		public static final Type[] VALUES = values();
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	public BlockAlveary() {
		super(new MaterialBeehive(false));
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("axe", 0);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, Type.PLAIN).withProperty(STATE, State.OFF).withProperty(LEVEL, Level.BOTTOM));
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
	public boolean isNormalCube() {
		return true;
	}
	
	@Override
	public boolean isFullCube() {
		return true;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int metadata = getMetaFromState(state);
		ArrayList<ItemStack> drop = new ArrayList<>();
		drop.add(new ItemStack(this, 1, metadata != 1 ? metadata : 0));
		return drop;
	}

	/* TILE ENTITY CREATION */
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		int metadata = getMetaFromState(state);
		if (metadata < 0 || metadata > Type.VALUES.length) {
			return null;
		}

		Type type = Type.VALUES[metadata];
		switch (type) {
			case SWARMER:
				return new TileAlvearySwarmer();
			case FAN:
				return new TileAlvearyFan();
			case HEATER:
				return new TileAlvearyHeater();
			case HYGRO:
				return new TileAlvearyHygroregulator();
			case STABILISER:
				return new TileAlvearyStabiliser();
			case SIEVE:
				return new TileAlvearySieve();
			case PLAIN:
			default:
				return new TileAlvearyPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, getStateFromMeta(meta));
	}

	/* ITEM MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "apiculture/alveary.plain");
		manager.registerItemModel(item, 1, "apiculture/alveary.entrance");
		manager.registerItemModel(item, 2, "apiculture/alveary.swarmer");
		manager.registerItemModel(item, 3, "apiculture/alveary.fan");
		manager.registerItemModel(item, 4, "apiculture/alveary.heater");
		manager.registerItemModel(item, 5, "apiculture/alveary.hygro");
		manager.registerItemModel(item, 6, "apiculture/alveary.stabiliser");
		manager.registerItemModel(item, 7, "apiculture/alveary.sieve");
	}
	
	/* STATES */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, Type.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Type) state.getValue(TYPE)).ordinal();
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { TYPE, STATE, LEVEL });
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileAlveary) {
			if (tile instanceof TileAlvearyClimatiser) {
				TileAlvearyClimatiser alveary = (TileAlvearyClimatiser) tile;
				if (alveary.isActive()) {
					state = state.withProperty(STATE, State.ON);
				} else {
					state = state.withProperty(STATE, State.OFF);
				}
			} else if (tile instanceof TileAlvearySwarmer) {
				TileAlvearySwarmer alveary = (TileAlvearySwarmer) tile;
				if (alveary.isActive()) {
					state = state.withProperty(STATE, State.ON);
				} else {
					state = state.withProperty(STATE, State.OFF);
				}
			}
		}

		int meta = ((Type) state.getValue(TYPE)).ordinal();

		Block blockXP = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock();
		Block blockXM = world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getBlock();

		state = state.withProperty(LEVEL, Level.BOTTOM);

		if (blockXP == this && blockXM != this) {

			IBlockState stateNeighbor = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
			if (stateNeighbor.getBlock().getMetaFromState(stateNeighbor) == 1) {

				if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() != this) {
					switchForSide(state, world, pos);
				} else {
					switchForSide(state, world, pos);
				}

			}
		} else if (blockXP != this && blockXM == this) {
			IBlockState stateNeighbor = world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
			if (stateNeighbor.getBlock().getMetaFromState(stateNeighbor) == 1) {

				if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() != this) {
					switchForSide(state, world, pos);
				} else {
					switchForSide(state, world, pos);
				}

			}
		}

		return super.getActualState(state, world, pos);
	}
	
	private void switchForSide(IBlockState state, IBlockAccess world, BlockPos pos) {

		Block blockX = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock();
		Block blockY = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock();
		if (blockX != this && blockY == this) {
			state = state.withProperty(LEVEL, Level.LEFT);
		} else if (blockX == this && blockY == this) {
			state = state.withProperty(LEVEL, Level.RIGHT);
		} else if (blockX == this && blockY != this) {
			state = state.withProperty(LEVEL, Level.LEFT);
		} else if (blockX != this && blockY != this) {
			state = state.withProperty(LEVEL, Level.RIGHT);
		}

	}
	
	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new AlvearyStateMapper());
	}
	
	@SideOnly(Side.CLIENT)
	private static class AlvearyStateMapper extends StateMapperBase {

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
			if (linkedhashmap.get(TYPE) != Type.PLAIN) {
				linkedhashmap.remove(LEVEL);
			}
			if (linkedhashmap.get(TYPE) == Type.SIEVE
					|| linkedhashmap.get(TYPE) == Type.ENTRANCE
					|| linkedhashmap.get(TYPE) == Type.STABILISER
					|| linkedhashmap.get(TYPE) == Type.HYGRO
					|| linkedhashmap.get(TYPE) == Type.PLAIN) {
				linkedhashmap.remove(STATE);
			}
			String s = String.format("%s:%s",
					Block.blockRegistry.getNameForObject(state.getBlock()).getResourceDomain(),
					"apiculture/alveary_" + TYPE.getName((Enum) linkedhashmap.remove(TYPE)));
			;
			return new ModelResourceLocation(s, this.getPropertyString(linkedhashmap));
		}

	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		super.onNeighborBlockChange(world, pos, state, neighborBlock);
		
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileAlveary) {
			TileAlveary tileAlveary = (TileAlveary) tileEntity;

			// We must check that the slabs on top were not removed
			tileAlveary.getMultiblockLogic().getController().reassemble();
		}
	}

	public ItemStack get(Type type) {
		return new ItemStack(this, 1, type.ordinal());
	}
}
