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

import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.WoodHelper.WoodMeshDefinition;
import forestry.arboriculture.proxy.ProxyArboricultureClient;

public class BlockForestryFenceGate<T extends Enum<T> & IWoodType> extends BlockFenceGate implements IWoodTyped, IItemModelRegister, IStateMapperRegister {

	private final boolean fireproof;
	private final T woodType;

	public BlockForestryFenceGate(boolean fireproof, T woodType) {
		super(EnumType.OAK);
		this.fireproof = fireproof;
		this.woodType = woodType;

		setHardness(2.0F);
		setResistance(5.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.FENCE_GATE;
	}

	@Override
	public T getWoodType(int meta) {
		return woodType;
	}

	@Override
	public Collection<T> getWoodTypes() {
		return Collections.singleton(woodType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		ProxyArboricultureClient.registerWoodStateMapper(this, new WoodTypeStateMapper(this, null).addPropertyToRemove(POWERED));
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(TreeManager.woodAccess.getStack(woodType, getBlockKind(), fireproof));
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		T woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		ModelBakery.registerItemVariants(item, WoodHelper.getDefaultResourceLocations(this));
		ProxyArboricultureClient.registerWoodMeshDefinition(item, new WoodMeshDefinition(this));
	}
}