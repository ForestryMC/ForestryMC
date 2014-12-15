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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.CreativeTabForestry;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class ItemLiquidContainer extends Item {

	public static enum EnumContainerType {

		GLASS, JAR, CAN, CAPSULE, REFRACTORY
	}

	private boolean isDrink = false;
	private boolean isAlwaysEdible = false;

	private int healAmount = 0;
	private float saturationModifier = 0.0f;

	private final EnumContainerType type;
	private final int colour;

	public ItemLiquidContainer(EnumContainerType type, int colour) {
		super();
		this.type = type;
		this.colour = colour;
		setCreativeTab(CreativeTabForestry.tabForestry);
		setMaxStackSize(16);
	}

	private int getMatchingSlot(EntityPlayer player, ItemStack stack) {

		for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
			ItemStack slotStack = player.inventory.getStackInSlot(slot);

			if (slotStack == null)
				return slot;

			if (!slotStack.isItemEqual(stack))
				continue;

			int space = slotStack.getMaxStackSize() - slotStack.stackSize;
			if (space >= stack.stackSize)
				return slot;
		}

		return -1;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!isDrink)
			return itemstack;

		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(this.getHealAmount(), this.getSaturationModifier());
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		/*
		 * if (!world.isRemote && potionId > 0 && world.rand.nextFloat() < potionEffectProbability) entityplayer.addPotionEffect(new PotionEffect(potionId,
		 * potionDuration * 20, potionAmplifier));
		 */

		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		if (isDrink)
			return 32;
		else
			return super.getMaxItemUseDuration(itemstack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		if (isDrink)
			return EnumAction.drink;
		else
			return EnumAction.none;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(world))
			return itemstack;

		// / DRINKS can be drunk
		if (isDrink) {
			if (entityplayer.canEat(isAlwaysEdible))
				entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			return itemstack;
		}

		// / Otherwise check empty container
		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, entityplayer, true);
		if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {

			int x = movingobjectposition.blockX;
			int y = movingobjectposition.blockY;
			int z = movingobjectposition.blockZ;
			Block targetedBlock = world.getBlock(x, y, z);

			FluidStack fluid = null;

			if (targetedBlock instanceof IFluidBlock) {
				fluid = ((IFluidBlock)targetedBlock).drain(world, x, y, z, false);
			} else {
				if (targetedBlock == Blocks.water || targetedBlock == Blocks.flowing_water) {
					fluid = new FluidStack(FluidRegistry.WATER, 1000);
				} else if (targetedBlock == Blocks.lava || targetedBlock == Blocks.flowing_lava) {
					fluid = new FluidStack(FluidRegistry.LAVA, 1000);
				}
			}

			if (fluid == null || fluid.amount <= 0)
				return itemstack;

			ItemStack filledContainer = FluidHelper.getFilledContainer(fluid.getFluid(), itemstack);
			if (filledContainer == null)
				return itemstack;

			// Search for a slot to stow a filled container in player's
			// inventory
			int slot = getMatchingSlot(entityplayer, filledContainer);
			if (slot < 0)
				return itemstack;

			if (entityplayer.inventory.getStackInSlot(slot) == null)
				entityplayer.inventory.setInventorySlotContents(slot, filledContainer.copy());
			else
				entityplayer.inventory.getStackInSlot(slot).stackSize++;

			// Remove consumed liquid block in world
			if (targetedBlock instanceof IFluidBlock) {
				((IFluidBlock)targetedBlock).drain(world, x, y, z, true);
			} else {
				world.setBlockToAir(x, y, z);
			}

			// Remove consumed empty container
			itemstack.stackSize--;

			// Notify player that his inventory has changed.
			Proxies.net.inventoryChangeNotify(entityplayer);

			return itemstack;
		}

		return itemstack;

	}

	public int getHealAmount() {
		return healAmount;
	}

	public float getSaturationModifier() {
		return saturationModifier;
	}

	public ItemLiquidContainer setDrink(int healAmount, float saturationModifier) {
		isDrink = true;
		this.healAmount = healAmount;
		this.saturationModifier = saturationModifier;
		return this;
	}

	/*
	 * public ItemLiquidContainer setPotionEffect(int i, int j, int k, float f) { potionId = i; potionDuration = j; potionAmplifier = k; potionEffectProbability
	 * = f; return this; }
	 */
	public ItemLiquidContainer setAlwaysEdible() {
		isAlwaysEdible = true;
		return this;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[2];
		icons[0] = TextureManager.getInstance().registerTex(register, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH) + ".bottle");
		icons[1] = TextureManager.getInstance().registerTex(register, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH) + ".contents");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0 && colour >= 0)
			return icons[1];
		else
			return icons[0];
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		if (j > 0 && colour >= 0)
			return colour;
		else
			return 0xffffff;
	}
}
