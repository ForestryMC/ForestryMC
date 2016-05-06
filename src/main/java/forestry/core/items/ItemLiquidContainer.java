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
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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

public class ItemLiquidContainer extends ItemForestry implements IItemColor {
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (worldIn.isRemote) {
			return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
		}

		RayTraceResult rayTraceResult = this.rayTrace(worldIn, playerIn, true);
		if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
		}

		BlockPos pos = rayTraceResult.getBlockPos();
		Block targetedBlock = worldIn.getBlockState(pos).getBlock();

		FluidStack fluid = null;

		if (targetedBlock instanceof IFluidBlock) {
			fluid = ((IFluidBlock) targetedBlock).drain(worldIn, pos, false);
		} else {
			if (targetedBlock == Blocks.WATER || targetedBlock == Blocks.FLOWING_WATER) {
				fluid = new FluidStack(FluidRegistry.WATER, 1000);
			} else if (targetedBlock == Blocks.LAVA || targetedBlock == Blocks.FLOWING_LAVA) {
				fluid = new FluidStack(FluidRegistry.LAVA, 1000);
			}
		}

		ItemStack filledContainer = FluidHelper.getFilledContainer(fluid.getFluid(), itemStackIn);
		if (filledContainer == null) {
			return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
		}

		// Search for a slot to stow a filled container in player's
		// inventory
		int slot = getMatchingSlot(playerIn, filledContainer);
		if (slot < 0) {
			return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
		}

		if (playerIn.inventory.getStackInSlot(slot) == null) {
			playerIn.inventory.setInventorySlotContents(slot, filledContainer.copy());
		} else {
			playerIn.inventory.getStackInSlot(slot).stackSize++;
		}

		// Remove consumed liquid block in world
		if (targetedBlock instanceof IFluidBlock) {
			((IFluidBlock) targetedBlock).drain(worldIn, pos, true);
		} else {
			worldIn.setBlockToAir(pos);
		}

		// Remove consumed empty container
		itemStackIn.stackSize--;

		// Notify player that his inventory has changed.
		Proxies.net.inventoryChangeNotify(playerIn);

		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
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
	public int getColorFromItemstack(ItemStack itemstack, int j) {
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
					return Translator.translateToLocalFormatted(grammarKey, fluid.getLocalizedName());
				}
			}
		}
		return super.getItemStackDisplayName(stack);
	}
}
