/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.farming;


import forestry.Forestry;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.features.CoreItems;
import forestry.farming.features.FarmingContainers;
import forestry.farming.gui.GuiFarm;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.*;
import forestry.farming.proxy.ProxyFarming;
import forestry.farming.proxy.ProxyFarmingClient;
import forestry.farming.triggers.FarmingTriggers;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import forestry.modules.ModuleHelper;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

//import forestry.arboriculture.genetics.alleles.AlleleFruits;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FARMING, name = "Farming", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.farming.description")
public class ModuleFarming extends BlankForestryModule {

    @Nullable
    public static ProxyFarming proxy;

    public ModuleFarming() {
        proxy = DistExecutor.runForDist(() -> ProxyFarmingClient::new, () -> ProxyFarming::new);
    }


    @Override
    public void setupAPI() {
        ForestryAPI.farmRegistry = FarmRegistry.getInstance();
    }

    @Override
    public void disabledSetupAPI() {
        ForestryAPI.farmRegistry = new DummyFarmRegistry();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(FarmingContainers.FARM.containerType(), GuiFarm::new);
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        IFarmRegistry registry = ForestryAPI.farmRegistry;
        registry.registerFarmables(
                ForestryFarmIdentifier.ARBOREAL,
                new FarmableVanillaSapling(Blocks.OAK_SAPLING),
                new FarmableVanillaSapling(Blocks.SPRUCE_SAPLING),
                new FarmableVanillaSapling(Blocks.ACACIA_SAPLING),
                new FarmableVanillaSapling(Blocks.BIRCH_SAPLING),
                new FarmableVanillaSapling(Blocks.JUNGLE_SAPLING),
                new FarmableVanillaSapling(Blocks.DARK_OAK_SAPLING)
        );
        if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
            registry.registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableGE());
        }

        registry.registerFarmables(
                ForestryFarmIdentifier.CROPS,
                new FarmableAgingCrop(
                        new ItemStack(Items.WHEAT_SEEDS),
                        Blocks.WHEAT,
                        new ItemStack(Items.WHEAT),
                        CropsBlock.AGE,
                        7,
                        0
                ),
                new FarmableAgingCrop(
                        new ItemStack(Items.POTATO),
                        Blocks.POTATOES,
                        new ItemStack(Items.POTATO),
                        CropsBlock.AGE,
                        7,
                        0
                ),
                new FarmableAgingCrop(
                        new ItemStack(Items.CARROT),
                        Blocks.CARROTS,
                        new ItemStack(Items.CARROT),
                        CropsBlock.AGE,
                        7,
                        0
                ),
                new FarmableAgingCrop(
                        new ItemStack(Items.BEETROOT_SEEDS),
                        Blocks.BEETROOTS,
                        new ItemStack(Items.BEETROOT),
                        BeetrootBlock.BEETROOT_AGE,
                        3,
                        0
                )
        );

        registry.registerFarmables(
                ForestryFarmIdentifier.SHROOM,
                new FarmableVanillaMushroom(
                        new ItemStack(Blocks.RED_MUSHROOM),
                        Blocks.RED_MUSHROOM_BLOCK.getDefaultState(),
                        Blocks.RED_MUSHROOM_BLOCK
                ),
                new FarmableVanillaMushroom(
                        new ItemStack(Blocks.BROWN_MUSHROOM),
                        Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState(),
                        Blocks.BROWN_MUSHROOM_BLOCK
                )
        );

        registry.registerFarmables(
                ForestryFarmIdentifier.GOURD,
                new FarmableGourd(new ItemStack(Items.MELON_SEEDS), Blocks.MELON_STEM, Blocks.MELON),
                new FarmableGourd(new ItemStack(Items.PUMPKIN_SEEDS), Blocks.PUMPKIN_STEM, Blocks.PUMPKIN)
        );

        registry.registerFarmables(
                ForestryFarmIdentifier.INFERNAL,
                new FarmableAgingCrop(new ItemStack(Items.NETHER_WART), Blocks.NETHER_WART, NetherWartBlock.AGE, 3)
        );

        registry.registerFarmables(
                ForestryFarmIdentifier.POALES,
                new FarmableStacked(new ItemStack(Items.SUGAR_CANE), Blocks.SUGAR_CANE, 3)
        );

        registry.registerFarmables(
                ForestryFarmIdentifier.SUCCULENTES,
                new FarmableStacked(new ItemStack(Blocks.CACTUS), Blocks.CACTUS, 3)
        );

        registry.registerFarmables(ForestryFarmIdentifier.ENDER, FarmableChorus.INSTANCE);

        //Forestry fertilizer
        //TODO - tags
        registry.registerFertilizer(CoreItems.FERTILIZER_COMPOUND.stack(), 500);

        // Layouts
        ICircuitLayout layoutManaged = new CircuitLayout("farms.managed", CircuitSocketType.FARM);
        ChipsetManager.circuitRegistry.registerLayout(layoutManaged);
        ICircuitLayout layoutManual = new CircuitLayout("farms.manual", CircuitSocketType.FARM);
        ChipsetManager.circuitRegistry.registerLayout(layoutManual);
    }

    @Override
    public void registerTriggers() {
        FarmingTriggers.initialize();
    }

    @Override
    public void doInit() {
        //Load config
        File configFile = new File(Forestry.instance.getConfigFolder(), Config.CATEGORY_FARM + ".cfg");
        LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");
        FarmRegistry.getInstance().loadConfig(config);
        config.save();

        FarmDefinition.init();
    }

    @Override
    public void getHiddenItems(List<ItemStack> hiddenItems) {
        // mushrooms are a workaround for the farm and should not be obtainable
        //		hiddenItems.add(new ItemStack(getBlocks().mushroom, 1, OreDictionary.WILDCARD_VALUE));
        //TODO - tag
    }

    @Override
    public ISidedModuleHandler getModuleHandler() {
        return proxy;
    }
}
