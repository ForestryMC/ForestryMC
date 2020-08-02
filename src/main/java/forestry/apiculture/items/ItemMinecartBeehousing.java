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
package forestry.apiculture.items;

import java.util.Locale;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.IItemSubtype;
import forestry.api.core.ItemGroups;
import forestry.apiculture.entities.MinecartEntityApiary;
import forestry.apiculture.entities.MinecartEntityBeeHousingBase;
import forestry.apiculture.entities.MinecartEntityBeehouse;

public class ItemMinecartBeehousing extends MinecartItem {

    //TODO merge with BlockTypeApiculture?
    public enum Type implements IItemSubtype {
        BEE_HOUSE,
        APIARY;

        @Override
        public String getString() {
            return toString().toLowerCase(Locale.ENGLISH);
        }
    }

    private final Type type;

    public ItemMinecartBeehousing(Type type) {
        super(null, (new Item.Properties()).maxDamage(0).group(ItemGroups.tabApiculture));
        this.type = type;

        DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.put(this, IDispenseItemBehavior.NOOP);
    }

    //TODO world.addEntity returns successfully here but nothing ever appears in the world
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();


        if (!AbstractRailBlock.isRail(world.getBlockState(pos))) {
            return ActionResultType.PASS;
        }

        ItemStack stack = player.getHeldItem(context.getHand());

        if (!context.getWorld().isRemote) {
            MinecartEntityBeeHousingBase minecart;

            if (type == Type.BEE_HOUSE) {
                minecart = new MinecartEntityBeehouse(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            } else {
                minecart = new MinecartEntityApiary(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            }

            minecart.getOwnerHandler().setOwner(player.getGameProfile());

            if (stack.hasDisplayName()) {
                minecart.setCustomName(stack.getDisplayName());
            }

            if (!world.addEntity(minecart)) {
                return ActionResultType.FAIL;
            }
        }

        stack.shrink(1);
        return ActionResultType.SUCCESS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return "cart." + type.getString();
    }
}
