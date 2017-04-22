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
package forestry.lepidopterology;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import forestry.Forestry;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.AlleleSpeciesRegisterEvent;
import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.lepidopterology.commands.CommandButterfly;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.ButterflyBranchDefinition;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.lepidopterology.genetics.ButterflyFactory;
import forestry.lepidopterology.genetics.ButterflyMutationFactory;
import forestry.lepidopterology.genetics.ButterflyRoot;
import forestry.lepidopterology.genetics.MothDefinition;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.items.ItemRegistryLepidopterology;
import forestry.lepidopterology.proxy.ProxyLepidopterology;
import forestry.lepidopterology.recipes.MatingRecipe;
import forestry.lepidopterology.render.ModelButterflyItem;
import forestry.lepidopterology.tiles.TileCocoon;
import forestry.lepidopterology.worldgen.CocoonDecorator;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

@ForestryPlugin(pluginID = ForestryPluginUids.LEPIDOPTEROLOGY, name = "Lepidopterology", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.lepidopterology.description")
public class PluginLepidopterology extends BlankForestryPlugin {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.lepidopterology.proxy.ProxyLepidopterologyClient", serverSide = "forestry.lepidopterology.proxy.ProxyLepidopterology")
	public static ProxyLepidopterology proxy;
	private static final String CONFIG_CATEGORY = "lepidopterology";
	public static int spawnConstraint = 100;
	public static int entityConstraint = 1000;
	public static int maxDistance = 64;
	private static boolean allowPollination = true;
	public static final Map<String, Float> spawnRaritys = Maps.newHashMap();
	private static boolean spawnButterflysFromLeaves = true;
	private static boolean generateCocoons = false;
	private static float generateCocoonsAmount = 1.0f;
	private static float serumChance = 0.55f;
	private static float secondSerumChance = 0;

	@Nullable
	private static ItemRegistryLepidopterology items;
	@Nullable
	private static BlockRegistryLepidopterology blocks;

	public static ItemRegistryLepidopterology getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	public static BlockRegistryLepidopterology getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void setupAPI() {
		ButterflyManager.butterflyRoot = new ButterflyRoot();
		AlleleManager.alleleRegistry.registerSpeciesRoot(ButterflyManager.butterflyRoot);

		ButterflyManager.butterflyFactory = new ButterflyFactory();
		ButterflyManager.butterflyMutationFactory = new ButterflyMutationFactory();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryLepidopterology();
		blocks = new BlockRegistryLepidopterology();
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		ButterflyBranchDefinition.createAlleles();
		ButterflyAlleles.registerEffectAlleles();

		GameRegistry.registerTileEntity(TileCocoon.class, "forestry.Cocoon");
		proxy.preInitializeRendering();
	}

	@Override
	public Set<String> getDependencyUids() {
		Set<String> dependencyUids = super.getDependencyUids();
		dependencyUids.add(ForestryPluginUids.ARBORICULTURE);
		return dependencyUids;
	}

	@Override
	public void doInit() {
		BlockRegistryLepidopterology blocks = getBlocks();

		PluginCore.rootCommand.addChildCommand(new CommandButterfly());

		ResourceLocation butterflyResourceLocation = new ResourceLocation(Constants.MOD_ID, "butterflyGE");
		EntityUtil.registerEntity(butterflyResourceLocation, EntityButterfly.class, "butterflyGE", 0, 0x000000,
				0xffffff, 50, 1, true);

		MothDefinition.initMoths();
		ButterflyDefinition.initButterflies();
		ButterflyAlleles.createLoot();

		MinecraftForge.EVENT_BUS.post(new AlleleSpeciesRegisterEvent(ButterflyManager.butterflyRoot, IAlleleButterflySpecies.class));

		blocks.butterflyChest.init();

		if (spawnButterflysFromLeaves) {
			TreeManager.treeRoot.registerLeafTickHandler(new ButterflySpawner());
		}

		RecipeSorter.register("forestry:lepidopterologymating", MatingRecipe.class, RecipeSorter.Category.SHAPELESS,
				"before:minecraft:shapeless");
	}

	@Override
	public void postInit() {
		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");
		loadConfig(configFile);
	}

	@Override
	public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ,
			boolean hasVillageGenerated) {
		if (generateCocoons) {
			if (generateCocoonsAmount > 0.0) {
				CocoonDecorator.decorateCocoons(chunkGenerator, world, rand, chunkX, chunkZ, hasVillageGenerated);
			}
		}
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		if (generateCocoons) {
			if (generateCocoonsAmount > 0.0) {
				CocoonDecorator.decorateCocoons(world, rand, chunkX, chunkZ);
			}
		}
	}

	private static void loadConfig(File configFile) {
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.1.0");

		spawnConstraint = config.getIntLocalized("butterfly.entities", "spawn.limit", spawnConstraint, 0, 500);
		entityConstraint = config.getIntLocalized("butterfly.entities", "maximum", entityConstraint, 0, 5000);
		maxDistance = config.getIntLocalized("butterfly.entities", "maxDistance", maxDistance, 0, 256);
		allowPollination = config.getBooleanLocalized("butterfly.entities", "pollination", allowPollination);
		spawnButterflysFromLeaves = config.getBooleanLocalized("butterfly.entities", "spawn.leaves",
				spawnButterflysFromLeaves);

		generateCocoons = config.getBooleanLocalized("butterfly.cocoons", "generate", generateCocoons);
		generateCocoonsAmount = config.getFloatLocalized("butterfly.cocoons", "generate.amount", generateCocoonsAmount,
				0.0f, 10.0f);

		serumChance = config.getFloatLocalized("butterfly.cocoons", "serum", serumChance, 0.0f, 100.0f);
		secondSerumChance = config.getFloatLocalized("butterfly.cocoons", "second.serum", secondSerumChance, 0.0f,
				100.0f);

		parseRarity(config);
		parseCooconLoots(config);

		config.save();
	}

	private static void parseRarity(LocalizedConfiguration config) {
		List<String> butterflyRarity = Lists.newArrayList();
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleButterflySpecies) {
				IAlleleButterflySpecies species = (IAlleleButterflySpecies) allele;
				butterflyRarity.add(species.getUID() + ":" + species.getRarity());
			}
		}
		Collections.sort(butterflyRarity);
		String[] defaultRaritys = butterflyRarity.toArray(new String[butterflyRarity.size()]);

		Property rarityConf = config.get("butterfly.alleles", "rarity", defaultRaritys);
		rarityConf.setComment(Translator.translateToLocal("for.config.butterfly.alleles.rarity"));

		String[] configRaritys = rarityConf.getStringList();
		for (String rarity : configRaritys) {
			if (rarity.contains(":") && rarity.length() > 3) {
				String[] raritys = rarity.split(":");
				try {
					spawnRaritys.put(raritys[0], Float.parseFloat(raritys[1]));
				} catch (Exception e) {
					Log.error("Failed to parse spawn rarity for butterfly. {}", rarity, e);
				}
			}
		}
	}

	private static void parseCooconLoots(LocalizedConfiguration config) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleButterflyCocoon) {
				parseCooconLoot(config, (IAlleleButterflyCocoon) allele);
			}
		}
	}

	private static void parseCooconLoot(LocalizedConfiguration config, IAlleleButterflyCocoon cocoon) {
		Map<ItemStack, Float> cooconLoot = new HashMap<>();
		List<String> lootList = new ArrayList<>();
		for (Entry<ItemStack, Float> entry : cocoon.getCocoonLoot().entrySet()) {
			String itemStackString = ItemStackUtil.getItemNameFromRegistryAsString(entry.getKey().getItem());

			int meta = entry.getKey().getItemDamage();
			if (meta != OreDictionary.WILDCARD_VALUE) {
				itemStackString = itemStackString + ':' + meta;
			}
			lootList.add(itemStackString + ";" + entry.getValue());
		}
		Collections.sort(lootList);
		String[] defaultLoot = lootList.toArray(new String[lootList.size()]);

		Property lootConf = config.get("butterfly.cocoons.alleles.loot", cocoon.getUID(), defaultLoot);
		lootConf.setComment(Translator.translateToLocal("for.config.butterfly.alleles.loot"));

		String[] configLoot = lootConf.getStringList();
		for (String loot : configLoot) {
			if (loot.contains(";") && loot.length() > 3) {
				String[] loots = loot.split(";");
				try {
					ItemStack itemStack = ItemStackUtil.parseItemStackString(loots[0], OreDictionary.WILDCARD_VALUE);
					if (itemStack != null) {
						cooconLoot.put(itemStack, Float.parseFloat(loots[1]));
					}
				} catch (Exception e) {
					Log.error("Failed to parse cocoon loot. {}", loot, e);
				}
			}
		}
		cocoon.clearLoot();
		for (Entry<ItemStack, Float> entry : cocoon.getCocoonLoot().entrySet()) {
			cocoon.addLoot(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void registerRecipes() {
		BlockRegistryLepidopterology blocks = getBlocks();
		ItemRegistryLepidopterology items = getItems();

		CraftingManager.getInstance().getRecipeList().add(new MatingRecipe());

		RecipeUtil.addRecipe(blocks.butterflyChest, " # ", "XYX", "XXX", '#', "blockGlass", 'X',
				new ItemStack(items.butterflyGE, 1, OreDictionary.WILDCARD_VALUE), 'Y', "chestWood");
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		BlockRegistryLepidopterology blocks = getBlocks();

		// cocoon itemBlock is different from the normal item
		hiddenItems.add(new ItemStack(blocks.cocoon));
		hiddenItems.add(new ItemStack(blocks.solidCocoon));
	}

	public static boolean isPollinationAllowed() {
		return allowPollination;
	}

	public static boolean isSpawnButterflysFromLeaves() {
		return spawnButterflysFromLeaves;
	}

	public static boolean isGenerateCocoons() {
		return generateCocoons;
	}

	public static float getGenerateCocoonsAmount() {
		return generateCocoonsAmount;
	}

	public static float getSerumChance() {
		return serumChance;
	}

	public static float getSecondSerumChance() {
		return secondSerumChance;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelBake(ModelBakeEvent event) {
		ModelButterflyItem.onModelBake(event);
	}
}
