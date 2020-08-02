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

import net.minecraft.client.gui.ScreenManager;

import net.minecraftforge.fml.DistExecutor;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.utils.ForgeUtils;
import forestry.energy.features.EnergyContainers;
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

    public ModuleEnergy() {
        //set up proxies as early as possible
        proxy = DistExecutor.runForDist(() -> ProxyEnergyClient::new, () -> ProxyEnergy::new);
        ForgeUtils.registerSubscriber(this);
    }

    @Override
    public void registerGuiFactories() {
        ScreenManager.registerFactory(EnergyContainers.ENGINE_ELECTRIC.containerType(), GuiEngineElectric::new);
        ScreenManager.registerFactory(EnergyContainers.ENGINE_BIOGAS.containerType(), GuiEngineBiogas::new);
        ScreenManager.registerFactory(EnergyContainers.ENGINE_PEAT.containerType(), GuiEnginePeat::new);
        ScreenManager.registerFactory(EnergyContainers.GENERATOR.containerType(), GuiGenerator::new);
    }

}
