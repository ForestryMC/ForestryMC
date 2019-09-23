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
package forestry.energy;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.energy.gui.EnergyContainerTypes;
import forestry.energy.gui.GuiEngineBiogas;
import forestry.energy.gui.GuiEngineElectric;
import forestry.energy.gui.GuiEnginePeat;
import forestry.energy.gui.GuiGenerator;
import forestry.energy.proxy.ProxyEnergy;
import forestry.energy.proxy.ProxyEnergyClient;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.ENERGY, name = "Energy", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.energy.description")
public class ModuleEnergy extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	public static ProxyEnergy proxy;

	@Nullable
	public static EnergyContainerTypes containerTypes;

	public ModuleEnergy() {
		//set up proxies as early as possible
		proxy = DistExecutor.runForDist(() -> () -> new ProxyEnergyClient(), () -> () -> new ProxyEnergy());
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	public static EnergyContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new EnergyContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		EnergyContainerTypes containerTypes = getContainerTypes();
		ScreenManager.registerFactory(containerTypes.ENGINE_ELECTRIC, GuiEngineElectric::new);
		ScreenManager.registerFactory(containerTypes.ENGINE_BIOGAS, GuiEngineBiogas::new);
		ScreenManager.registerFactory(containerTypes.ENGINE_PEAT, GuiEnginePeat::new);
		ScreenManager.registerFactory(containerTypes.GENERATOR, GuiGenerator::new);
	}

}
