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

import net.minecraftforge.fml.common.SidedProxy;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimateTransformer;
import forestry.api.modules.ForestryModule;
import forestry.climatology.blocks.BlockRegistryClimatology;
import forestry.climatology.circuits.CircuitHabitatformer;
import forestry.climatology.items.ItemRegistryClimatology;
import forestry.climatology.network.PacketRegistryClimatology;
import forestry.climatology.proxy.ProxyClimatology;
import forestry.climatology.tiles.TileHabitatformer;
import forestry.core.ModuleCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
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

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CLIMATOLOGY, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.greenhouse.description")
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
		proxy.preInti();

		ICircuitLayout layoutManaged = new CircuitLayout("habitatformer", CircuitSocketType.HABITAT_FORMER);
		ChipsetManager.circuitRegistry.registerLayout(layoutManaged);
		// Capabilities
		CapabilityManager.INSTANCE.register(IClimateListener.class, new NullStorage<>(), () -> FakeClimateListener.INSTANCE);
		CapabilityManager.INSTANCE.register(IClimateTransformer.class, new NullStorage<>(), () -> FakeClimateTransformer.INSTANCE);
	}

	@Override
	public void doInit() {
		TileUtil.registerTile(TileHabitatformer.class, "habitatformer");

		Circuits.formerRange1 = new CircuitHabitatformer("former.range.1", 0.05F, 0.1F, 0.0F);
		Circuits.formerRange2 = new CircuitHabitatformer("former.range.2", 0.0875F, 0.175F, 0.0F);
		Circuits.formerRange3 = new CircuitHabitatformer("former.range.3", 0.125F, 0.25F, 0.0F);
		Circuits.formerEfficiency1 = new CircuitHabitatformer("former.efficiency.1", 0.0F, 0.0F, -0.075F);
		Circuits.formerEfficiency2 = new CircuitHabitatformer("former.efficiency.2", 0.0F, 0.0F, -0.1F);
		Circuits.formerEfficiency3 = new CircuitHabitatformer("former.efficiency.3", 0.0F, 0.0F, -0.125F);
		Circuits.formerSpeed1 = new CircuitHabitatformer("former.speed.1", 0.15F, 0.0F, 0.0F);
		Circuits.formerSpeed2 = new CircuitHabitatformer("former.speed.2", 0.20F, 0.0F, 0.0F);
		Circuits.formerSpeed3 = new CircuitHabitatformer("former.speed.3", 0.25F, 0.0F, 0.0F);

		//Old greenhouse circuits
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("climatiser.temperature.1", Circuits.formerSpeed1);
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("climatiser.temperature.2", Circuits.formerSpeed2);
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("climatiser.humidity.1", Circuits.formerSpeed1);
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("climatiser.humidity.2", Circuits.formerSpeed2);
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryClimatology blocks = getBlocks();

		RecipeUtil.addRecipe("habitatformer", new ItemStack(blocks.habitatformer),
			"GRG",
			"TST",
			"BCB",
			'S', coreItems.sturdyCasing,
			'G', OreDictUtil.BLOCK_GLASS,
			'B', OreDictUtil.GEAR_BRONZE,
			'R', OreDictUtil.DUST_REDSTONE,
			'C', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC),
			'T', coreItems.tubes.get(EnumElectronTube.IRON, 1));


		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.habitatformer");
		if (layout == null) {
			return;
		}
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuits.formerRange1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.LAPIS, 1), Circuits.formerRange2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.EMERALD, 1), Circuits.formerRange3);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.IRON, 1), Circuits.formerEfficiency1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.formerEfficiency2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.DIAMOND, 1), Circuits.formerEfficiency3);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuits.formerSpeed1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BLAZE, 1), Circuits.formerSpeed2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.ENDER, 1), Circuits.formerSpeed3);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryClimatology();
	}

}
