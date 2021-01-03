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
import forestry.core.features.CoreFeatures;
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
import forestry.core.particles.CoreParticles;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.ForgeUtils;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

        CoreParticles.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
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
        CoreFeatures.registerOres();
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
        crateRegistry.registerCrate(CoreItems.INGOT_TIN);
        crateRegistry.registerCrate(CoreItems.INGOT_COPPER);
        crateRegistry.registerCrate(CoreItems.INGOT_BRONZE);

        // forestry blocks
        crateRegistry.registerCrate(CoreBlocks.HUMUS);
        crateRegistry.registerCrate(CoreBlocks.BOG_EARTH);

        // vanilla items
        crateRegistry.registerCrate(Items.WHEAT);
        crateRegistry.registerCrate(Items.COOKIE);
        crateRegistry.registerCrate(Items.REDSTONE);
        crateRegistry.registerCrate(Items.LAPIS_LAZULI);
        crateRegistry.registerCrate(Items.SUGAR_CANE);
        crateRegistry.registerCrate(Items.CLAY_BALL);
        crateRegistry.registerCrate(Items.PRISMARINE_SHARD);
        crateRegistry.registerCrate(Items.APPLE);
        crateRegistry.registerCrate(Items.NETHER_WART);
        crateRegistry.registerCrate(Items.COAL);
        crateRegistry.registerCrate(Items.CHARCOAL);
        crateRegistry.registerCrate(Items.WHEAT_SEEDS);
        crateRegistry.registerCrate(Items.POTATO);
        crateRegistry.registerCrate(Items.CARROT);

        // vanilla blocks
        crateRegistry.registerCrate(Blocks.OAK_LOG);
        crateRegistry.registerCrate(Blocks.BIRCH_LOG);
        crateRegistry.registerCrate(Blocks.JUNGLE_LOG);
        crateRegistry.registerCrate(Blocks.SPRUCE_LOG);
        crateRegistry.registerCrate(Blocks.ACACIA_LOG);
        crateRegistry.registerCrate(Blocks.DARK_OAK_LOG);
        crateRegistry.registerCrate(Blocks.COBBLESTONE);
        crateRegistry.registerCrate(Blocks.DIRT);
        crateRegistry.registerCrate(Blocks.GRASS_BLOCK);
        crateRegistry.registerCrate(Blocks.STONE);
        crateRegistry.registerCrate(Blocks.GRANITE);
        crateRegistry.registerCrate(Blocks.DIORITE);
        crateRegistry.registerCrate(Blocks.ANDESITE);
        crateRegistry.registerCrate(Blocks.PRISMARINE);
        crateRegistry.registerCrate(Blocks.PRISMARINE_BRICKS);
        crateRegistry.registerCrate(Blocks.DARK_PRISMARINE);
        crateRegistry.registerCrate(Blocks.BRICKS);
        crateRegistry.registerCrate(Blocks.CACTUS);
        crateRegistry.registerCrate(Blocks.SAND);
        crateRegistry.registerCrate(Blocks.RED_SAND);
        crateRegistry.registerCrate(Blocks.OBSIDIAN);
        crateRegistry.registerCrate(Blocks.NETHERRACK);
        crateRegistry.registerCrate(Blocks.SOUL_SAND);
        crateRegistry.registerCrate(Blocks.SANDSTONE);
        crateRegistry.registerCrate(Blocks.NETHER_BRICKS);
        crateRegistry.registerCrate(Blocks.MYCELIUM);
        crateRegistry.registerCrate(Blocks.GRAVEL);
        crateRegistry.registerCrate(Blocks.OAK_SAPLING);
        crateRegistry.registerCrate(Blocks.BIRCH_SAPLING);
        crateRegistry.registerCrate(Blocks.JUNGLE_SAPLING);
        crateRegistry.registerCrate(Blocks.SPRUCE_SAPLING);
        crateRegistry.registerCrate(Blocks.ACACIA_SAPLING);
        crateRegistry.registerCrate(Blocks.DARK_OAK_SAPLING);
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
