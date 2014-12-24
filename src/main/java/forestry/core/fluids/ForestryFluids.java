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
package forestry.core.fluids;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;

public enum ForestryFluids {

	ETHANOL(Fluids.BIOETHANOL, new Color(255, 111, 0), 790, 1000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 300, true);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketEthanol.getItemStack(),
				ForestryItem.canEthanol.getItemStack(),
				ForestryItem.waxCapsuleEthanol.getItemStack(),
				ForestryItem.refractoryEthanol.getItemStack()
			);
		}
	},
	BIOMASS(Fluids.BIOMASS, new Color(100, 132, 41), 400, 6560) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 100, true);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketBiomass.getItemStack(),
				ForestryItem.canBiomass.getItemStack(),
				ForestryItem.waxCapsuleBiomass.getItemStack(),
				ForestryItem.refractoryBiomass.getItemStack()
			);
		}
	},
	LIQUID_GLASS(Fluids.GLASS, new Color(164, 164, 164), 2400, 10000, 1200) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 0, true);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketGlass.getItemStack()
			);
		}
	},
	HONEY(Fluids.HONEY, new Color(255, 173, 31), 1420, 73600) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketHoney.getItemStack(),
				ForestryItem.canHoney.getItemStack(),
				ForestryItem.waxCapsuleHoney.getItemStack(),
				ForestryItem.refractoryHoney.getItemStack()
			);
		}
	},
	LEGACY_HONEY(Fluids.LEGACY_HONEY, new Color(224, 194, 57), 1420, 73600),
	ICE(Fluids.ICE, new Color(175, 242, 255), 920, 1000, 265) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketIce.getItemStack(),
				ForestryItem.canIce.getItemStack(),
				ForestryItem.waxCapsuleIce.getItemStack(),
				ForestryItem.refractoryIce.getItemStack()
			);
		}
	},
	JUICE(Fluids.JUICE, new Color(168, 201, 114), 1000, 1000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketJuice.getItemStack(),
				ForestryItem.canJuice.getItemStack(),
				ForestryItem.waxCapsuleJuice.getItemStack(),
				ForestryItem.refractoryJuice.getItemStack()
			);
		}
	},
	MILK(Fluids.MILK, new Color(255, 255, 255), 1030, 3000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				new ItemStack(Items.milk_bucket)
			);
		}
	},
	SEEDOIL(Fluids.SEEDOIL, new Color(255, 255, 168), 885, 5000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 2, true);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketSeedoil.getItemStack(),
				ForestryItem.canSeedOil.getItemStack(),
				ForestryItem.waxCapsuleSeedOil.getItemStack(),
				ForestryItem.refractorySeedOil.getItemStack()
			);
		}
	},
	SHORT_MEAD(Fluids.SHORT_MEAD, new Color(239, 153, 0), 1000, 1200) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
		@Override
		List<ItemStack> getFilledContainers() {
			return Arrays.asList(
				ForestryItem.bucketShortMead.getItemStack()
			);
		}
	};

	public static final ForestryFluids[] VALUES = values();
	public final String tag;
	public final Fluids standardFluid;
	public final int density, viscosity, temperature;
	private final Color color;
	protected Fluid fluid;
	protected Block fluidBlock;

	private ForestryFluids(Fluids standardFluid, Color color) {
		this(standardFluid, color, 1000, 1000, 295);
	}

	private ForestryFluids(Fluids standardFluid, Color color, int density, int viscosity) {
		this(standardFluid, color, density, viscosity, 295);
	}

	private ForestryFluids(Fluids standardFluid, Color color, int density, int viscosity, int temperature) {
		this.tag = "fluid." + standardFluid.getTag();
		this.standardFluid = standardFluid;
		this.color = color;
		this.density = density;
		this.viscosity = viscosity;
		this.temperature = temperature;
	}

	private void initFluid() {
		if (fluid == null && Config.isFluidEnabled(this)) {
			String fluidName = standardFluid.getTag();
			if (!FluidRegistry.isFluidRegistered(fluidName)) {
				fluid = new Fluid(fluidName).setDensity(density).setViscosity(viscosity).setTemperature(temperature);
				FluidRegistry.registerFluid(fluid);
				initBlock();
			}
		}
	}

	List<ItemStack> getFilledContainers() {
		return Collections.emptyList();
	}

	Block makeBlock() {
		return null;
	}

	private void initBlock() {
		if (fluidBlock != null)
			return;

		Fluid fluid = standardFluid.getFluid();
		if (fluid == null)
			return;

		fluidBlock = fluid.getBlock();

		if (Config.isBlockEnabled(tag))
			if (fluidBlock == null) {
				fluidBlock = makeBlock();
				if (fluidBlock != null) {
					fluidBlock.setBlockName("forestry." + tag);
					Proxies.common.registerBlock(fluidBlock, ItemBlock.class);
				}
			} else {
				GameRegistry.UniqueIdentifier blockID = GameRegistry.findUniqueIdentifierFor(fluidBlock);
				Proxies.log.severe("Pre-existing {0} fluid block detected, deferring to {1}:{2}, "
						+ "this may cause issues if the server/client have different mod load orders, "
						+ "recommended that you disable all but one instance of {0} fluid blocks via your configs.", fluid.getName(), blockID.modId, blockID.name);
			}
	}

	public Block getBlock() {
		return fluidBlock;
	}

	public Color getColor() {
		return color;
	}

	public static class MissingFluidException extends RuntimeException {

		public MissingFluidException(String tag) {
			super("Fluid '" + tag + "' was not found. Please check your configs.");
		}

	}

	public static void preInit() {
		for (ForestryFluids fluidType : VALUES) {
			fluidType.initFluid();
		}
	}

	public static void init() {
		for (ForestryFluids fluidType : VALUES) {
			if (fluidType.standardFluid.getFluid() == null)
				continue;

			for (ItemStack filledContainer : fluidType.getFilledContainers()) {
				LiquidHelper.injectLiquidContainer(fluidType.standardFluid, filledContainer);
			}
		}
	}

	public static void postInit() {
		for (ForestryFluids fluidType : VALUES) {
			fluidType.initBlock();
			if (fluidType.standardFluid.getFluid() == null)
				throw new MissingFluidException(fluidType.standardFluid.getTag());
		}
	}

	public static class TextureHook {

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void textureHook(TextureStitchEvent.Post event) {
			if (event.map.getTextureType() == 0) {
				for (ForestryFluids fluidType : VALUES) {
					if (fluidType.fluid != null)
						fluidType.fluid.setIcons(fluidType.fluidBlock.getBlockTextureFromSide(1), fluidType.fluidBlock.getBlockTextureFromSide(2));
				}
			}
		}

	}

	public static Object getTextureHook() {
		return new TextureHook();
	}

	public static class FillBucketHook {
		@SubscribeEvent
		public void fillBucket(FillBucketEvent event) {
			MovingObjectPosition movingObjectPosition = event.target;
			int x = movingObjectPosition.blockX;
			int y = movingObjectPosition.blockY;
			int z = movingObjectPosition.blockZ;
			Block targetedBlock = event.world.getBlock(x, y, z);
			if (targetedBlock instanceof BlockForestryFluid) {
				Item filledBucket = ItemLiquidContainer.getExistingBucket(targetedBlock);
				if (filledBucket != null) {
					event.result = new ItemStack(filledBucket);
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}

	public static Object getFillBucketHook() {
		return new FillBucketHook();
	}
}