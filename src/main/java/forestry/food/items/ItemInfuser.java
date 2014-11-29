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
package forestry.food.items;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.food.BeverageManager;
import forestry.api.food.IBeverageEffect;
import forestry.api.food.IInfuserManager;
import forestry.api.food.IIngredientManager;
import forestry.core.CreativeTabForestry;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.food.BeverageEffect;
import forestry.plugins.PluginManager;

public class ItemInfuser extends ItemForestry {

	// / RECIPE MANAGMENT
	/**
	 * Mixture describes the itemstacks required to achieve a certain effect.
	 */
	public static class Mixture {

		private final int meta;
		private final ItemStack[] ingredients;
		private final IBeverageEffect effect;

		public Mixture(int meta, ItemStack ingredient, IBeverageEffect effect) {
			this(meta, new ItemStack[] { ingredient }, effect);
		}

		public Mixture(int meta, ItemStack ingredients[], IBeverageEffect effect) {
			this.meta = meta;
			this.ingredients = ingredients;
			this.effect = effect;
		}

		public ItemStack[] getIngredients() {
			return ingredients;
		}

		public boolean isIngredient(ItemStack itemstack) {
			for (ItemStack ingredient : ingredients) {
				if (ingredient.getItemDamage() < 0 && ingredient.getItem() == itemstack.getItem())
					return true;
				else if (ingredient.getItemDamage() >= 0 && ingredient.isItemEqual(itemstack))
					return true;
			}

			return false;
		}

		public boolean matches(ItemStack[] res) {

			// No recipe without resource!
			if (res == null || res.length <= 0)
				return false;

			boolean matchedAll = true;

			for (ItemStack stack : ingredients) {
				boolean matched = false;
				for (ItemStack matchStack : res) {
					if (matchStack == null)
						continue;

					// Check item matching
					if (stack.getItemDamage() < 0 && stack.getItem() == matchStack.getItem()) {
						if (stack.stackSize <= matchStack.stackSize) {
							matched = true;
							break;
						}
					} else if (stack.getItemDamage() >= 0 && stack.isItemEqual(matchStack))
						if (stack.stackSize <= matchStack.stackSize) {
							matched = true;
							break;
						}
				}
				if (!matched)
					matchedAll = false;
			}
			return matchedAll;

		}

		public IBeverageEffect getEffect() {
			return this.effect;
		}

		public int getMeta() {
			return meta;
		}

		public int getWeight() {
			return ingredients.length;
		}
	}

	/**
	 * MixtureManager contains the available mixtures.
	 */
	public static class MixtureManager implements IInfuserManager {

		private final ArrayList<ItemInfuser.Mixture> mixtures = new ArrayList<Mixture>();

		@Override
		public void addMixture(int meta, ItemStack ingredient, IBeverageEffect effect) {
			this.mixtures.add(new Mixture(meta, ingredient, effect));
		}

		@Override
		public void addMixture(int meta, ItemStack[] ingredients, IBeverageEffect effect) {
			this.mixtures.add(new Mixture(meta, ingredients, effect));
		}

		public boolean isIngredient(ItemStack itemstack) {
			for (Mixture ingredient : mixtures) {
				if (ingredient.isIngredient(itemstack))
					return true;
			}

			return false;
		}

		private Mixture[] getMatchingMixtures(ItemStack[] ingredients) {

			ArrayList<Mixture> matches = new ArrayList<Mixture>();

			for (Mixture mixture : mixtures) {
				if (mixture.matches(ingredients))
					matches.add(mixture);
			}

			return matches.toArray(new Mixture[matches.size()]);
		}

		@Override
		public boolean hasMixtures(ItemStack[] ingredients) {
			return getMatchingMixtures(ingredients).length > 0;
		}

		@Override
		public ItemStack[] getRequired(ItemStack[] ingredients) {
			Mixture[] mixtures = getMatchingMixtures(ingredients);
			ArrayList<ItemStack> required = new ArrayList<ItemStack>();

			for (Mixture mixture : mixtures) {
				required.addAll(Arrays.asList(mixture.getIngredients()));
			}

			return required.toArray(new ItemStack[required.size()]);
		}

		@Override
		public ItemStack getSeasoned(ItemStack base, ItemStack[] ingredients) {
			Mixture[] mixtures = getMatchingMixtures(ingredients);
			ArrayList<IBeverageEffect> effects = new ArrayList<IBeverageEffect>();
			effects.addAll(Arrays.asList(((ItemBeverage) base.getItem()).beverages[base.getItemDamage()].loadEffects(base)));

			int weight = 0;
			int meta = 0;
			for (Mixture mixture : mixtures) {
				effects.add(mixture.getEffect());
				if (mixture.getWeight() > weight) {
					weight = mixture.getWeight();
					meta = mixture.getMeta();
				}
			}

			ItemStack seasoned = base.copy();
			seasoned.setItemDamage(meta);
			((ItemBeverage) base.getItem()).beverages[meta].saveEffects(seasoned, effects.toArray(new IBeverageEffect[effects.size()]));
			return seasoned;
		}
	}

	public static class Ingredient {

		public final ItemStack ingredient;
		public final String description;

		public Ingredient(ItemStack ingredient, String description) {
			this.ingredient = ingredient;
			this.description = description;
		}
	}

	public static class IngredientManager implements IIngredientManager {

		private final ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();

		@Override
		public void addIngredient(ItemStack ingredient, String description) {
			this.ingredients.add(new Ingredient(ingredient, description));
		}

		@Override
		public String getDescription(ItemStack itemstack) {
			if (itemstack == null)
				return null;

			for (Ingredient ingredient : ingredients) {
				if (ingredient.ingredient.getItemDamage() < 0 && ingredient.ingredient.getItem() == itemstack.getItem())
					return ingredient.description;
				else if (ingredient.ingredient.getItemDamage() >= 0 && ingredient.ingredient.isItemEqual(itemstack))
					return ingredient.description;
			}

			return null;
		}
	}

	// / INVENTORY MANAGMENT
	public static class InfuserInventory implements IInventory {

		private final ItemStack[] inventoryStacks = new ItemStack[6];
		private final short inputSlot = 0;
		private final short outputSlot = 1;
		private final short ingredientSlot1 = 2;
		EntityPlayer player;

		public InfuserInventory(EntityPlayer player) {
			this.player = player;
		}

		private void trySeasoning() {

			// Need input
			if (inventoryStacks[inputSlot] == null)
				return;

			// Output slot may not be occupied
			if (inventoryStacks[outputSlot] != null)
				return;

			// Need a valid base
			if (!inventoryStacks[inputSlot].isItemEqual(ForestryItem.beverage.getItemStack()))
				return;

			// Create the seasoned item
			ItemStack[] ingredients = new ItemStack[4];
			System.arraycopy(inventoryStacks, ingredientSlot1, ingredients, 0, 4);

			// Only continue if there is anything to season
			if (!BeverageManager.infuserManager.hasMixtures(ingredients))
				return;

			ItemStack seasoned = BeverageManager.infuserManager.getSeasoned(inventoryStacks[inputSlot], ingredients);
			if (seasoned == null)
				return;

			// Remove required ingredients.
			ItemStack[] toRemove = BeverageManager.infuserManager.getRequired(ingredients);
			for (ItemStack templ : toRemove) {
				ItemStack ghost = templ.copy();

				for (int i = ingredientSlot1; i < this.getSizeInventory(); i++) {
					if (inventoryStacks[i] == null)
						continue;
					if (ghost.stackSize <= 0)
						break;

					if ((ghost.getItemDamage() >= 0 && inventoryStacks[i].isItemEqual(ghost))
							|| (ghost.getItemDamage() < 0 && ghost.getItem() == inventoryStacks[i].getItem())) {
						ItemStack removed = decrStackSize(i, 1);
						ghost.stackSize -= removed.stackSize;
					}
				}
			}
			decrStackSize(inputSlot, 1);
			inventoryStacks[outputSlot] = seasoned;

		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			if (inventoryStacks[i] == null)
				return null;

			ItemStack product;
			if (inventoryStacks[i].stackSize <= j) {
				product = inventoryStacks[i];
				inventoryStacks[i] = null;
				return product;
			} else {
				product = inventoryStacks[i].splitStack(j);
				if (inventoryStacks[i].stackSize == 0)
					inventoryStacks[i] = null;

				return product;
			}
		}

		@Override
		public void markDirty() {
			if (!Proxies.common.isSimulating(player.worldObj))
				return;
			trySeasoning();
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			inventoryStacks[i] = itemstack;
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return inventoryStacks[i];
		}

		@Override
		public int getSizeInventory() {
			return inventoryStacks.length;
		}

		@Override
		public String getInventoryName() {
			return "Infuser";
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void openInventory() {
		}

		@Override
		public void closeInventory() {
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			if (inventoryStacks[slot] == null)
				return null;
			ItemStack toReturn = inventoryStacks[slot];
			inventoryStacks[slot] = null;
			return toReturn;
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return true;
		}
	}

	public ItemInfuser() {
		super();
		setMaxStackSize(1);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world))
			entityplayer.openGui(ForestryAPI.instance, GuiId.InfuserGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);

		return itemstack;
	}

	public static void initialize() {
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			BeverageManager.ingredientManager.addIngredient(ForestryItem.pollenCluster.getItemStack(1, 0), "Strong Curative");
			BeverageManager.ingredientManager.addIngredient(ForestryItem.pollenCluster.getItemStack(1, 1), "Weak Curative");
			BeverageManager.infuserManager.addMixture(1, ForestryItem.pollenCluster.getItemStack(1, 0), BeverageEffect.strongAntidote);
			BeverageManager.infuserManager.addMixture(1, ForestryItem.pollenCluster.getItemStack(1, 1), BeverageEffect.weakAntidote);
		}
	}
}
