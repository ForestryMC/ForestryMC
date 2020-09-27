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

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.EnumContainerType;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;

//TODO: Move the fluid and block creation to the new feature system if the fluid system is more final (Do we really need a source and a flowing fluid ?)
@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FLUIDS, name = "Fluids", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.module.fluids.description")
public class ModuleFluids extends BlankForestryModule {

    @Override
    public boolean canBeDisabled() {
        return false;
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void doInit() {
        if (RecipeManagers.squeezerManager != null) {
            RecipeManagers.squeezerManager.addContainerRecipe(
                    10,
                    FluidsItems.CONTAINERS.stack(EnumContainerType.CAN),
                    CoreItems.INGOT_TIN.stack(),
                    0.05f
            );
            RecipeManagers.squeezerManager.addContainerRecipe(
                    10,
                    FluidsItems.CONTAINERS.stack(EnumContainerType.CAPSULE),
                    CoreItems.BEESWAX.stack(),
                    0.10f
            );
            RecipeManagers.squeezerManager.addContainerRecipe(
                    10,
                    FluidsItems.CONTAINERS.stack(EnumContainerType.REFRACTORY),
                    CoreItems.REFRACTORY_WAX.stack(),
                    0.10f
            );
        }

        FluidStack ethanol = ForestryFluids.BIO_ETHANOL.getFluid(1);
        if (!ethanol.isEmpty()) {
            GeneratorFuel ethanolFuel = new GeneratorFuel(
                    ethanol,
                    (int) (32 * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.generator")),
                    4
            );
            FuelManager.generatorFuel.put(ethanol.getFluid(), ethanolFuel);
        }

        FluidStack biomass = ForestryFluids.BIOMASS.getFluid(1);
        if (!biomass.isEmpty()) {
            GeneratorFuel biomassFuel = new GeneratorFuel(
                    biomass,
                    (int) (8 * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.generator")),
                    1
            );
            FuelManager.generatorFuel.put(biomass.getFluid(), biomassFuel);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void handleTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation() != PlayerContainer.LOCATION_BLOCKS_TEXTURE) {
            return;
        }
        for (ForestryFluids fluid : ForestryFluids.values()) {
            for (ResourceLocation resource : fluid.getFeature().getProperties().resources) {
                event.addSprite(resource);
            }
        }
    }
}
