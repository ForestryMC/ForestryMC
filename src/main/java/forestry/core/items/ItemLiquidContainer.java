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
package forestry.core.items;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Translator;

public class ItemLiquidContainer extends ItemForestry {
	private final EnumContainerType type;
	private final int color;

	public ItemLiquidContainer(EnumContainerType type, int color) {
		super(CreativeTabForestry.tabForestry);
		this.type = type;
		this.color = color;
	}

	private static int getMatchingSlot(EntityPlayer player, ItemStack stack) {

		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			ItemStack slotStack = player.inventory.getStackInSlot(slot);

			if (slotStack == null) {
				return slot;
			}

			if (!slotStack.isItemEqual(stack)) {
				continue;
			}

			int space = slotStack.getMaxStackSize() - slotStack.stackSize;
			if (space >= stack.stackSize) {
				return slot;
			}
		}

		return -1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (world.isRemote) {
			return itemstack;
		}

		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, entityplayer, true);
		if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {

			BlockPos pos = movingobjectposition.getBlockPos();
			Block targetedBlock = world.getBlockState(pos).getBlock();

			FluidStack fluid = null;

			if (targetedBlock instanceof IFluidBlock) {
				fluid = ((IFluidBlock) targetedBlock).drain(world, pos, false);
			} else {
				if (targetedBlock == Blocks.water || targetedBlock == Blocks.flowing_water) {
					fluid = new FluidStack(FluidRegistry.WATER, 1000);
				} else if (targetedBlock == Blocks.lava || targetedBlock == Blocks.flowing_lava) {
					fluid = new FluidStack(FluidRegistry.LAVA, 1000);
				}
			}

			ItemStack filledContainer = FluidHelper.getFilledContainer(fluid.getFluid(), itemstack);
			if (filledContainer == null) {
				return itemstack;
			}

			// Search for a slot to stow a filled container in player's
			// inventory
			int slot = getMatchingSlot(entityplayer, filledContainer);
			if (slot < 0) {
				return itemstack;
			}

			if (entityplayer.inventory.getStackInSlot(slot) == null) {
				entityplayer.inventory.setInventorySlotContents(slot, filledContainer.copy());
			} else {
				entityplayer.inventory.getStackInSlot(slot).stackSize++;
			}

			// Remove consumed liquid block in world
			if (targetedBlock instanceof IFluidBlock) {
				((IFluidBlock) targetedBlock).drain(world, pos, true);
			} else {
				world.setBlockToAir(pos);
			}

			// Remove consumed empty container
			itemstack.stackSize--;

			// Notify player that his inventory has changed.
			Proxies.net.inventoryChangeNotify(entityplayer);

			return itemstack;
		}

		return itemstack;

	}

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if(color == 0){
			manager.registerItemModel(item, 0, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH)+ "_empty");
		}else{
			manager.registerItemModel(item, 0, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH));
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		if (j > 0) {
			return color;
		} else {
			return 0xffffff;
		}
	}

	public EnumContainerType getType() {
		return type;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemLiquidContainer) {
			FluidStack fluid = FluidHelper.getFluidStackInContainer(stack);
			if (fluid != null) {
				String exactTranslationKey = "item.for." + type.getName() + '.' + fluid.getFluid().getName() + ".name";
				if (Translator.canTranslateToLocal(exactTranslationKey)) {
					return Translator.translateToLocal(exactTranslationKey);
				} else {
					String grammarKey = "item.for." + type.getName() + ".grammar";
					String grammar = Translator.translateToLocal(grammarKey);
					return Translator.translateToLocalFormatted(grammar, fluid.getLocalizedName());
				}
			}
		}
		return super.getItemStackDisplayName(stack);
	}
}
