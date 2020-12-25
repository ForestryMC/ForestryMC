/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.factory.tiles;

import forestry.api.fuels.RainSubstrate;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileMill;
import forestry.factory.features.FactoryTiles;
import forestry.factory.inventory.InventoryRainmaker;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.IServerWorldInfo;

import javax.annotation.Nullable;

public class TileMillRainmaker extends TileMill {
    private int duration;
    private boolean reverse;

    public TileMillRainmaker() {
        super(FactoryTiles.RAINMAKER.tileType());
        speed = 0.01f;
        setInternalInventory(new InventoryRainmaker(this));
    }

    //	@Override	//TODO this needs to be in onactivated or similar
    //	public void openGui(PlayerEntity player, ItemStack heldItem) {
    //		if (!player.world.isRemote && !heldItem.isEmpty()) {
    //			// We don't have a gui, but we can be activated
    //			if (FuelManager.rainSubstrate.containsKey(heldItem) && charge == 0) {
    //				RainSubstrate substrate = FuelManager.rainSubstrate.get(heldItem);
    //				if (substrate.getItem().isItemEqual(heldItem)) {
    //					addCharge(substrate);
    //					heldItem.shrink(1);
    //				}
    //			}
    //			sendNetworkUpdate();
    //		}
    //	}

    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);

        charge = compoundNBT.getInt("Charge");
        progress = compoundNBT.getFloat("Progress");
        stage = compoundNBT.getInt("Stage");
        duration = compoundNBT.getInt("Duration");
        reverse = compoundNBT.getBoolean("Reverse");
    }


    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);

        compoundNBT.putInt("Charge", charge);
        compoundNBT.putFloat("Progress", progress);
        compoundNBT.putInt("Stage", stage);
        compoundNBT.putInt("Duration", duration);
        compoundNBT.putBoolean("Reverse", reverse);
        return compoundNBT;
    }

    public void addCharge(RainSubstrate substrate) {
        charge = 1;
        speed = substrate.getSpeed();
        duration = substrate.getDuration();
        reverse = substrate.isReverse();
        sendNetworkUpdate();
    }

    @Override
    public void activate() {
        if (world.isRemote) {
            world.playSound(
                    null,
                    getPos(),
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    SoundCategory.WEATHER,
                    10000.0F,
                    0.8F + world.rand.nextFloat() * 0.2F
            );

            float f = getPos().getX() + 0.5F;
            float f1 = getPos().getY() + 0.0F + world.rand.nextFloat() * 6F / 16F;
            float f2 = getPos().getZ() + 0.5F;
            float f3 = 0.52F;
            float f4 = world.rand.nextFloat() * 0.6F - 0.3F;

            ParticleRender.addEntityExplodeFX(world, f - f3, f1, f2 + f4);
            ParticleRender.addEntityExplodeFX(world, f + f3, f1, f2 + f4);
            ParticleRender.addEntityExplodeFX(world, f + f4, f1, f2 - f3);
            ParticleRender.addEntityExplodeFX(world, f + f4, f1, f2 + f3);
        } else {
            if (reverse) {
                world.getWorldInfo().setRaining(false);
            } else {
                world.getWorldInfo().setRaining(true);
                ((IServerWorldInfo) world.getWorldInfo()).setRainTime(duration);
            }
            charge = 0;
            duration = 0;
            reverse = false;
            sendNetworkUpdate();
        }
    }

    @Override
    @Nullable
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    @Override
    protected boolean hasGui() {
        return false;
    }
}
