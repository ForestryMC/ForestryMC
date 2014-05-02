/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.mail;

import net.minecraft.inventory.IInventory;

public interface ITradeStation extends ILetterHandler, IInventory {

	String getMoniker();

	boolean isValid();

	void invalidate();

	void setVirtual(boolean isVirtual);

	boolean isVirtual();

	TradeStationInfo getTradeInfo();

}
