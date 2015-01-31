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
package forestry.plugins;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableGenericCrop;


@Plugin(pluginID = "HarvestCraft", name = "HarvestCraft", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.harvestcraft.description")
public class PluginHarvestCraft extends ForestryPlugin {

    private static final String HC = "harvestcraft";

    @Override
    public boolean isAvailable() {
        return Proxies.common.isModLoaded(HC);
    }

    @Override
    public String getFailMessage() {
        return "HarvestCraft not found";
    }

    @Override
    public void doInit() {
        super.doInit();
    }

    @Override
    protected void registerRecipes() {

        ArrayList<String> berryList = new ArrayList<String>();
        berryList.add("cranberry");
        berryList.add("blackberry");
        berryList.add("blueberry");
        berryList.add("raspberry");
        berryList.add("strawberry");

        ArrayList<String> fruitList = new ArrayList<String>();
        fruitList.add("pineapple");
        fruitList.add("cactusfruit");
        fruitList.add("cantaloupe");
        fruitList.add("grape");
        fruitList.add("kiwi");
        fruitList.add("chilipepper");

        ArrayList<String> treeFruitList = new ArrayList<String>();
        //treeFruitList.add("apple");
        treeFruitList.add("banana");
        treeFruitList.add("dragonfruit");
        treeFruitList.add("lemon");
        treeFruitList.add("lime");
        treeFruitList.add("mango");
        treeFruitList.add("orange");
        treeFruitList.add("papaya");
        treeFruitList.add("peach");
        treeFruitList.add("pear");
        treeFruitList.add("plum");
        treeFruitList.add("pomegranate");
        treeFruitList.add("starfruit");
        treeFruitList.add("apricot");
        treeFruitList.add("date");
        treeFruitList.add("fig");
        treeFruitList.add("grapefruit");
        treeFruitList.add("persimmon");
        treeFruitList.add("avocado");
        treeFruitList.add("coconut");
        treeFruitList.add("durian");

        ArrayList<String> treeList = new ArrayList<String>();
        treeList.add("nutmeg");
        treeList.add("olive");
        treeList.add("peppercorn");

//        ArrayList<String> treeSpecialList = new ArrayList<String>();
//        treeSpecialList.add("cinnamon");
//        treeSpecialList.add("maple");
//        treeSpecialList.add("paperbark");
//        treeSpecialList.add("vanilla");

        ArrayList<String> herbList = new ArrayList<String>();
        herbList.add("garlic");

        ArrayList<String> spiceList = new ArrayList<String>();
        spiceList.add("ginger");
        spiceList.add("spiceleaf");
        spiceList.add("mustardseed");

        ArrayList<String> vegetableList = new ArrayList<String>();
        vegetableList.add("asparagus");
        vegetableList.add("bean");
        vegetableList.add("beet");
        vegetableList.add("broccoli");
        vegetableList.add("cauliflower");
        vegetableList.add("celery");
        vegetableList.add("leek");
        vegetableList.add("lettuce");
        vegetableList.add("onion");
        vegetableList.add("parsnip");
        vegetableList.add("radish");
        vegetableList.add("rutabaga");
        vegetableList.add("scallion");
        vegetableList.add("soybean");
        vegetableList.add("sweetpotato");
        vegetableList.add("turnip");
        vegetableList.add("whitemushroom");
        vegetableList.add("artichoke");
        vegetableList.add("bellpepper");
        vegetableList.add("brusselsprout");
        vegetableList.add("cabbage");
        vegetableList.add("corn");
        vegetableList.add("cucumber");
        vegetableList.add("eggplant");
        vegetableList.add("okra");
        vegetableList.add("peas");
        vegetableList.add("rhubarb");
        vegetableList.add("seaweed");
        vegetableList.add("tomato");
        vegetableList.add("wintersquash");
        vegetableList.add("zucchini");
        vegetableList.add("bambooshoot");

        ArrayList<String> grainList = new ArrayList<String>();
        grainList.add("barley");
        grainList.add("oats");
        grainList.add("rye");

        ArrayList<String> cropnutList = new ArrayList<String>();
        cropnutList.add("peanut");

        ArrayList<String> nutList = new ArrayList<String>();
        nutList.add("walnut");
        nutList.add("almond");
        nutList.add("cashew");
        nutList.add("chestnut");
        nutList.add("pecan");
        nutList.add("pistachio");
        nutList.add("cherry"); //Cherries in forestry make seed oil

        ArrayList<String> genericCropList = new ArrayList<String>();
        genericCropList.add("cotton");
        genericCropList.add("rice");
        genericCropList.add("tea");
        genericCropList.add("coffee");
        genericCropList.add("candleberry");

        ArrayList<String> plantList = new ArrayList<String>();

        int amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") / 25;
        int seedamount =  GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
        ItemStack wheatamount = GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat");
        amount = Math.max(amount, 1); // Produce at least 1 mb of juice.
        for (String aBerryList : berryList) {
            ItemStack berry = GameRegistry.findItemStack(HC, aBerryList + "Item", 1);
            ItemStack berrySeed = GameRegistry.findItemStack(HC, aBerryList + "seedItem", 1);
            Block berryBlock = GameRegistry.findBlock(HC, "pam" + aBerryList + "Crop");
            if(berry != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{berry}, Fluids.JUICE.getFluid(amount));
            if(berrySeed != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{berrySeed}, Fluids.SEEDOIL.getFluid(seedamount));
            if(berrySeed != null && berryBlock != null)
                Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(berrySeed, berryBlock, 7));
            plantList.add(aBerryList);
        }
        amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
        for (String aFruitList : fruitList) {
            ItemStack fruit = GameRegistry.findItemStack(HC,aFruitList + "Item",1);
            ItemStack fruitSeed = GameRegistry.findItemStack(HC,aFruitList + "seedItem",1);
            Block fruitBlock = GameRegistry.findBlock(HC, "pam" + aFruitList + "Crop");
            if(fruit != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{fruit}, Fluids.JUICE.getFluid(amount));
            if(fruitSeed != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{fruitSeed}, Fluids.SEEDOIL.getFluid(seedamount));
            if(fruitSeed != null && fruitBlock != null)
                Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(fruitSeed, fruitBlock, 7));
            plantList.add(aFruitList);
        }
        for (String aVegetabletList : vegetableList) {
            ItemStack vegetable = GameRegistry.findItemStack(HC,aVegetabletList + "Item",1);
            ItemStack vegetableSeed = GameRegistry.findItemStack(HC,aVegetabletList + "seedItem",1);
            Block vegetableBlock = GameRegistry.findBlock(HC, "pam" + aVegetabletList + "Crop");
            if(vegetable != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{vegetable}, Fluids.VEGETABLE_JUICE.getFluid(amount));
            if(vegetableSeed != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{vegetableSeed}, Fluids.SEEDOIL.getFluid(seedamount));
            if(vegetableSeed != null && vegetableBlock != null)
                Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(vegetableSeed, vegetableBlock, 7));
            plantList.add(aVegetabletList);
        }
        for (String aGrainList : grainList) {
            ItemStack grain = GameRegistry.findItemStack(HC,aGrainList + "Item",1);
            ItemStack grainSeed = GameRegistry.findItemStack(HC,aGrainList + "seedItem",1);
            Block grainBlock = GameRegistry.findBlock(HC, "pam" + aGrainList + "Crop");
            if (grain != null && wheatamount.stackSize > 0) {
                Proxies.common.addRecipe(wheatamount, " X ", "X#X", " X ", '#', Blocks.dirt, 'X', grain);
            }
            if(grainSeed != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{grainSeed}, Fluids.SEEDOIL.getFluid(seedamount));
            if(grainSeed != null && grainBlock != null)
                Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(grainSeed, grainBlock, 7));
            plantList.add(aGrainList);
        }
        for (String aHerbList : herbList) {
            genericCropList.add(aHerbList);
        }
        for (String aSpiceList : spiceList) {
            genericCropList.add(aSpiceList);
        }
        for (String aTreeFruitList : treeFruitList) {
            ItemStack treeFruit = GameRegistry.findItemStack(HC, aTreeFruitList + "Item", 1);
            if(treeFruit != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{treeFruit}, Fluids.JUICE.getFluid(amount));
            plantList.add(aTreeFruitList);
        }
        for (String aTreeList : treeList) {
            plantList.add(aTreeList);
        }
        for (String aGenericCropList : genericCropList) {
            ItemStack genericCropSeed = GameRegistry.findItemStack(HC,aGenericCropList + "seedItem",1);
            Block genericCropBlock = GameRegistry.findBlock(HC, "pam" + aGenericCropList + "Crop");
            if(genericCropSeed != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{genericCropSeed}, Fluids.SEEDOIL.getFluid(seedamount));
            if(genericCropSeed != null && genericCropBlock != null)
                Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(genericCropSeed, genericCropBlock, 7));
            plantList.add(aGenericCropList);
        }
        for (String aPlantList : plantList) {
            ItemStack plant = GameRegistry.findItemStack(HC,aPlantList + "Item",1);
            if(plant != null)
            RecipeUtil.injectLeveledRecipe(plant, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
        }
        for (String aCropnutList : cropnutList) {
            ItemStack cropnutSeed = GameRegistry.findItemStack(HC,aCropnutList + "seedItem",1);
            Block cropnutBlock = GameRegistry.findBlock(HC, "pam" + aCropnutList + "Crop");
            if(cropnutSeed != null)
                RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{cropnutSeed}, Fluids.SEEDOIL.getFluid(seedamount));
            if(cropnutSeed != null && cropnutBlock != null)
                Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(cropnutSeed, cropnutBlock, 7));
            nutList.add(aCropnutList);
        }
        for (String aNutList : nutList) {
            ItemStack nut = GameRegistry.findItemStack(HC, aNutList + "Item", 1);
            if(nut != null)
                RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{nut}, Fluids.SEEDOIL.getFluid(3 * seedamount));
        }
        ItemStack HChoneyItem = GameRegistry.findItemStack(HC,"honeyItem",1);
        if (HChoneyItem != null)
        RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{HChoneyItem}, Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP));
        ItemStack HCbeeswaxItem = GameRegistry.findItemStack(HC, "beeswaxItem",1);
        if (HCbeeswaxItem != null)
        Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.capsule"), "XXX ", 'X', HCbeeswaxItem);
    }

}
