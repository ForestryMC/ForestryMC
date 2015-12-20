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
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.property.PropertySapling;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockSapling extends BlockTreeContainer implements IGrowable {

	public static final PropertySapling SAPLING = new PropertySapling("sapling");
	
	public static TileSapling getSaplingTile(IBlockAccess world, BlockPos pos) {
		return TileUtil.getTile(world, pos, TileSapling.class);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileSapling sapling = (TileSapling) world.getTileEntity(pos);
		IAlleleTreeSpecies species = sapling.getTree().getGenome().getPrimary();
		state = state.withProperty(SAPLING, species);
		return super.getActualState(state, world, pos);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{SAPLING});
	}

	public BlockSapling() {
		super(Material.plants);

		float factor = 0.4F;
		setBlockBounds(0.5F - factor, 0.0F, 0.5F - factor, 0.5F + factor, factor * 2.0F, 0.5F + factor);
		setStepSound(soundTypeGrass);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int meta) {
		return new TileSapling();
	}

	/* COLLISION BOX */
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new SplingStateMap());
	}
	
	@SideOnly(Side.CLIENT)
	public class SplingStateMap implements IStateMapper{
		
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
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleTreeSpecies) {
					IBlockState state = getDefaultState().withProperty(SAPLING, (IAlleleTreeSpecies)allele);
					LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
					String s = String.format("%s:%s",( (IAlleleTreeSpecies) allele).getModID(), "saplings/" + SAPLING.getName((IAlleleTreeSpecies) linkedhashmap.remove(SAPLING)));
					mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
				}
			}
			return mapStateModelLocations;
		}
		
	}

	/* PLANTING */
	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		TileSapling tile = getSaplingTile(world, pos);
		if (tile == null) {
			return false;
		}
		if (tile.getTree() == null) {
			return false;
		}

		return tile.getTree().canStay(world, pos);
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		super.onNeighborBlockChange(world, pos, state, neighborBlock);
		if (!world.isRemote && !this.canSustainPlant(world, pos, null, null)) {
			dropAsSapling(world, pos);
			world.setBlockToAir(pos);
		}
	}

	/* REMOVING */
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<>();
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		TileSapling sapling = getSaplingTile(world, pos);
		if (sapling == null || sapling.getTree() == null) {
			return null;
		}
		return TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING.ordinal());
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote && canHarvestBlock(world, pos, player)) {
			if (!player.capabilities.isCreativeMode) {
				dropAsSapling(world, pos);
			}
		}

		return world.setBlockToAir(pos);
	}

	private static void dropAsSapling(World world, BlockPos pos) {
		if (world.isRemote) {
			return;
		}

		TileSapling sapling = getSaplingTile(world, pos);
		if (sapling != null && sapling.getTree() != null) {
			ItemStack saplingStack = TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING.ordinal());
			ItemStackUtil.dropItemStackAsEntity(saplingStack, world, pos);
		}

	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		TileSapling saplingTile = getSaplingTile(world, pos);
		if (saplingTile != null) {
			return saplingTile.canAcceptBoneMeal();
		}
		return true;
	}
	
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return world.rand.nextFloat() < 0.45F;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileSapling saplingTile = getSaplingTile(world, pos);
		if (saplingTile != null) {
			saplingTile.tryGrow(true);
		}
	}
}
