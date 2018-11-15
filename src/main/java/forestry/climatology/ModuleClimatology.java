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
package forestry.climatology;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.common.SidedProxy;

import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateTransformer;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.climatology.blocks.BlockRegistryClimatology;
import forestry.climatology.items.ItemRegistryClimatology;
import forestry.climatology.network.PacketRegistryClimatology;
import forestry.climatology.proxy.ProxyClimatology;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.ModuleCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.climate.FakeClimateListener;
import forestry.core.climate.FakeClimateTransformer;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CLIMATOLOGY, name = "Climatology", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.greenhouse.description")
public class ModuleClimatology extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.climatology.proxy.ProxyClimatologyClient", serverSide = "forestry.climatology.proxy.ProxyClimatology")
	public static ProxyClimatology proxy;

	@Nullable
	private static BlockRegistryClimatology blocks;
	@Nullable
	private static ItemRegistryClimatology items;

	public static BlockRegistryClimatology getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static ItemRegistryClimatology getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryClimatology();
		items = new ItemRegistryClimatology();
	}

	@Override
	public void preInit() {
		proxy.preInit();

		// Capabilities
		CapabilityManager.INSTANCE.register(IClimateListener.class, new NullStorage<>(), () -> FakeClimateListener.INSTANCE);
		CapabilityManager.INSTANCE.register(IClimateTransformer.class, new NullStorage<>(), () -> FakeClimateTransformer.INSTANCE);
	}

	@Override
	public void doInit() {
		TileUtil.registerTile(TileHabitatFormer.class, "habitat_former");
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryClimatology blockRegistry = getBlocks();

		RecipeUtil.addRecipe("habitat_former", new ItemStack(blockRegistry.habitatformer),
			"GRG",
			"TST",
			"BCB",
			'S', coreItems.sturdyCasing,
			'G', OreDictUtil.BLOCK_GLASS,
			'B', OreDictUtil.GEAR_BRONZE,
			'R', OreDictUtil.DUST_REDSTONE,
			'C', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC),
			'T', coreItems.tubes.get(EnumElectronTube.IRON, 1));
		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			RecipeManagers.carpenterManager.addRecipe(100, new FluidStack(FluidRegistry.WATER, 2000), ItemStack.EMPTY, getItems().habitatScreen.getItemStack(),
				"IPI",
				"IPI",
				"GDG",
				'G', OreDictUtil.GEAR_BRONZE,
				'P', OreDictUtil.PANE_GLASS,
				'I', OreDictUtil.INGOT_BRONZE,
				'D', OreDictUtil.GEM_DIAMOND);
		} else {
			RecipeUtil.addRecipe("habitat_screen", getItems().habitatScreen.getItemStack(),
				"IPI",
				"IPI",
				"GDG",
				'G', OreDictUtil.GEAR_BRONZE,
				'P', OreDictUtil.PANE_GLASS,
				'I', OreDictUtil.INGOT_BRONZE,
				'D', OreDictUtil.GEM_DIAMOND);
		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryClimatology();
	}

}
