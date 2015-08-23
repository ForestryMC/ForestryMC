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
import net.minecraft.entity.passive.EntityVillager.EmeraldForItems;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.ItemAndEmeraldToItem;
import net.minecraft.entity.passive.EntityVillager.ListEnchantedBookForEmeralds;
import net.minecraft.entity.passive.EntityVillager.ListEnchantedItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.ListItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VanillaTrades;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import forestry.api.apiculture.EnumBeeType;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.items.ItemHoneycomb;
import forestry.apiculture.worldgen.ComponentVillageBeeHouse;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginApiculture;

public class VillageHandlerApiculture implements IVillageCreationHandler {
	
	public static void init()
	{
		VillagerProfession prof = new VillagerProfession("beekeeper", Defaults.TEXTURE_SKIN_BEEKEEPER);
		{
			(new VillagerCareer(prof, "beekeeper")).init(trades[0][0]);
		}
	}
	
	public static void registerVillageComponents() {
		try {
			MapGenStructureIO.registerStructure(ComponentVillageBeeHouse.class, "Forestry:BeeHouse");
		} catch (Throwable e) {
			Proxies.log.severe("Failed to register village beehouse.");
		}
	}
	
	private static final ITradeList[][][][] trades =
        {
            {
                {
                    {
                        new EmeraldForItems(Items.wheat, new PriceInfo(18, 22)),
                        new EmeraldForItems(Items.potato, new PriceInfo(15, 19)),
                        new EmeraldForItems(Items.carrot, new PriceInfo(15, 19)),
                        new ListItemForEmeralds(Items.bread, new PriceInfo(-4, -2))
                    },
                    {
                        new EmeraldForItems(Item.getItemFromBlock(Blocks.pumpkin), new PriceInfo(8, 13)),
                        new ListItemForEmeralds(Items.pumpkin_pie, new PriceInfo(-3, -2))
                    },
                    {
                        new EmeraldForItems(Item.getItemFromBlock(Blocks.melon_block), new PriceInfo(7, 12)),
                        new ListItemForEmeralds(Items.apple, new PriceInfo(-5, -7))
                    },
                    {
                        new ListItemForEmeralds(Items.cookie, new PriceInfo(-6, -10)),
                        new ListItemForEmeralds(Items.cake, new PriceInfo(1, 1))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.string, new PriceInfo(15, 20)),
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ItemAndEmeraldToItem(Items.fish, new PriceInfo(6, 6), Items.cooked_fish, new PriceInfo(6, 6))
                    },
                    {
                        new ListEnchantedItemForEmeralds(Items.fishing_rod, new PriceInfo(7, 8))
                    }
                },
                {
                    {
                        new EmeraldForItems(Item.getItemFromBlock(Blocks.wool), new PriceInfo(16, 22)),
                        new ListItemForEmeralds(Items.shears, new PriceInfo(3, 4))
                    },
                    {
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 0), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 1), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 2), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 3), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 4), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 5), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 6), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 7), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 8), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 9), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 10), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 11), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 12), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 13), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 14), new PriceInfo(1, 2)),
                        new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 15), new PriceInfo(1, 2))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.string, new PriceInfo(15, 20)),
                        new ListItemForEmeralds(Items.arrow, new PriceInfo(-12, -8))
                    },
                    {
                        new ListItemForEmeralds(Items.bow, new PriceInfo(2, 3)),
                        new ItemAndEmeraldToItem(Item.getItemFromBlock(Blocks.gravel), new PriceInfo(10, 10), Items.flint, new PriceInfo(6, 10))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.paper, new PriceInfo(24, 36)),
                        new ListEnchantedBookForEmeralds()
                    },
                    {
                        new EmeraldForItems(Items.book, new PriceInfo(8, 10)),
                        new ListItemForEmeralds(Items.compass, new PriceInfo(10, 12)),
                        new ListItemForEmeralds(Item.getItemFromBlock(Blocks.bookshelf), new PriceInfo(3, 4))
                    },
                    {
                        new EmeraldForItems(Items.written_book, new PriceInfo(2, 2)),
                        new ListItemForEmeralds(Items.clock, new PriceInfo(10, 12)),
                        new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glass), new PriceInfo(-5, -3))
                    },
                    {
                        new ListEnchantedBookForEmeralds()
                    },
                    {
                        new ListEnchantedBookForEmeralds()
                    },
                    {
                        new ListItemForEmeralds(Items.name_tag, new PriceInfo(20, 22))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.rotten_flesh, new PriceInfo(36, 40)),
                        new EmeraldForItems(Items.gold_ingot, new PriceInfo(8, 10))
                    },
                    {
                        new ListItemForEmeralds(Items.redstone, new PriceInfo(-4, -1)),
                        new ListItemForEmeralds(new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
                        new PriceInfo(-2, -1))
                    },
                    {
                        new ListItemForEmeralds(Items.ender_eye, new PriceInfo(7, 11)),
                        new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glowstone), new PriceInfo(-3, -1))
                    },
                    {
                        new ListItemForEmeralds(Items.experience_bottle, new PriceInfo(3, 11))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListItemForEmeralds(Items.iron_helmet, new PriceInfo(4, 6))
                    },
                    {
                        new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)),
                        new ListItemForEmeralds(Items.iron_chestplate, new PriceInfo(10, 14))
                    },
                    {
                        new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)),
                        new ListEnchantedItemForEmeralds(Items.diamond_chestplate, new PriceInfo(16, 19))
                    },
                    {
                        new ListItemForEmeralds(Items.chainmail_boots, new PriceInfo(5, 7)),
                        new ListItemForEmeralds(Items.chainmail_leggings, new PriceInfo(9, 11)),
                        new ListItemForEmeralds(Items.chainmail_helmet, new PriceInfo(5, 7)),
                        new ListItemForEmeralds(Items.chainmail_chestplate, new PriceInfo(11, 15))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListItemForEmeralds(Items.iron_axe, new PriceInfo(6, 8))
                    },
                    {
                        new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)),
                        new ListEnchantedItemForEmeralds(Items.iron_sword, new PriceInfo(9, 10))
                    },
                    {
                        new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)),
                        new ListEnchantedItemForEmeralds(Items.diamond_sword, new PriceInfo(12, 15)),
                        new ListEnchantedItemForEmeralds(Items.diamond_axe, new PriceInfo(9, 12))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListEnchantedItemForEmeralds(Items.iron_shovel, new PriceInfo(5, 7))
                    },
                    {
                        new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9)),
                        new ListEnchantedItemForEmeralds(Items.iron_pickaxe, new PriceInfo(9, 11))
                    },
                    {
                        new EmeraldForItems(Items.diamond, new PriceInfo(3, 4)),
                        new ListEnchantedItemForEmeralds(Items.diamond_pickaxe, new PriceInfo(12, 15))
                    }
                }
            },
            {
                {
                    {
                        new EmeraldForItems(Items.porkchop, new PriceInfo(14, 18)),
                        new EmeraldForItems(Items.chicken, new PriceInfo(14, 18))
                    },
                    {
                        new EmeraldForItems(Items.coal, new PriceInfo(16, 24)),
                        new ListItemForEmeralds(Items.cooked_porkchop, new PriceInfo(-7, -5)),
                        new ListItemForEmeralds(Items.cooked_chicken, new PriceInfo(-8, -6))
                    }
                },
                {
                    {
                        new EmeraldForItems(Items.leather, new PriceInfo(9, 12)),
                        new ListItemForEmeralds(Items.leather_leggings, new PriceInfo(2, 4))
                    },
                    {
                        new ListEnchantedItemForEmeralds(Items.leather_chestplate, new PriceInfo(7, 12))
                    },
                    {
                        new ListItemForEmeralds(Items.saddle, new PriceInfo(8, 10))
                    }
                }
            }
        };
    }

	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		recipeList.add(new MerchantRecipe(ForestryItem.beePrincessGE.getItemStack(1, Defaults.WILDCARD), new ItemStack(Items.emerald, 1)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.wheat, 2), ItemHoneycomb.getRandomComb(1, random, false)));
		recipeList.add(new MerchantRecipe(new ItemStack(Blocks.log, 24, Defaults.WILDCARD), ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_APIARY_META)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), ForestryItem.frameProven.getItemStack(6)));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 12), ForestryItem.beePrincessGE.getItemStack(1, Defaults.WILDCARD),
				PluginApiculture.beeInterface.getMemberStack(
						PluginApiculture.beeInterface.getBee(villager.worldObj, PluginApiculture.beeInterface.templateAsGenome(BeeTemplates.getMonasticTemplate())),
						EnumBeeType.DRONE.ordinal())));
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
	public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
		return ComponentVillageBeeHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
	}
}
