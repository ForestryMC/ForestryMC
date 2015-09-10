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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.gadgets.UnlistedBlockAccess;
import forestry.core.gadgets.UnlistedBlockPos;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public class BlockSapling extends BlockTreeContainer implements IGrowable, IModelRegister {

	public static TileSapling getSaplingTile(IBlockAccess world, BlockPos pos) {
		return Utils.getTile(world, pos, TileSapling.class);
	}

	public BlockSapling() {
		super(Material.plants);

		float factor = 0.4F;
		setBlockBounds(0.5F - factor, 0.0F, 0.5F - factor, 0.5F + factor, factor * 2.0F, 0.5F + factor);
		setStepSound(soundTypeGrass);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState)super.getExtendedState(state, world, pos )).withProperty(UnlistedBlockPos.POS, pos).withProperty(UnlistedBlockAccess.BLOCKACCESS , world);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
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
	@SideOnly(Side.CLIENT)
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return 3;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				((IAlleleTreeSpecies) allele).getSpriteProvider().registerIcons(TextureManager.getInstance());
			}
			if (allele instanceof IAlleleFruit) {
				((IAlleleFruit) allele).getProvider().registerIcons();
			}
		}
	}

	/* PLANTING */
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		super.onNeighborBlockChange(world, pos, state, neighborBlock);
		if (Proxies.common.isSimulating(world) && canSustainPlant(world, pos, null, null)) {
			dropAsSapling(world, pos);
			world.setBlockToAir(pos);
		}
	}
	
	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		TileSapling tile = getSaplingTile(world, pos);
		if (tile == null) {
			return false;
		}
		if (tile.getTree() == null) {
			return false;
		}

		return tile.getTree().canStay((World) world, pos);
	}

	/* REMOVING */
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
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
		if (Proxies.common.isSimulating(world) && canHarvestBlock(world, pos, player)) {
			if (!player.capabilities.isCreativeMode) {
				dropAsSapling(world, pos);
			}
		}

		return world.setBlockToAir(pos);
	}

	private static void dropAsSapling(World world, BlockPos pos) {
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileSapling sapling = getSaplingTile(world, pos);
		if (sapling != null && sapling.getTree() != null) {
			ItemStack saplingStack = TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING.ordinal());
			StackUtils.dropItemStackAsEntity(saplingStack, world, pos.getX(), pos.getY(), pos.getZ());
		}

	}

	@Override
	/** canFertilize */
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileSapling saplingTile = getSaplingTile(world, pos);
		if (saplingTile != null) {
			return saplingTile.canAcceptBoneMeal();
		}
		return true;
	}

	@Override
	/** shouldFertilize */
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return world.rand.nextFloat() < 0.45F;
	}

	@Override
	/** fertilize */
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileSapling saplingTile = getSaplingTile(world, pos);
		if (saplingTile != null) {
			saplingTile.tryGrow(true);
		}
	}
}
