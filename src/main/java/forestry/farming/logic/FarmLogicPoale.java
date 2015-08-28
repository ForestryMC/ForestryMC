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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.sprite.ISprite;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;

public class FarmLogicPoale extends FarmLogic {

	private final IFarmable[] germlings;

	public FarmLogicPoale(IFarmHousing housing) {
		super(housing);
		Collection<IFarmable> farmables = Farmables.farmables.get("farmPoales");
		germlings = farmables.toArray(new IFarmable[farmables.size()]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ISprite getIcon() {
		return TextureManager.getInstance().getFarmSprite("farmLoigc", "items", "reeds");
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Reed Farm";
		} else {
			return "Managed Reed Farm";
		}
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		if (isManual) {
			return false;
		}

		return StackUtils.equals(Blocks.sand, itemstack) || StackUtils.equals(Blocks.dirt, itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		if (isManual) {
			return false;
		}

		return itemstack.getItem() == Items.reeds;
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(BlockPos pos, FarmDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos, direction, i);
			for (IFarmable seed : germlings) {
				ICrop crop = seed.getCropAt(world, pos);
				if (crop != null) {
					crops.push(crop);
				}
			}
		}
		return crops;
	}

}
