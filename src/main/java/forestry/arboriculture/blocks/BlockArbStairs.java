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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.WoodHelper.WoodMeshDefinition;
import forestry.core.proxy.Proxies;

public class BlockArbStairs extends BlockStairs implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	private final boolean fireproof;
	private final EnumWoodType woodType;

	public BlockArbStairs(boolean fireproof, IBlockState modelState, EnumWoodType woodType) {
		super(modelState);
		this.fireproof = fireproof;
		this.woodType = woodType;
		setCreativeTab(Tabs.tabArboriculture);
		setHarvestLevel("axe", 0);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerVariant(item, WoodHelper.getResourceLocations(this));
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Nonnull
	@Override
	public String getBlockKind() {
		return "stairs";
	}

	@Nonnull
	@Override
	public EnumWoodType getWoodType(int meta) {
		return woodType;
	}

	@Nonnull
	@Override
	public Collection<EnumWoodType> getWoodTypes() {
		return Collections.singleton(woodType);
	}

	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, null));
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
}
