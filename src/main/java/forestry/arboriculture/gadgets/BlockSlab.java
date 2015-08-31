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

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import forestry.arboriculture.items.ItemWoodBlock.WoodMeshDefinition;
import forestry.core.config.ForestryBlock;

public class BlockSlab extends net.minecraft.block.BlockSlab implements IWoodTyped, IModelRegister {
	
	private final boolean fireproof;

	public BlockSlab(boolean fireproof) {
		super(Material.wood);

		this.fireproof = fireproof;
		
		IBlockState state = this.blockState.getBaseState();;

		setCreativeTab(Tabs.tabArboriculture);
		setLightOpacity(0);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setHarvestLevel("axe", 0);
		setDefaultState(state.withProperty(WoodType.WOODTYPE, WoodType.LARCH).withProperty(HALF, EnumBlockHalf.BOTTOM));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{HALF, WoodType.WOODTYPE});
	}
	
    @Override
	public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, new WoodMeshDefinition("slabs"));
	}

	/* ICONS */
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ForestryBlock.slabs.item();
	}
	
	@Override
	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(Blocks.wooden_slab, 2);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (WoodType woodType : WoodType.VALUES) {
			list.add(woodType.getSlab(fireproof));
		}
	}

	/* PROPERTIES */
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack itemStack = new ItemStack(ForestryBlock.slabs.item());
		NBTTagCompound nbt = BlockWood.getTagCompound(world, pos);
		itemStack.setTagCompound(nbt);
		return itemStack;
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
	public float getBlockHardness(World world, BlockPos pos) {
		WoodType type = BlockWood.getWoodType(world, pos);
		if (type == null) {
			return WoodType.DEFAULT_HARDNESS;
		}
		return type.getHardness();
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return !isFireproof();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return isFireproof() ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return isFireproof() ? 0 : 5;
	}

	@Override
	public String getBlockKind() {
		return "slab";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}

	// Minecraft's BlockSlab overrides this for their slabs, so we change it back to normal here
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public boolean isDouble() {
		return false;
	}

	@Override
	public IProperty getVariantProperty() {
		return WoodType.WOODTYPE;
	}

	@Override
	public Object getVariant(ItemStack stack) {
		return WoodType.getFromCompound(stack.getTagCompound());
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return "SomeSlab";
	}
}
