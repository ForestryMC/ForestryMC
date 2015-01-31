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
package forestry.farming.logic;

import java.util.Collection;
import java.util.Stack;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.Utils;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public class FarmLogicEnder extends FarmLogicHomogeneous {

    public FarmLogicEnder(IFarmHousing housing) {
        super(housing,
                new ItemStack[]{new ItemStack(Blocks.end_stone)},
                new ItemStack(Blocks.end_stone),
                Farmables.farmables.get("farmEnder").toArray(new IFarmable[0]));

    }

    @Override
    public String getName() {
        return "Managed Ender Farm";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return Items.ender_eye.getIconFromDamage(0);
    }

    @Override
    public int getFertilizerConsumption() {
        return 20;
    }

    @Override
    public int getWaterConsumption(float hydrationModifier) {
        return 0;
    }

    @Override
    public Collection<ItemStack> collect() {
        return null;
    }

    @Override
    public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
        World world = getWorld();

        Stack<ICrop> crops = new Stack<ICrop>();
        for (int i = 0; i < extent; i++) {
            Vect position = translateWithOffset(x, y + 1, z, direction, i);
            for (IFarmable farmable : germlings) {
                ICrop crop = farmable.getCropAt(world, position.x, position.y, position.z);
                if (crop != null) {
                    crops.push(crop);
                }
            }

        }
        return crops;

    }

    @Override
    protected boolean maintainGermlings(int x, int y, int z, ForgeDirection direction, int extent) {
        World world = getWorld();

        for (int i = 0; i < extent; i++) {
            Vect position = translateWithOffset(x, y, z, direction, i);
            if (!VectUtil.isAirBlock(world, position) && !Utils.isReplaceableBlock(world, position.x, position.y, position.z)) {
                continue;
            }

            ItemStack below = VectUtil.getAsItemStack(world, position.add(0, -1, 0));
            if (!isAcceptedGround(below)) {
                continue;
            }

            return trySetCrop(position);
        }

        return false;
    }

    private boolean trySetCrop(Vect position) {
        World world = getWorld();

        for (IFarmable candidate : germlings) {
            if (housing.plantGermling(candidate, world, position.x, position.y, position.z)) {
                return true;
            }
        }

        return false;
    }

}
