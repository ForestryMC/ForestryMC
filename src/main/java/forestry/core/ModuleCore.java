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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
import forestry.core.data.ForestryTags;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreContainers;
import forestry.core.features.CoreFeatures;
import forestry.core.features.CoreItems;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.gui.GuiEscritoire;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.particles.CoreParticles;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.core.description", coreModule = true)
public class ModuleCore extends BlankForestryModule {
	public static final LiteralArgumentBuilder<CommandSource> rootCommand = LiteralArgumentBuilder.literal("forestry");

	public ModuleCore() {
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

		MultiblockManager.logicFactory = new MultiblockLogicFactory();

		RecipeManagers.hygroregulatorManager = new HygroregulatorManager();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerGuiFactories() {
		ScreenManager.register(CoreContainers.ALYZER.containerType(), GuiAlyzer::new);
		ScreenManager.register(CoreContainers.ANALYZER.containerType(), GuiAnalyzer::new);
		ScreenManager.register(CoreContainers.NATURALIST_INVENTORY.containerType(), GuiNaturalistInventory::new);
		ScreenManager.register(CoreContainers.ESCRITOIRE.containerType(), GuiEscritoire::new);
		ScreenManager.register(CoreContainers.SOLDERING_IRON.containerType(), GuiSolderingIron::new);
	}


	@Override
	public void preInit() {
		GameProfileDataSerializer.register();

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
		crateRegistry.registerCrate(CoreItems.PEAT.stack());
		crateRegistry.registerCrate(CoreItems.APATITE.stack());
		crateRegistry.registerCrate(CoreItems.FERTILIZER_COMPOUND.stack());
		crateRegistry.registerCrate(CoreItems.MULCH.stack());
		crateRegistry.registerCrate(CoreItems.PHOSPHOR.stack());
		crateRegistry.registerCrate(CoreItems.ASH.stack());
		crateRegistry.registerCrate(ForestryTags.Items.INGOTS_TIN);
		crateRegistry.registerCrate(ForestryTags.Items.INGOTS_COPPER);
		crateRegistry.registerCrate(ForestryTags.Items.INGOTS_BRONZE);

		// forestry blocks
		crateRegistry.registerCrate(CoreBlocks.HUMUS.stack());
		crateRegistry.registerCrate(CoreBlocks.BOG_EARTH.stack());

		// vanilla items
		crateRegistry.registerCrate(Tags.Items.CROPS_WHEAT);
		crateRegistry.registerCrate(Items.COOKIE);
		crateRegistry.registerCrate(Tags.Items.DUSTS_REDSTONE);
		crateRegistry.registerCrate(Tags.Items.GEMS_LAPIS);
		crateRegistry.registerCrate(Items.SUGAR_CANE);
		crateRegistry.registerCrate(Items.CLAY_BALL);
		crateRegistry.registerCrate(Tags.Items.DUSTS_GLOWSTONE);
		crateRegistry.registerCrate(Items.APPLE);
		crateRegistry.registerCrate(Items.NETHER_WART);
		crateRegistry.registerCrate(Items.COAL);
		crateRegistry.registerCrate(Items.CHARCOAL);
		crateRegistry.registerCrate(Items.WHEAT_SEEDS);
		crateRegistry.registerCrate(Tags.Items.CROPS_POTATO);
		crateRegistry.registerCrate(Tags.Items.CROPS_CARROT);
		crateRegistry.registerCrate(Tags.Items.CROPS_BEETROOT);
		crateRegistry.registerCrate(Tags.Items.CROPS_NETHER_WART);

		// vanilla blocks
		crateRegistry.registerCrate(Items.OAK_LOG);    //TODO - use tags?
		crateRegistry.registerCrate(Items.BIRCH_LOG);
		crateRegistry.registerCrate(Items.JUNGLE_LOG);
		crateRegistry.registerCrate(Items.SPRUCE_LOG);
		crateRegistry.registerCrate(Items.ACACIA_LOG);
		crateRegistry.registerCrate(Items.DARK_OAK_LOG);
		crateRegistry.registerCrate(Tags.Items.COBBLESTONE);
		crateRegistry.registerCrate(Items.DIRT);
		crateRegistry.registerCrate(Items.GRASS_BLOCK);
		crateRegistry.registerCrate(Tags.Items.STONE);
		crateRegistry.registerCrate(Items.GRANITE);
		crateRegistry.registerCrate(Items.DIORITE);
		crateRegistry.registerCrate(Items.ANDESITE);
		crateRegistry.registerCrate(Items.PRISMARINE);
		crateRegistry.registerCrate(Items.PRISMARINE_BRICKS);
		crateRegistry.registerCrate(Items.DARK_PRISMARINE);
		crateRegistry.registerCrate(Items.BRICKS);
		crateRegistry.registerCrate(Items.CACTUS);
		crateRegistry.registerCrate(Items.SAND);
		crateRegistry.registerCrate(Items.RED_SAND);
		crateRegistry.registerCrate(Tags.Items.OBSIDIAN);
		crateRegistry.registerCrate(Tags.Items.NETHERRACK);
		crateRegistry.registerCrate(Items.SOUL_SAND);
		crateRegistry.registerCrate(Tags.Items.SANDSTONE);
		crateRegistry.registerCrate(Items.NETHER_BRICKS);
		crateRegistry.registerCrate(Items.MYCELIUM);
		crateRegistry.registerCrate(Tags.Items.GRAVEL);
		crateRegistry.registerCrate(Items.OAK_SAPLING);
		crateRegistry.registerCrate(Items.BIRCH_SAPLING);
		crateRegistry.registerCrate(Items.JUNGLE_SAPLING);
		crateRegistry.registerCrate(Items.SPRUCE_SAPLING);
		crateRegistry.registerCrate(Items.ACACIA_SAPLING);
		crateRegistry.registerCrate(Items.DARK_OAK_SAPLING);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryCore();
	}

	@Override
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		if (message.getMethod().equals("blacklist-ores-dimension")) {
			ResourceLocation[] dims = (ResourceLocation[]) message.getMessageSupplier().get();    //TODO - how does IMC work
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

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return Proxies.render;
	}
}
