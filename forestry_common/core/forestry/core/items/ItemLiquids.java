/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
