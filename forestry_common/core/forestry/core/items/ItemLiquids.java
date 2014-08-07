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
package forestry.core.items;

public class ItemLiquids extends ItemForestry {

	public ItemLiquids() {
		super();
		setCreativeTab(null);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	/*
	 * //@Override Client side only public String getItemStackDisplayName(ItemStack itemstack) { switch(itemstack.getItemDamage()) { case 0: return "Liquid Honey";
	 * case 1: return "Liquid Mead"; default: return "Unknown Item"; } }
	 * 
	 * //@Override Client side only public int getIconFromDamage(int damage) { switch(damage) { case 0: return 76; case 1: return 77; default: return 0; } }
	 * 
	 * @Override public void addCreativeItems(ArrayList itemList) { //for(int i = 0; i < 2; i++) // itemList.add(new ItemStack(this, 1, i)); }
	 */
}
