/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.circuits;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemForestry;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemCircuitBoard extends ItemForestry {

	public ItemCircuitBoard() {
		super();
		setHasSubtypes(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[] {}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[] {}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[] {}));
		itemList.add(createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[] {}));
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[2];
		icons[0] = TextureManager.getInstance().registerTex(register, getUnlocalizedName().replace("item.", "") + ".0");
		icons[1] = TextureManager.getInstance().registerTex(register, getUnlocalizedName().replace("item.", "") + ".1");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0)
			return icons[0];
		else
			return icons[1];
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[itemstack.getItemDamage()];
		if (j == 0)
			return type.primaryColor;
		else
			return type.secondaryColor;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		EnumCircuitBoardType type = EnumCircuitBoardType.values()[itemstack.getItemDamage()];
		return StringUtil.localize("item.circuitboard." + type.toString().toLowerCase(Locale.ENGLISH));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		ICircuitBoard circuitboard = ChipsetManager.circuitRegistry.getCircuitboard(itemstack);
		if (circuitboard != null)
			circuitboard.addTooltip(list);
	}

	public static ItemStack createCircuitboard(EnumCircuitBoardType type, ICircuitLayout layout, ICircuit[] circuits) {
		ItemStack chipset = ForestryItem.circuitboards.getItemStack(1, type.ordinal());
		saveChipset(chipset, new CircuitBoard(type, layout, circuits));
		return chipset;
	}

	public static void saveChipset(ItemStack itemstack, ICircuitBoard circuitboard) {
		if (circuitboard == null) {
			itemstack.setTagCompound(null);
			return;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		circuitboard.writeToNBT(nbttagcompound);
		itemstack.setTagCompound(nbttagcompound);
	}

}
