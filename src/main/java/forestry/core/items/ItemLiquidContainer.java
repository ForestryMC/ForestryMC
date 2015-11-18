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

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.CreativeTabForestry;
import forestry.core.fluids.BlockForestryFluid;
import forestry.core.fluids.FluidHelper;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

public class ItemLiquidContainer extends ItemForestry {
	private static final Map<Block, ItemLiquidContainer> buckets = new HashMap<>();

	private boolean isDrink = false;
	private boolean isAlwaysEdible = false;

	private int healAmount = 0;
	private float saturationModifier = 0.0f;

	private final EnumContainerType type;
	private final Block contents;
	private final Color color;

	public ItemLiquidContainer(EnumContainerType type, Block contents, Color color) {
		super(CreativeTabForestry.tabForestry);
		this.type = type;
		this.contents = contents;
		this.color = color;
		if (type == EnumContainerType.BUCKET) {
			setContainerItem(Items.bucket);
			this.maxStackSize = 1;
			buckets.put(contents, this);
		}
	}

	public static ItemLiquidContainer getExistingBucket(Block contents) {
		return buckets.get(contents);
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
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!isDrink) {
			return itemstack;
		}

		itemstack.stackSize--;
		entityplayer.getFoodStats().addStats(healAmount, saturationModifier);
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		/*
		 * if (!world.isRemote && potionId > 0 && world.rand.nextFloat() < potionEffectProbability) entityplayer.addPotionEffect(new PotionEffect(potionId,
		 * potionDuration * 20, potionAmplifier));
		 */

		return itemstack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		if (isDrink) {
			return 32;
		} else {
			return super.getMaxItemUseDuration(itemstack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		if (isDrink) {
			return EnumAction.drink;
		} else {
			return EnumAction.none;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		if (world.isRemote) {
			return itemstack;
		}

		// / DRINKS can be drunk
		if (isDrink) {
			if (entityplayer.canEat(isAlwaysEdible)) {
				entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
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
				fluid = ((IFluidBlock) targetedBlock).drain(world, x, y, z, false);
			} else {
				if (targetedBlock == Blocks.water || targetedBlock == Blocks.flowing_water) {
					fluid = new FluidStack(FluidRegistry.WATER, 1000);
				} else if (targetedBlock == Blocks.lava || targetedBlock == Blocks.flowing_lava) {
					fluid = new FluidStack(FluidRegistry.LAVA, 1000);
				}
			}

			if (fluid == null || fluid.amount <= 0 && this.type == EnumContainerType.BUCKET) {
				return tryPlaceLiquid(itemstack, world, entityplayer, movingobjectposition);
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
				((IFluidBlock) targetedBlock).drain(world, x, y, z, true);
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

	private ItemStack tryPlaceLiquid(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition movingobjectposition) {

		if (this.type != EnumContainerType.BUCKET) {
			return itemstack;
		}

		if (this.contents == Blocks.air) {
			return new ItemStack(Items.bucket);
		}

		int x = movingobjectposition.blockX;
		int y = movingobjectposition.blockY;
		int z = movingobjectposition.blockZ;

		switch (movingobjectposition.sideHit) {
			case 0:
				--y;
				break;
			case 1:
				++y;
				break;
			case 2:
				--z;
				break;
			case 3:
				++z;
				break;
			case 4:
				--x;
				break;
			case 5:
				++x;
				break;
		}

		if (!player.canPlayerEdit(x, y, z, movingobjectposition.sideHit, itemstack)) {
			return itemstack;
		}

		if (this.tryPlaceLiquidAtPosition(world, x, y, z) && !player.capabilities.isCreativeMode) {
			return new ItemStack(Items.bucket);
		}

		return itemstack;
	}

	private boolean tryPlaceLiquidAtPosition(World world, int x, int y, int z) {
		if (this.contents == Blocks.air) {
			return false;
		} else {
			Material material = world.getBlock(x, y, z).getMaterial();
			boolean isLiquid = !material.isSolid();

			if (!world.isAirBlock(x, y, z) && !isLiquid) {
				return false;
			} else {
				// Can't put down liquids in the nether.
				// Explode if it's flammable, evaporate otherwise.
				if (world.provider.isHellWorld && this.contents != Blocks.flowing_lava) {
					int flammability = contents.getFlammability(world, x, y, z, ForgeDirection.UNKNOWN);
					if (contents instanceof BlockForestryFluid && flammability > 0) {
						// Explosion size is determined by flammability, up to size 4.
						float explosionSize = 4F * flammability / 300F;
						world.newExplosion(null, x, y, z, explosionSize, true, true);
						return true;
					} else {
						Random random = world.rand;
						world.playSoundEffect(x + 0.5, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);

						for (int l = 0; l < 8; ++l) {
							world.spawnParticle("largesmoke", x + random.nextDouble(), y + random.nextDouble(), z + random.nextDouble(), 0.0, 0.0, 0.0);
						}
					}
				} else {
					if (!world.isRemote && isLiquid && !material.isLiquid()) {
						world.func_147480_a(x, y, z, true);
					}

					world.setBlock(x, y, z, this.contents, 0, 3);
				}

				return true;
			}
		}
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
		icons[0] = TextureManager.registerTex(register, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH) + ".bottle");
		icons[1] = TextureManager.registerTex(register, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH) + ".contents");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0 && color != null) {
			return icons[1];
		} else {
			return icons[0];
		}
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		if (j > 0 && color != null) {
			return color.getRGB() & 0xffffff; // remove alpha
		} else {
			return 0xffffff;
		}
	}

	public EnumContainerType getType() {
		return type;
	}
}
