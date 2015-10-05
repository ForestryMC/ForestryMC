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

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.items.ItemWoodBlock;
import forestry.arboriculture.items.ItemWoodBlock.WoodMeshDefinition;

public class BlockArbStairs extends BlockStairs implements IWoodTyped, IModelRegister, ITileEntityProvider {

	private final boolean fireproof;

	public BlockArbStairs(Block par2Block, boolean fireproof) {
		super(par2Block.getDefaultState());

		this.fireproof = fireproof;

		setCreativeTab(Tabs.tabArboriculture);
		setHardness(2.0F);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
		setDefaultState(this.blockState.getBaseState().withProperty(WoodType.WOODTYPE, WoodType.LARCH).withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{WoodType.WOODTYPE, FACING, HALF, SHAPE});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (WoodType woodType : WoodType.VALUES) {
			list.add(woodType.getStairs(fireproof));
		}
	}
	
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if(!fireproof)
		{
			manager.registerVariant(item, ItemWoodBlock.getVariants(this));
		}
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		return BlockWood.blockRemovedByPlayer(this, world, player, pos);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack itemStack = super.getPickBlock(target, world, pos, player);
		NBTTagCompound stairsNBT = BlockWood.getTagCompound(world, pos);
		itemStack.setTagCompound(stairsNBT);
		return itemStack;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(placer instanceof EntityPlayer)
		{
			state = state.withProperty(WoodType.WOODTYPE, WoodType.getFromCompound(stack.getTagCompound()));
		}
	}
	
	
	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new TileWood();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileWood)
		{
			TileWood wood = (TileWood) tile;
			state = state.withProperty(WoodType.WOODTYPE, wood.getWoodType());
		}
		return super.getActualState(state, world, pos);
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}

	@Override
	public String getBlockKind() {
		return "stairs";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}
}
