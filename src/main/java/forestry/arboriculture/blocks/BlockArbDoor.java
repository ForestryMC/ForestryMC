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
import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.proxy.ProxyArboricultureClient;

public class BlockArbDoor extends BlockDoor implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	private final EnumForestryWoodType woodType;

	public BlockArbDoor(EnumForestryWoodType woodType) {
		super(Material.WOOD);
		this.woodType = woodType;

		setHarvestLevel("axe", 0);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		ModelBakery.registerItemVariants(item, WoodHelper.getResourceLocations(this));
		ProxyArboricultureClient.registerWoodMeshDefinition(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		ProxyArboricultureClient.registerWoodStateMapper(this,
			new WoodTypeStateMapper(this, null).addPropertyToRemove(POWERED));
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.DOOR;
	}

	@Override
	public boolean isFireproof() {
		return false;
	}

	@Override
	public EnumForestryWoodType getWoodType(int meta) {
		return woodType;
	}

	@Override
	public Collection<EnumForestryWoodType> getWoodTypes() {
		return Collections.singleton(woodType);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		EnumForestryWoodType woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : getItem();
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(getItem());
	}

	private Item getItem() {
		return TreeManager.woodAccess.getStack(woodType, getBlockKind(), false).getItem();
	}
}
