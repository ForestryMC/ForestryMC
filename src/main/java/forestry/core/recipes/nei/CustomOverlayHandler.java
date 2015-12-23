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
package forestry.core.recipes.nei;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.core.proxy.Proxies;
import forestry.factory.network.packets.PacketWorktableNEISelect;

import codechicken.nei.LayoutManager;
import codechicken.nei.OffsetPositioner;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.IRecipeHandler;

/**
 * @author bdew
 */
public class CustomOverlayHandler implements IOverlayHandler {
	private final int xOffs;
	private final int yOffs;
	private final boolean forceShift;

	public CustomOverlayHandler(int xOffs, int yOffs, boolean forceShift) {
		this.xOffs = xOffs;
		this.yOffs = yOffs;
		this.forceShift = forceShift;
	}

	@Override
	public void overlayRecipe(GuiContainer cont, IRecipeHandler recipe, int recipeIndex, boolean shift) {
		List<PositionedStack> ingr = recipe.getIngredientStacks(recipeIndex);

		if (shift || forceShift) {
			NBTTagList stacksnbt = new NBTTagList();

			for (PositionedStack pstack : ingr) {
				if (pstack != null) {
					// This is back-asswards but i don't see a better way :(
					int x = (pstack.relx - 25) / 18;
					int y = (pstack.rely - 6) / 18;

					ItemStack stack = pstack.item;
					NBTTagCompound stacknbt = stack.writeToNBT(new NBTTagCompound());
					stacknbt.setInteger("slot", y * 3 + x);
					stacksnbt.appendTag(stacknbt);
				}

				ItemStack stack = recipe.getResultStack(recipeIndex).items[0];
				NBTTagCompound stacknbt = stack.writeToNBT(new NBTTagCompound());
				stacknbt.setInteger("slot", 9);
				stacksnbt.appendTag(stacknbt);
			}

			NBTTagCompound data = new NBTTagCompound();
			data.setTag("stacks", stacksnbt);

			Proxies.net.sendToServer(new PacketWorktableNEISelect(data));
		} else {
			IStackPositioner positioner = new OffsetPositioner(xOffs, yOffs);
			LayoutManager.overlayRenderer = new DefaultOverlayRenderer(ingr, positioner);
		}
	}
}
