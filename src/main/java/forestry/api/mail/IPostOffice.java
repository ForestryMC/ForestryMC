/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import java.util.Map;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public interface IPostOffice {

	void collectPostage(NonNullList<ItemStack> stamps);

	IPostalState lodgeLetter(ServerLevel world, ItemStack itemstack, boolean doLodge);

	ItemStack getAnyStamp(int max);

	ItemStack getAnyStamp(EnumPostage postage, int max);

	ItemStack getAnyStamp(EnumPostage[] postages, int max);

	void registerTradeStation(ITradeStation trade);

	void deregisterTradeStation(ITradeStation trade);

	Map<IMailAddress, ITradeStation> getActiveTradeStations(Level world);
}
