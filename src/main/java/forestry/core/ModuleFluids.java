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
package forestry.core;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Preference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import forestry.core.utils.ForgeUtils;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

//TODO: Move the fluid and block creation to the new feature system if the fluid system is more final (Do we really need a source and a flowing fluid ?)
@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FLUIDS, name = "Fluids", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.module.fluids.description")
public class ModuleFluids extends BlankForestryModule {

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public void preInit() {
		ForgeUtils.registerSubscriber(this);
	}

	@Override
	public void doInit() {
		FluidStack ethanol = ForestryFluids.BIO_ETHANOL.getFluid(1);
		if (!ethanol.isEmpty()) {
			GeneratorFuel ethanolFuel = new GeneratorFuel(ethanol, (int) (32 * Preference.FUEL_MODIFIER), 4);
			FuelManager.generatorFuel.put(ethanol.getFluid(), ethanolFuel);
		}

		FluidStack biomass = ForestryFluids.BIOMASS.getFluid(1);
		if (!biomass.isEmpty()) {
			GeneratorFuel biomassFuel = new GeneratorFuel(biomass, (int) (8 * Preference.FUEL_MODIFIER), 1);
			FuelManager.generatorFuel.put(biomass.getFluid(), biomassFuel);
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleTextureStitchPre(TextureStitchEvent.Pre event) {
		if (event.getMap().location() != PlayerContainer.BLOCK_ATLAS) {
			return;
		}
		for (ForestryFluids fluid : ForestryFluids.values()) {
			for (ResourceLocation resource : fluid.getFeature().properties().resources) {
				event.addSprite(resource);
			}
		}
	}
}
