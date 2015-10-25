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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.core.render.EntityColoredDropParticleFX;

public class BlockForestryFluid extends BlockFluidClassic {

	private final boolean flammable;
	private final int flammability;
	private final Color color;

	public BlockForestryFluid(Fluids forestryFluid) {
		this(forestryFluid, 0, false);
	}

	public BlockForestryFluid(Fluids forestryFluid, int flammability, boolean flammable) {
		this(forestryFluid.getFluid(), flammability, flammable, forestryFluid.getColor());
	}

	private BlockForestryFluid(Fluid fluid, int flammability, boolean flammable, Color color) {
		super(fluid, Material.water);

		setDensity(fluid.getDensity());

		this.flammability = flammability;
		this.flammable = flammable;

		this.color = color;
	}

	@Override
	public boolean canDrain(World world, BlockPos pos) {
		return true;
	}

	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid(fluidName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (rand.nextInt(10) == 0
				&& World.doesBlockHaveSolidTopSurface(world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()))
				&& !world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 2, pos.getZ())).getBlock().getMaterial()
						.blocksMovement()) {
			double px = pos.getX() + rand.nextFloat();
			double py = pos.getY() - 1.05D;
			double pz = pos.getZ() + rand.nextFloat();

			EntityFX fx = new EntityColoredDropParticleFX(world, px, py, pz, color.getRed(), color.getGreen(),
					color.getBlue());
			FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
			return false;
		}
		return super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial().isLiquid()) {
			return false;
		}
		return super.displaceIfPossible(world, pos);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return flammable ? 30 : 0;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return flammability;
	}

	private static boolean isFlammable(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().isFlammable(world, pos, null);
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return flammable;
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return flammable && flammability == 0;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public Material getMaterial() {
		// Fahrenheit 451 = 505.928 Kelvin
		// The temperature at which book-paper catches fire, and burns.
		if (temperature > 505) {
			return Material.lava;
		} else {
			return super.getMaterial();
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		// Start fires if the fluid is lava-like
		if (getMaterial() == Material.lava) {
			int rangeUp = rand.nextInt(3);

			for (int i = 0; i < rangeUp; ++i) {
				int x = pos.getX() + rand.nextInt(3) - 1;
				int y = pos.getY() + 1;
				int z = pos.getZ() + rand.nextInt(3) - 1;
				pos = new BlockPos(x, y, z);
				Block block = world.getBlockState(pos).getBlock();

				if (block.getMaterial() == Material.air) {
					if (isNeighborFlammable(world, pos)) {
						world.setBlockState(pos, Blocks.fire.getDefaultState());
						return;
					}
				} else if (block.getMaterial().blocksMovement()) {
					return;
				}
			}

			if (rangeUp == 0) {
				int startX = pos.getX();
				int startZ = pos.getZ();

				for (int i = 0; i < 3; ++i) {
					int x = startX + rand.nextInt(3) - 1;
					int z = startZ + rand.nextInt(3) - 1;
					pos = new BlockPos(x, pos.getY(), z);

					if (world.isAirBlock(new BlockPos(x, pos.getY() + 1, z)) && isFlammable(world, pos)) {
						world.setBlockState(new BlockPos(x, pos.getY() + 1, z), Blocks.fire.getDefaultState());
					}
				}
			}
		}

		// explode if very flammable and near fire
		int flammability = getFlammability(world, pos, null);
		if (flammability > 0) {
			// Explosion size is determined by flammability, up to size 4.
			float explosionSize = 4F * flammability / 300F;
			if (explosionSize > 1.0 && isNearFire(world, pos)) {
				world.setBlockState(pos, Blocks.fire.getDefaultState());
				world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), explosionSize, true, true);
			}
		}
	}

	private static boolean isNeighborFlammable(World world, BlockPos pos) {
		return isFlammable(world, new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()))
				|| isFlammable(world, new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()))
				|| isFlammable(world, new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1))
				|| isFlammable(world, new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1))
				|| isFlammable(world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()))
				|| isFlammable(world, new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
	}

	private boolean isNearFire(World world, BlockPos pos) {
		AxisAlignedBB boundingBox = AxisAlignedBB.fromBounds(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
				pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		return world.isFlammableWithin(boundingBox);
	}

}
