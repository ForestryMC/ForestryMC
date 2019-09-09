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

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.DistExecutor;

import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateTransformer;
import forestry.api.modules.ForestryModule;
import forestry.climatology.blocks.BlockRegistryClimatology;
import forestry.climatology.gui.ClimatologyContainerTypes;
import forestry.climatology.gui.GuiHabitatFormer;
import forestry.climatology.items.ItemRegistryClimatology;
import forestry.climatology.network.PacketRegistryClimatology;
import forestry.climatology.proxy.ProxyClimatology;
import forestry.climatology.proxy.ProxyClimatologyClient;
import forestry.climatology.tiles.TileRegistryClimatology;
import forestry.core.ModuleCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.climate.FakeClimateListener;
import forestry.core.climate.FakeClimateTransformer;
import forestry.core.config.Constants;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CLIMATOLOGY, name = "Climatology", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.greenhouse.description")
public class ModuleClimatology extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	public static ProxyClimatology proxy;

	@Nullable
	private static BlockRegistryClimatology blocks;
	@Nullable
	private static ItemRegistryClimatology items;
	@Nullable
	private static ClimatologyContainerTypes containerTypes;
	@Nullable
	private static TileRegistryClimatology tiles;

	public ModuleClimatology() {
		proxy = DistExecutor.runForDist(() -> () -> new ProxyClimatologyClient(), () -> () -> new ProxyClimatology());
	}

	public static BlockRegistryClimatology getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static ItemRegistryClimatology getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	public static ClimatologyContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	public static TileRegistryClimatology getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryClimatology();
	}

	@Override
	public void registerItems() {
		items = new ItemRegistryClimatology();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new ClimatologyContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(getContainerTypes().HABITAT_FORMER, GuiHabitatFormer::new);
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryClimatology();
	}

	@Override
	public void preInit() {
		proxy.preInit();

		// Capabilities
		CapabilityManager.INSTANCE.register(IClimateListener.class, new NullStorage<>(), () -> FakeClimateListener.INSTANCE);
		CapabilityManager.INSTANCE.register(IClimateTransformer.class, new NullStorage<>(), () -> FakeClimateTransformer.INSTANCE);
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryClimatology blockRegistry = getBlocks();

		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			//			RecipeManagers.carpenterManager.addRecipe(100, new FluidStack(FluidRegistry.WATER, 2000), ItemStack.EMPTY, getItems().habitatScreen.getItemStack(),
			//				"IPI",
			//				"IPI",
			//				"GDG",
			//				'G', OreDictUtil.GEAR_BRONZE,
			//				'P', OreDictUtil.PANE_GLASS,
			//				'I', OreDictUtil.INGOT_BRONZE,
			//				'D', OreDictUtil.GEM_DIAMOND);	//TODO fluids
		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryClimatology();
	}

}
