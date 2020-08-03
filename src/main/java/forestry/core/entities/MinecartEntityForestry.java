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
package forestry.core.entities;

import forestry.core.tiles.ITitled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

//TODO - check nothing missing from MinecartEntity now that this extends AbstractMinecartEntity
public abstract class MinecartEntityForestry extends AbstractMinecartEntity implements ITitled {

    public MinecartEntityForestry(EntityType<? extends MinecartEntityForestry> type, World world) {
        super(type, world);
        setHasDisplayTile(true);
    }

    public MinecartEntityForestry(EntityType<?> type, World world, double posX, double posY, double posZ) {
        super(type, world, posX, posY, posZ);
        setHasDisplayTile(true);
    }

    //	//TODO - check
    //	@Override
    //	public boolean processInitialInteract(PlayerEntity player, Hand hand) {
    //		if(super.processInitialInteract(player, hand)) {
    //			return true;
    //		}
    //		//TODO sides
    //		NetworkHooks.openGui((ServerPlayerEntity) player, );
    //		return true;
    //	}

    /* MinecartEntity */
    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public boolean isPoweredCart() {
        return false;
    }

    // cart contents
    @Override
    public abstract BlockState getDisplayTile();

    // cart itemStack
    @Override
    public abstract ItemStack getCartItem();

    @Override
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);
        if (/*this.world.getGameRules().getBoolean("doEntityDrops")*/ true) {    //TODO - revisit when class is deobsfucated
            Block block = getDisplayTile().getBlock();
            entityDropItem(new ItemStack(block), 0.0F);
        }
    }

    // fix cart contents rendering as black in the End dimension
    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent(getUnlocalizedTitle());
    }

    /* ITitled */
    @Override
    public String getUnlocalizedTitle() {
        ItemStack cartItem = getCartItem();
        return cartItem.getTranslationKey();
    }

    @Override
    public Type getMinecartType() {
        return null;
    }
}
