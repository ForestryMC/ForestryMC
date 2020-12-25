/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.mail;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public interface IPostOffice {

    void collectPostage(NonNullList<ItemStack> stamps);

    IPostalState lodgeLetter(ServerWorld world, ItemStack itemstack, boolean doLodge);

    ItemStack getAnyStamp(int max);

    ItemStack getAnyStamp(EnumPostage postage, int max);

    ItemStack getAnyStamp(EnumPostage[] postages, int max);

    void registerTradeStation(ITradeStation trade);

    void deregisterTradeStation(ITradeStation trade);

    Map<IMailAddress, ITradeStation> getActiveTradeStations(World world);
}
