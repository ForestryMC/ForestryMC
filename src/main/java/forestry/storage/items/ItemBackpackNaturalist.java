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
package forestry.storage.items;

import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.storage.gui.ContainerNaturalistBackpack;
import forestry.storage.inventory.ItemInventoryBackpackPaged;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

public class ItemBackpackNaturalist extends ItemBackpack {
    private final String rootUid;

    public ItemBackpackNaturalist(String rootUid, IBackpackDefinition definition) {
        this(rootUid, definition, ItemGroupForestry.tabForestry);
    }

    public ItemBackpackNaturalist(String rootUid, IBackpackDefinition definition, ItemGroup tab) {
        super(definition, EnumBackpackType.NATURALIST, tab);
        this.rootUid = rootUid;
    }
    //TODO gui
    //	@Override
    //	protected void openGui(ServerPlayerEntity playerEntity, ItemStack stack) {
    //		NetworkHooks.openGui(playerEntity, ContainerNaturalistInventory,
    //		});
    //	}
    //	@Override
    //	@OnlyIn(Dist.CLIENT)
    //	public ContainerScreen getGui(PlayerEntity player, ItemStack heldItem, int page) {
    //		ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, heldItem, this);
    //		ContainerNaturalistBackpack container = new ContainerNaturalistBackpack(player, inventory, page);
    //		return new GuiNaturalistInventory(speciesRoot, player, container, page, 5);
    //	}

    @Override
    public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
        ItemInventoryBackpackPaged inventory = new ItemInventoryBackpackPaged(player, Constants.SLOTS_BACKPACK_APIARIST, heldItem, this);
        return new ContainerNaturalistBackpack(windowId, player.inventory, inventory, 0);    //TODO init on first page? Or is this server desync?
    }

    //TODO see if this can be deduped. Given we pass in the held item etc.
    public static class ContainerProvider implements INamedContainerProvider {

        private final ItemStack heldItem;

        public ContainerProvider(ItemStack heldItem) {
            this.heldItem = heldItem;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent("ITEM_GUI_TITLE");    //TODO needs to be overriden individually
        }

        @Nullable
        @Override
        public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            Item item = heldItem.getItem();
            if (!(item instanceof ItemBackpackNaturalist)) {
                return null;
            }
            ItemBackpackNaturalist backpack = (ItemBackpackNaturalist) item;
            return backpack.getContainer(windowId, playerEntity, heldItem);
        }
    }
}
