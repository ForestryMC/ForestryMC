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
package forestry.storage;

import forestry.Forestry;
import forestry.api.modules.ForestryModule;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.api.storage.StorageManager;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.config.forge_old.Property;
import forestry.core.features.CoreItems;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.features.BackpackContainers;
import forestry.storage.gui.GuiBackpack;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

@ForestryModule(moduleID = ForestryModuleUids.BACKPACKS, containerID = Constants.MOD_ID, name = "Backpack", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.backpacks.description", lootTable = "storage")
public class ModuleBackpacks extends BlankForestryModule {

    private static final String CONFIG_CATEGORY = "backpacks";
    private final Map<String, List<String>> backpackAcceptedOreDictRegexpDefaults = new HashMap<>();
    private final Map<String, List<String>> backpackAcceptedItemDefaults = new HashMap<>();

    private final List<String> forestryBackpackUids = Arrays.asList(
            BackpackManager.MINER_UID,
            BackpackManager.DIGGER_UID,
            BackpackManager.FORESTER_UID,
            BackpackManager.HUNTER_UID,
            BackpackManager.ADVENTURER_UID,
            BackpackManager.BUILDER_UID
    );

    @Override
    public void setupAPI() {
        StorageManager.crateRegistry = new CrateRegistry();

        BackpackManager.backpackInterface = new BackpackInterface();

        BackpackDefinition definition;

        if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
            Predicate<ItemStack> filter = BackpackManager.backpackInterface.createNaturalistBackpackFilter("rootBees");
            definition = new BackpackDefinition(new Color(0xc4923d), Color.WHITE, filter);
            BackpackManager.backpackInterface.registerBackpackDefinition("apiarist", definition);
        }

        if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
            Predicate<ItemStack> filter = BackpackManager.backpackInterface.createNaturalistBackpackFilter(
                    "rootButterflies");
            definition = new BackpackDefinition(new Color(0x995b31), Color.WHITE, filter);
            BackpackManager.backpackInterface.registerBackpackDefinition("lepidopterist", definition);
        }

        definition = new BackpackDefinition(new Color(0x36187d), Color.WHITE);
        BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.MINER_UID, definition);

        definition = new BackpackDefinition(new Color(0x363cc5), Color.WHITE);
        BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.DIGGER_UID, definition);

        definition = new BackpackDefinition(new Color(0x347427), Color.WHITE);
        BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.FORESTER_UID, definition);

        definition = new BackpackDefinition(new Color(0x412215), Color.WHITE);
        BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.HUNTER_UID, definition);

        definition = new BackpackDefinition(new Color(0x7fb8c2), Color.WHITE);
        BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.ADVENTURER_UID, definition);

        definition = new BackpackDefinition(new Color(0xdd3a3a), Color.WHITE);
        BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.BUILDER_UID, definition);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(BackpackContainers.BACKPACK.containerType(), GuiBackpack::new);
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit() {
        final String newConfig = CONFIG_CATEGORY + ".cfg";

        File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
        LocalizedConfiguration config = new LocalizedConfiguration(configFile, "2.0.0");
        if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion())) {
            boolean deleted = configFile.delete();
            if (deleted) {
                config = new LocalizedConfiguration(configFile, "2.0.0");
            }
        }

        setDefaultsForConfig();

        for (String backpackUid : forestryBackpackUids) {
            handleBackpackConfig(config, backpackUid);
        }

        config.save();
    }

    //TODO - in 1.13 just ship json file that people can edit, don't have config in code.
    private void setDefaultsForConfig() {
        backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.MINER_UID, Arrays.asList(
                "obsidian",
                "ore[A-Z].*",
                "dust[A-Z].*",
                "gem[A-Z].*",
                "ingot[A-Z].*",
                "nugget[A-Z].*",
                "crushed[A-Z].*",
                "cluster[A-Z].*",
                "denseore[A-Z].*"
        ));

        backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.DIGGER_UID, Arrays.asList(
                "cobblestone",
                "dirt",
                "grass",
                "grass[A-Z].*",
                "gravel",
                "netherrack",
                "stone",
                "stone[A-Z].*",
                "sandstone",
                "sand"
        ));

        backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.HUNTER_UID, Arrays.asList(
                "bone",
                "egg",
                "enderpearl",
                "feather",
                "fish[A-Z].*",
                "gunpowder",
                "leather",
                "slimeball",
                "string"
        ));

        backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.FORESTER_UID, Arrays.asList(
                "logWood",
                "stickWood",
                "woodStick",
                "saplingTree",
                "treeSapling",
                "vine",
                "sugarcane",
                "blockCactus",
                "crop[A-Z].*",
                "seed[A-Z].*",
                "tree[A-Z].*"
        ));

        backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.BUILDER_UID, Arrays.asList(
                "block[A-Z].*",
                "paneGlass[A-Z].*",
                "slabWood[A-Z].*",
                "stainedClay[A-Z].*",
                "stainedGlass[A-Z].*",
                "stone",
                "sandstone",
                OreDictUtil.PLANK_WOOD,
                OreDictUtil.STAIR_WOOD,
                OreDictUtil.SLAB_WOOD,
                OreDictUtil.FENCE_WOOD,
                OreDictUtil.FENCE_GATE_WOOD,
                OreDictUtil.TRAPDOOR_WOOD,
                "glass",
                "paneGlass",
                "torch",
                "chest",
                "chest[A-Z].*",
                "workbench",
                "doorWood"
        ));

        backpackAcceptedItemDefaults.put(BackpackManager.MINER_UID, getItemStrings(Arrays.asList(
                new ItemStack(Blocks.COAL_ORE),
                new ItemStack(Items.COAL),
                CoreItems.BRONZE_PICKAXE.stack(),
                CoreItems.KIT_PICKAXE.stack(),
                CoreItems.BROKEN_BRONZE_PICKAXE.stack()
        )));

        backpackAcceptedItemDefaults.put(BackpackManager.DIGGER_UID, getItemStrings(Arrays.asList(
                //			new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE), TODO tags
                new ItemStack(Items.FLINT),
                new ItemStack(Items.CLAY_BALL),
                new ItemStack(Items.SNOWBALL),
                new ItemStack(Blocks.SOUL_SAND),
                new ItemStack(Blocks.CLAY),
                new ItemStack(Blocks.SNOW),
                CoreItems.BRONZE_SHOVEL.stack(),
                CoreItems.KIT_SHOVEL.stack(),
                CoreItems.BROKEN_BRONZE_SHOVEL.stack()
        )));

        backpackAcceptedItemDefaults.put(BackpackManager.FORESTER_UID, getItemStrings(Arrays.asList(
                new ItemStack(Blocks.RED_MUSHROOM),
                new ItemStack(Blocks.BROWN_MUSHROOM),
                new ItemStack(Blocks.POPPY),    //TODO tag
                new ItemStack(Blocks.GRASS),    //TODO tag
                new ItemStack(Blocks.SUNFLOWER),    //TODO tag tall flowers
                new ItemStack(Blocks.PUMPKIN),
                new ItemStack(Blocks.MELON),
                new ItemStack(Items.GOLDEN_APPLE),
                new ItemStack(Items.NETHER_WART),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack(Items.PUMPKIN_SEEDS),
                new ItemStack(Items.MELON_SEEDS),
                new ItemStack(Items.BEETROOT_SEEDS),
                new ItemStack(Items.BEETROOT),
                new ItemStack(Items.CHORUS_FRUIT),
                new ItemStack(Blocks.CHORUS_PLANT),
                new ItemStack(Items.APPLE)
        )));

        backpackAcceptedItemDefaults.put(BackpackManager.HUNTER_UID, getItemStrings(Arrays.asList(
                new ItemStack(Items.BLAZE_POWDER),
                new ItemStack(Items.BLAZE_ROD),
                new ItemStack(Items.ROTTEN_FLESH),
                new ItemStack(Items.SKELETON_SKULL),    //TODO tag
                new ItemStack(Items.GHAST_TEAR),
                new ItemStack(Items.GOLD_NUGGET),
                new ItemStack(Items.ARROW),
                new ItemStack(Items.SPECTRAL_ARROW),
                new ItemStack(Items.TIPPED_ARROW),
                new ItemStack(Items.PORKCHOP),
                new ItemStack(Items.COOKED_PORKCHOP),
                new ItemStack(Items.BEEF),
                new ItemStack(Items.COOKED_BEEF),
                new ItemStack(Items.CHICKEN),
                new ItemStack(Items.COOKED_CHICKEN),
                new ItemStack(Items.MUTTON),
                new ItemStack(Items.COOKED_MUTTON),
                new ItemStack(Items.RABBIT),
                new ItemStack(Items.COOKED_RABBIT),
                new ItemStack(Items.RABBIT_FOOT),
                new ItemStack(Items.RABBIT_HIDE),
                new ItemStack(Items.SPIDER_EYE),
                new ItemStack(Items.FERMENTED_SPIDER_EYE),
                new ItemStack(Items.BONE_MEAL),    //TODO correct item?
                new ItemStack(Blocks.HAY_BLOCK),
                new ItemStack(Blocks.WHITE_WOOL),    //TODO tag
                new ItemStack(Items.ENDER_EYE),
                new ItemStack(Items.MAGMA_CREAM),
                new ItemStack(Items.GLISTERING_MELON_SLICE),    //TODO right item?
                new ItemStack(Items.COD),    //TODO tag
                new ItemStack(Items.COOKED_COD),    //TODO tag
                new ItemStack(Items.LEAD),
                new ItemStack(Items.FISHING_ROD),
                new ItemStack(Items.NAME_TAG),
                new ItemStack(Items.SADDLE),
                new ItemStack(Items.DIAMOND_HORSE_ARMOR),
                new ItemStack(Items.GOLDEN_HORSE_ARMOR),
                new ItemStack(Items.IRON_HORSE_ARMOR)
        )));

        backpackAcceptedItemDefaults.put(BackpackManager.BUILDER_UID, getItemStrings(Arrays.asList(
                new ItemStack(Blocks.REDSTONE_TORCH),
                new ItemStack(Blocks.REDSTONE_LAMP),
                new ItemStack(Blocks.SEA_LANTERN),
                new ItemStack(Blocks.END_ROD),
                new ItemStack(Blocks.STONE_BRICKS),    //TODO tag
                new ItemStack(Blocks.BRICKS),
                new ItemStack(Blocks.CLAY),
                new ItemStack(Blocks.TERRACOTTA),    //TODO tag?
                new ItemStack(Blocks.WHITE_TERRACOTTA),    //TODO tag
                new ItemStack(Blocks.WHITE_GLAZED_TERRACOTTA),    //TODO tag
                new ItemStack(Blocks.PACKED_ICE),
                new ItemStack(Blocks.NETHER_BRICKS),
                new ItemStack(Blocks.NETHER_BRICK_FENCE),
                new ItemStack(Blocks.CRAFTING_TABLE),
                new ItemStack(Blocks.FURNACE),
                new ItemStack(Blocks.LEVER),
                new ItemStack(Blocks.DISPENSER),
                new ItemStack(Blocks.DROPPER),
                new ItemStack(Blocks.LADDER),
                new ItemStack(Blocks.IRON_BARS),
                new ItemStack(Blocks.QUARTZ_BLOCK),    //TODO tag
                new ItemStack(Blocks.QUARTZ_STAIRS),
                new ItemStack(Blocks.SANDSTONE_STAIRS),
                new ItemStack(Blocks.RED_SANDSTONE_STAIRS),
                new ItemStack(Blocks.COBBLESTONE_WALL),    //TODO tag
                new ItemStack(Blocks.STONE_BUTTON),
                new ItemStack(Blocks.OAK_BUTTON),    //TODO tag
                new ItemStack(Blocks.STONE_SLAB),    //TODO tag
                new ItemStack(Blocks.SANDSTONE_SLAB),    //TODO tag
                new ItemStack(Blocks.OAK_SLAB),    //TODO tag
                new ItemStack(Blocks.PURPUR_BLOCK),
                new ItemStack(Blocks.PURPUR_PILLAR),
                new ItemStack(Blocks.PURPUR_STAIRS),
                new ItemStack(Blocks.PURPUR_SLAB),
                new ItemStack(Blocks.END_STONE_BRICKS),
                new ItemStack(Blocks.WHITE_CARPET),    //TODO tag
                new ItemStack(Blocks.IRON_TRAPDOOR),
                new ItemStack(Blocks.STONE_PRESSURE_PLATE),
                new ItemStack(Blocks.OAK_PRESSURE_PLATE),    //TODO tag
                new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE),
                new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
                new ItemStack(Items.OAK_SIGN),    //TODO tag
                new ItemStack(Items.ITEM_FRAME),
                new ItemStack(Items.ACACIA_DOOR),
                new ItemStack(Items.BIRCH_DOOR),
                new ItemStack(Items.DARK_OAK_DOOR),
                new ItemStack(Items.IRON_DOOR),
                new ItemStack(Items.JUNGLE_DOOR),
                new ItemStack(Items.OAK_DOOR),
                new ItemStack(Items.SPRUCE_DOOR)
        )));

        if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
            backpackAcceptedItemDefaults.get(BackpackManager.BUILDER_UID).addAll(getItemStrings(Arrays.asList(
                    ApicultureBlocks.CANDLE.stack(),    //TODO tag
                    ApicultureBlocks.STUMP.stack()
            )));
        }

        // include everything added via the API
        BackpackInterface backpackInterface = (BackpackInterface) BackpackManager.backpackInterface;
        backpackAcceptedItemDefaults.putAll(backpackInterface.getBackpackAcceptedItems());
    }

    private static List<String> getItemStrings(List<ItemStack> itemStacks) {
        List<String> itemStrings = new ArrayList<>(itemStacks.size());
        for (ItemStack itemStack : itemStacks) {
            String itemString = ItemStackUtil.getStringForItemStack(itemStack);
            if (itemString != null) {
                itemStrings.add(itemString);
            }
        }
        return itemStrings;
    }

    private void handleBackpackConfig(LocalizedConfiguration config, String backpackUid) {
        BackpackDefinition backpackDefinition = (BackpackDefinition) BackpackManager.backpackInterface.getBackpackDefinition(
                backpackUid);
        if (backpackDefinition == null) {
            return;
        }

        Predicate<ItemStack> filter = backpackDefinition.getFilter();
        if (filter instanceof IBackpackFilterConfigurable) {
            IBackpackFilterConfigurable backpackFilter = (IBackpackFilterConfigurable) filter;
            backpackFilter.clear();

            // accepted items
            {
                String[] defaultValidItems = new String[0];
                List<String> defaultAcceptedItemNames = backpackAcceptedItemDefaults.get(backpackUid);
                if (defaultAcceptedItemNames != null) {
                    Collections.sort(defaultAcceptedItemNames);
                    defaultValidItems = defaultAcceptedItemNames.toArray(new String[0]);
                }

                Property backpackConf = config.get(
                        "backpacks." + backpackUid,
                        "item.stacks.accepted",
                        defaultValidItems
                );
                backpackConf.setComment(new TranslationTextComponent(
                        "for.config.backpacks.item.stacks.format",
                        backpackUid
                ).getString());

                String[] backpackItemList = backpackConf.getStringList();
                //				List<ItemStack> backpackItems = ItemStackUtil.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);	//TODO tags, new config
                //				for (ItemStack backpackItem : backpackItems) {
                //					backpackFilter.acceptItem(backpackItem);
                //				}
            }

            // accepted oreDict
            {
                String[] defaultOreRegexpNames = new String[0];
                List<String> defaultOreRegexpList = backpackAcceptedOreDictRegexpDefaults.get(backpackUid);
                if (defaultOreRegexpList != null) {
                    Collections.sort(defaultOreRegexpList);
                    defaultOreRegexpNames = defaultOreRegexpList.toArray(new String[0]);
                }

                Property backpackConf = config.get(
                        "backpacks." + backpackUid,
                        "ore.dict.accepted",
                        defaultOreRegexpNames
                );
                backpackConf.setComment(new TranslationTextComponent(
                        "for.config.backpacks.ore.dict.format",
                        backpackUid
                ).getString());

                //				for (String name : OreDictionary.getOreNames()) {
                //					if (name == null) {
                //						Log.error("Found a null oreName in the ore dictionary");
                //					} else {
                //						for (String regex : backpackConf.getStringList()) {
                //							if (name.matches(regex)) {
                //								backpackFilter.acceptOreDictName(name);
                //							}
                //						}
                //					}
                //				}	//TODO new config, tags
            }
        }
    }

    @Override
    public boolean processIMCMessage(InterModComms.IMCMessage message) {
        if (message.getMethod().equals("add-backpack-items")) {
            String[] tokens = ((String) message.getMessageSupplier().get()).split("@");    //TODO new imc
            if (tokens.length != 2) {
                IMCUtil.logInvalidIMCMessage(message);
                return true;
            }

            String backpackUid = tokens[0];
            String itemStackStrings = tokens[1];

            IBackpackDefinition backpackDefinition = BackpackManager.backpackInterface.getBackpackDefinition(backpackUid);
            if (backpackDefinition == null) {
                String errorMessage = IMCUtil.getInvalidIMCMessageText(message);
                Log.error("{} For non-existent backpack {}.", errorMessage, backpackUid);
                return true;
            }

            List<ItemStack> itemStacks = ItemStackUtil.parseItemStackStrings(itemStackStrings, 0);
            for (ItemStack itemStack : itemStacks) {
                BackpackManager.backpackInterface.addItemToForestryBackpack(backpackUid, itemStack);
            }

            return true;
        }
        return false;
    }

    @Override
    public IPickupHandler getPickupHandler() {
        return new PickupHandlerStorage();
    }

    @Override
    public IResupplyHandler getResupplyHandler() {
        return new ResupplyHandler();
    }

    @Override
    public void addLootPoolNames(Set<String> lootPoolNames) {
        lootPoolNames.add("forestry_storage_items");
    }
}
