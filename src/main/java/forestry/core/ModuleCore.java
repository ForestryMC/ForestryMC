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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.circuits.ChipsetManager;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.modules.ForestryModule;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.GuiSolderingIron;
import forestry.core.circuits.SolderManager;
import forestry.core.commands.CommandModules;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreContainers;
import forestry.core.features.CoreItems;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.gui.GuiEscritoire;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.loot.OrganismFunction;
import forestry.core.models.ClientManager;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.core.description", coreModule = true)
public class ModuleCore extends BlankForestryModule {
    public static final LiteralArgumentBuilder<CommandSource> rootCommand = LiteralArgumentBuilder.literal("forestry");

    public ModuleCore() {
        MinecraftForge.EVENT_BUS.register(this);
        ForgeUtils.registerSubscriber(this);
    }

    @Override
    public boolean canBeDisabled() {
        return false;
    }

    @Override
    public Set<ResourceLocation> getDependencyUids() {
        return Collections.emptySet();
    }

    @Override
    public void setupAPI() {
        ChipsetManager.solderManager = new SolderManager();

        ChipsetManager.circuitRegistry = new CircuitRegistry();

        AlleleManager.climateHelper = new ClimateUtil();
        AlleleManager.alleleFactory = new AlleleFactory();

        //LootFunctionManager.registerFunction(new OrganismFunction.Serializer());

        MultiblockManager.logicFactory = new MultiblockLogicFactory();

        RecipeManagers.hygroregulatorManager = new HygroregulatorManager();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(CoreContainers.ALYZER.containerType(), GuiAlyzer::new);
        ScreenManager.registerFactory(CoreContainers.ANALYZER.containerType(), GuiAnalyzer::new);
        ScreenManager.registerFactory(CoreContainers.NATURALIST_INVENTORY.containerType(), GuiNaturalistInventory::new);
        ScreenManager.registerFactory(CoreContainers.ESCRITOIRE.containerType(), GuiEscritoire::new);
        ScreenManager.registerFactory(CoreContainers.SOLDERING_IRON.containerType(), GuiSolderingIron::new);
    }


    @Override
    public void preInit() {
        GameProfileDataSerializer.register();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ClimateHandlerServer());

        rootCommand.then(CommandModules.register());
    }

    @Nullable
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return rootCommand;
    }

    @Override
    public void doInit() {
        ForestryModEnvWarningCallable.register();

        Proxies.render.initRendering();

        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome.getCategory() == Biome.Category.NETHER || biome.getCategory() == Biome.Category.THEEND) {
                continue;
            }

//            biome.addFeature(
//                    GenerationStage.Decoration.UNDERGROUND_ORES,
//                    Feature.ORE.withConfiguration(new OreFeatureConfig(
//                            OreFeatureConfig.FillerBlockType.NATURAL_STONE,
//                            CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).defaultState(),
//                            36
//                    )).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(4, 56, 0, 184)))
//            );
//            biome.addFeature(
//                    GenerationStage.Decoration.UNDERGROUND_ORES,
//                    Feature.ORE.withConfiguration(new OreFeatureConfig(
//                            OreFeatureConfig.FillerBlockType.NATURAL_STONE,
//                            CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).defaultState(),
//                            6
//                    )).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(20, 32, 0, 76)))
//            );
//            biome.addFeature(
//                    GenerationStage.Decoration.UNDERGROUND_ORES,
//                    Feature.ORE.withConfiguration(new OreFeatureConfig(
//                            OreFeatureConfig.FillerBlockType.NATURAL_STONE,
//                            CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).defaultState(),
//                            6
//                    )).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(20, 16, 0, 76)))
//            );
        }
    }

    @Override
    public ISaveEventHandler getSaveEventHandler() {
        return new SaveEventHandlerCore();
    }

    @Override
    public void registerCrates() {
        ICrateRegistry crateRegistry = StorageManager.crateRegistry;

        // forestry items
        crateRegistry.registerCrate(CoreItems.PEAT);
        crateRegistry.registerCrate(CoreItems.APATITE);
        crateRegistry.registerCrate(CoreItems.FERTILIZER_COMPOUND);
        crateRegistry.registerCrate(CoreItems.MULCH);
        crateRegistry.registerCrate(CoreItems.PHOSPHOR);
        crateRegistry.registerCrate(CoreItems.ASH);
        crateRegistry.registerCrate(OreDictUtil.INGOT_TIN);
        crateRegistry.registerCrate(OreDictUtil.INGOT_COPPER);
        crateRegistry.registerCrate(OreDictUtil.INGOT_BRONZE);

        // forestry blocks
        crateRegistry.registerCrate(CoreBlocks.HUMUS);
        crateRegistry.registerCrate(CoreBlocks.BOG_EARTH);

        // vanilla items
        crateRegistry.registerCrate(OreDictUtil.CROP_WHEAT);
        crateRegistry.registerCrate(Items.COOKIE);
        crateRegistry.registerCrate(OreDictUtil.DUST_REDSTONE);
        crateRegistry.registerCrate(new ItemStack(Items.LAPIS_LAZULI, 1));    //TODO - I think...
        crateRegistry.registerCrate("sugarcane");
        crateRegistry.registerCrate(Items.CLAY_BALL);
        crateRegistry.registerCrate("dustGlowstone");
        crateRegistry.registerCrate(Items.APPLE);
        crateRegistry.registerCrate(new ItemStack(Items.NETHER_WART));
        crateRegistry.registerCrate(new ItemStack(Items.COAL, 1));
        crateRegistry.registerCrate(new ItemStack(Items.CHARCOAL, 1));
        crateRegistry.registerCrate(Items.WHEAT_SEEDS);
        crateRegistry.registerCrate("cropPotato");
        crateRegistry.registerCrate("cropCarrot");

        // vanilla blocks
        crateRegistry.registerCrate(new ItemStack(Blocks.OAK_LOG, 1));    //TODO - use tags?
        crateRegistry.registerCrate(new ItemStack(Blocks.BIRCH_LOG, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.JUNGLE_LOG, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.SPRUCE_LOG, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.ACACIA_LOG, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.DARK_OAK_LOG, 1));
        crateRegistry.registerCrate("cobblestone");
        crateRegistry.registerCrate("dirt");
        crateRegistry.registerCrate(new ItemStack(Blocks.GRASS_BLOCK, 1));
        crateRegistry.registerCrate("stone");
        crateRegistry.registerCrate("stoneGranite");
        crateRegistry.registerCrate("stoneDiorite");
        crateRegistry.registerCrate("stoneAndesite");
        crateRegistry.registerCrate("blockPrismarine");
        crateRegistry.registerCrate("blockPrismarineBrick");
        crateRegistry.registerCrate("blockPrismarineDark");
        crateRegistry.registerCrate(Blocks.BRICKS);
        crateRegistry.registerCrate("blockCactus");
        crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.RED_SAND, 1));
        crateRegistry.registerCrate("obsidian");
        crateRegistry.registerCrate("netherrack");
        crateRegistry.registerCrate(Blocks.SOUL_SAND);
        crateRegistry.registerCrate(Blocks.SANDSTONE);
        crateRegistry.registerCrate(Blocks.NETHER_BRICKS);
        crateRegistry.registerCrate(Blocks.MYCELIUM);
        crateRegistry.registerCrate("gravel");
        crateRegistry.registerCrate(new ItemStack(Blocks.OAK_SAPLING, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.BIRCH_SAPLING, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.JUNGLE_SAPLING, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.SPRUCE_SAPLING, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.ACACIA_SAPLING, 1));
        crateRegistry.registerCrate(new ItemStack(Blocks.DARK_OAK_SAPLING, 1));
    }

    @Override
    public IPacketRegistry getPacketRegistry() {
        return new PacketRegistryCore();
    }

    @Override
    public boolean processIMCMessage(InterModComms.IMCMessage message) {
        if (message.getMethod().equals("blacklist-ores-dimension")) {
            //TODO - how does IMC work
            ResourceLocation[] dims = (ResourceLocation[]) message.getMessageSupplier().get();
            for (ResourceLocation dim : dims) {
                Config.blacklistOreDim(dim);
            }
            return true;
        }
        return false;
    }

    @Override
    public IPickupHandler getPickupHandler() {
        return new PickupHandlerCore();
    }

    @Override
    public void getHiddenItems(List<ItemStack> hiddenItems) {
        // research note items are not useful without actually having completed research
        hiddenItems.add(CoreItems.RESEARCH_NOTE.stack());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBakeModels(ModelBakeEvent event) {
        ClientManager.getInstance().onBakeModels(event);
    }

    @SubscribeEvent
    public void onRegisterLoot(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        //TODO: Remove if forge adds missing registry types.
        OrganismFunction.type = Registry.register(
                Registry.LOOT_FUNCTION_TYPE,
                new ResourceLocation(Constants.MOD_ID, "set_species_nbt"),
                new LootFunctionType(new OrganismFunction.Serializer())
        );
    }

    @Override
    public ISidedModuleHandler getModuleHandler() {
        return Proxies.render;
    }
}
