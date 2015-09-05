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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
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
import forestry.core.gadgets.BlockStructure;
import forestry.core.multiblock.MultiblockRegistry;

public class BlockAlveary extends BlockStructure {
	
	public static final PropertyEnum ALVEARYTYPE = PropertyEnum.create("alveary", AlvearyTypes.class);
	public static final PropertyEnum STATE = PropertyEnum.create("state", State.class);
	public static final PropertyEnum TYPE = PropertyEnum.create("type", Type.class);
	
	public enum State implements IStringSerializable{
		ON,
		OFF;
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public enum Type implements IStringSerializable{
		DOWN,
		MIDDLE,
		UP;
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public enum AlvearyTypes implements IStringSerializable{
		ALVEARY_PLAIN,
		ALVEARY_ENTRANCE,
		ALVEARY_SWARMER,
		ALVEARY_HEATER,
		ALVEARY_FAN,
		ALVEARY_HYGRO,
		ALVEARY_STABILISER,
		ALVEARY_SIEVE;
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	public BlockAlveary() {
		super(new MaterialBeehive(false));
		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setDefaultState(this.blockState.getBaseState().withProperty(ALVEARYTYPE, AlvearyTypes.ALVEARY_PLAIN).withProperty(STATE, State.OFF).withProperty(TYPE, Type.MIDDLE));
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
		return new BlockState(this, new IProperty[]{ALVEARYTYPE, STATE, TYPE});
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile =world.getTileEntity(pos);
		if(tile instanceof TileAlveary)
		{
			if(tile instanceof TileAlvearyClimatiser)
			{
				TileAlvearyClimatiser alveary = (TileAlvearyClimatiser) tile;
				if(alveary.active)
				{
					state = state.withProperty(STATE, State.ON);
				}
				else{
					state = state.withProperty(STATE, State.OFF);
				}
			}
			else if(tile instanceof TileAlvearySwarmer)
			{
				TileAlvearySwarmer alveary = (TileAlvearySwarmer) tile;
				if(alveary.active)
				{
					state = state.withProperty(STATE, State.ON);
				}
				else{
					state = state.withProperty(STATE, State.OFF);
				}
			}
		}
		
		int meta = ((AlvearyTypes)state.getValue(ALVEARYTYPE)).ordinal();
		
		Block blockXP = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock();
		Block blockXM = world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())).getBlock();
		
		state = state.withProperty(TYPE, Type.MIDDLE);
		
		if (blockXP == this && blockXM != this) {

			IBlockState stateNeighbor = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
			if (stateNeighbor.getBlock().getMetaFromState(stateNeighbor) == 1) {

				if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() != this) {
					switchForSide(42, state, world, pos);
				} else {
					switchForSide(41, state, world, pos);
				}

			}
		} else if (blockXP != this && blockXM == this) {
			IBlockState stateNeighbor = world.getBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
			if (stateNeighbor.getBlock().getMetaFromState(stateNeighbor) == 1) {

				if (world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)).getBlock() != this) {
					switchForSide(41, state, world, pos);
				} else {
					switchForSide(42, state, world, pos);
				}

			}
		}
		
		
		return super.getActualState(state, world, pos);
	}
	
	@SideOnly(Side.CLIENT)
	private void switchForSide(int textureId, IBlockState state, IBlockAccess world, BlockPos pos) {

		Block blockX = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())).getBlock();
		Block blockY = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock();
		if(blockX != this && blockY == this)
		{
			state = state.withProperty(TYPE, Type.UP);
		}
		else if(blockX == this && blockY == this)
		{
			state = state.withProperty(TYPE, Type.DOWN);
		}
		else if(blockX == this && blockY != this)
		{
			state = state.withProperty(TYPE, Type.UP);
		}
		else if(blockX != this && blockY != this)
		{
			state = state.withProperty(TYPE, Type.DOWN);
		}

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
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "apiculture", "alveary.plain");
		manager.registerItemModel(item, 1, "apiculture", "alveary.entrance");
		manager.registerItemModel(item, 2, "apiculture", "alveary.swarmer");
		manager.registerItemModel(item, 3, "apiculture", "alveary.heater");
		manager.registerItemModel(item, 4, "apiculture", "alveary.fan");
		manager.registerItemModel(item, 5, "apiculture", "alveary.hygro");
		manager.registerItemModel(item, 6, "apiculture", "alveary.stabiliser");
		manager.registerItemModel(item, 7, "apiculture", "alveary.sieve");
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
	
	public static class AlvearyStateMapper extends StateMapperBase{
		
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
		    if(linkedhashmap.get(ALVEARYTYPE) != AlvearyTypes.ALVEARY_PLAIN){
		    	linkedhashmap.remove(TYPE);
		    }
		    if(linkedhashmap.get(ALVEARYTYPE) == AlvearyTypes.ALVEARY_SIEVE || linkedhashmap.get(ALVEARYTYPE) == AlvearyTypes.ALVEARY_ENTRANCE || linkedhashmap.get(ALVEARYTYPE) == AlvearyTypes.ALVEARY_STABILISER || linkedhashmap.get(ALVEARYTYPE) == AlvearyTypes.ALVEARY_HYGRO || linkedhashmap.get(ALVEARYTYPE) ==  AlvearyTypes.ALVEARY_PLAIN){
		    	linkedhashmap.remove(STATE);
		    }
		    String s = String.format("%s:%s", ((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock())).getResourceDomain(), "apiculture/" + ALVEARYTYPE.getName((Comparable)linkedhashmap.remove(ALVEARYTYPE)));;
			return new ModelResourceLocation(s, this.getPropertyString(linkedhashmap));
		}
		
	}
}
