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
package forestry.arboriculture.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.common.property.ExtendedBlockState;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.core.IStateMapperRegister;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.property.PropertyFruit;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.UnlistedBlockAccess;
import forestry.core.utils.UnlistedBlockPos;

public class BlockFruitPod extends BlockCocoa implements IStateMapperRegister {

	public final static PropertyFruit FRUIT = new PropertyFruit("fruit");
	
	public BlockFruitPod() {
		super();
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
				.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { FACING, AGE, FRUIT },
				new IUnlistedProperty[] { UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS });
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileFruitPod sapling = (TileFruitPod) world.getTileEntity(pos);
		IAlleleFruit fruit = sapling.getAllele();
		state = state.withProperty(FRUIT, fruit);
		return super.getActualState(state, world, pos);
	}

	public static TileFruitPod getPodTile(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileFruitPod)) {
			return null;
		}

		return (TileFruitPod) tile;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!canBlockStay(world, pos, state)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
			return;
		}

		TileFruitPod tile = getPodTile(world, pos);
		if (tile == null) {
			return;
		}

		tile.onBlockTick();
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote) {
			TileFruitPod tile = getPodTile(world, pos);
			if (tile != null) {
				for (ItemStack drop : tile.getDrop()) {
					ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
				}
			}
		}

		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<>();
	}
	
	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		return BlockUtil.getDirectionalMetadata(world, pos) >= 0;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileFruitPod();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new FruitStateMap());
	}
	
	@SideOnly(Side.CLIENT)
	public class FruitStateMap implements IStateMapper{
		
		protected Map mapStateModelLocations = Maps.newLinkedHashMap();

		public String getPropertyString(Map p_178131_1_) {
			StringBuilder stringbuilder = new StringBuilder();
			Iterator iterator = p_178131_1_.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();

				if (stringbuilder.length() != 0) {
					stringbuilder.append(",");
				}

				IProperty iproperty = (IProperty) entry.getKey();
				Comparable comparable = (Comparable) entry.getValue();
				stringbuilder.append(iproperty.getName());
				stringbuilder.append("=");
				stringbuilder.append(iproperty.getName(comparable));
			}

			if (stringbuilder.length() == 0) {
				stringbuilder.append("normal");
			}

			return stringbuilder.toString();
		}

		@Override
		public Map putStateModelLocations(Block block) {
			for	(EnumFacing facing : EnumFacing.values()){
				if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
					for	(int age = 0;age < AGE.getAllowedValues().size();age++){
						for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
							if (allele instanceof IAlleleFruit) {
								if(((IAlleleFruit) allele).getTextureName() != null){
									IBlockState state = getDefaultState().withProperty(FRUIT, (IAlleleFruit)allele).withProperty(FACING, facing).withProperty(AGE, age);
									LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
									String s = String.format("%s:%s",( (IAlleleFruit) allele).getModID(), "pods/" + FRUIT.getName((IAlleleFruit) linkedhashmap.remove(FRUIT)));
									mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
								}
							}
						}
					}
				}
			}
			return mapStateModelLocations;
		}
		
	}

	/* IGrowable */
	
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileFruitPod podTile = getPodTile(world, pos);
		if (podTile != null) {
			return podTile.canMature();
		}
		return false;
	}
	
	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}
	
	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileFruitPod podTile = getPodTile(world, pos);
		if (podTile != null) {
			podTile.mature();
		}
	}

	public static void registerSprites() {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleFruit) {
				((IAlleleFruit) allele).getProvider().registerSprites();
			}
		}
	}
}
