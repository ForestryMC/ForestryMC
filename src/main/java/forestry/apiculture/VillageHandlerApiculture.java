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
package forestry.apiculture;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

import forestry.api.apiculture.EnumBeeType;
import forestry.apiculture.blocks.BlockApicultureType;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.worldgen.ComponentVillageBeeHouse;
import forestry.core.utils.Log;
import forestry.plugins.PluginApiculture;

public class VillageHandlerApiculture implements IVillageCreationHandler, IVillageTradeHandler {

	public static void registerVillageComponents() {
		try {
			MapGenStructureIO.func_143031_a(ComponentVillageBeeHouse.class, "Forestry:BeeHouse");
		} catch (Throwable e) {
			Log.severe("Failed to register village beehouse.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		ItemStack wildcardPrincess = new ItemStack(PluginApiculture.items.beePrincessGE, 1, OreDictionary.WILDCARD_VALUE);
		ItemStack wildcardDrone = new ItemStack(PluginApiculture.items.beeDroneGE, 4, OreDictionary.WILDCARD_VALUE);
		recipeList.add(new MerchantRecipe(wildcardPrincess, new ItemStack(Items.emerald, 1)));

		ItemStack randomComb = PluginApiculture.items.beeComb.getRandomComb(1, random, false);
		recipeList.add(new MerchantRecipe(new ItemStack(Items.wheat, 2), randomComb));

		ItemStack apiary = PluginApiculture.blocks.apiculture.get(BlockApicultureType.APIARY);
		recipeList.add(new MerchantRecipe(new ItemStack(Blocks.log, 24, OreDictionary.WILDCARD_VALUE), apiary));

		ItemStack provenFrames = PluginApiculture.items.frameProven.getItemStack(6);
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), provenFrames));

		ItemStack monasticDrone = BeeDefinition.MONASTIC.getMemberStack(EnumBeeType.DRONE);
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 12), wildcardPrincess, monasticDrone));

		ItemStack endDrone = BeeDefinition.ENDED.getMemberStack(EnumBeeType.DRONE);
		recipeList.add(new MerchantRecipe(new ItemStack(Items.ender_eye, 16), wildcardPrincess, endDrone));

		ItemStack propolis = new ItemStack(PluginApiculture.items.propolis,1);
		ItemStack forestDrone = BeeDefinition.FOREST.getMemberStack(EnumBeeType.DRONE);
		ItemStack meadowsDrone = BeeDefinition.MEADOWS.getMemberStack(EnumBeeType.DRONE);
		ItemStack modestDrone = BeeDefinition.MODEST.getMemberStack(EnumBeeType.DRONE);
		ItemStack tropicalDrone = BeeDefinition.TROPICAL.getMemberStack(EnumBeeType.DRONE);
		ItemStack wintryDrone = BeeDefinition.WINTRY.getMemberStack(EnumBeeType.DRONE);
		ItemStack marshyDrone = BeeDefinition.MARSHY.getMemberStack(EnumBeeType.DRONE);
		recipeList.add(new MerchantRecipe(propolis, wildcardDrone, forestDrone));
		recipeList.add(new MerchantRecipe(propolis, wildcardDrone, meadowsDrone));
		recipeList.add(new MerchantRecipe(propolis, wildcardDrone, modestDrone));
		recipeList.add(new MerchantRecipe(propolis, wildcardDrone, tropicalDrone));
		recipeList.add(new MerchantRecipe(propolis, wildcardDrone, wintryDrone));
		recipeList.add(new MerchantRecipe(propolis, wildcardDrone, marshyDrone));
	}

	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
		return new StructureVillagePieces.PieceWeight(ComponentVillageBeeHouse.class, 15, MathHelper.getRandomIntegerInRange(random, size, 1 + size));
	}

	@Override
	public Class<?> getComponentClass() {
		return ComponentVillageBeeHouse.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2,
			int p3, int p4, int p5) {
		return ComponentVillageBeeHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}
}
