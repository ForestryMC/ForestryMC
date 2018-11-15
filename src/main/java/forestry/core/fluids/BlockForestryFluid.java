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

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.entities.ParticleColoredDripParticle;

public class BlockForestryFluid extends BlockFluidClassic implements IItemModelRegister {

	private final boolean flammable;
	private final int flammability;
	private final Color color;

	public BlockForestryFluid(Fluids forestryFluid) {
		this(forestryFluid, 0, false);
	}

	public BlockForestryFluid(Fluids forestryFluid, int flammability, boolean flammable) {
		this(forestryFluid.getFluid(), flammability, flammable, forestryFluid.getParticleColor());
	}

	private BlockForestryFluid(Fluid fluid, int flammability, boolean flammable, Color color) {
		super(fluid, Material.WATER);

		setDensity(fluid.getDensity());

		this.flammability = flammability;
		this.flammable = flammable;

		this.color = color;
	}

	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid(fluidName);
	}

	/**
	 * Copied from {@link BlockLiquid#randomDisplayTick(IBlockState, World, BlockPos, Random)}
	 * and modified to have colored particles.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double d0 = pos.getX();
		double d1 = pos.getY();
		double d2 = pos.getZ();

		if (this.material == Material.WATER) {
			int i = stateIn.getValue(LEVEL);

			if (i > 0 && i < 8) {
				if (getFluid().getViscosity(worldIn, pos) < 5000 && rand.nextInt(64) == 0) {
					worldIn.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() + 0.5F, false);
				}
			} else if (rand.nextInt(10) == 0) {
				worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + rand.nextFloat(), d1 + rand.nextFloat(), d2 + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			}
		}

		if (this.material == Material.LAVA && worldIn.getBlockState(pos.up()).getMaterial() == Material.AIR && !worldIn.getBlockState(pos.up()).isOpaqueCube()) {
			if (rand.nextInt(100) == 0) {
				double d8 = d0 + rand.nextFloat();
				double d4 = d1 + stateIn.getBoundingBox(worldIn, pos).maxY;
				double d6 = d2 + rand.nextFloat();
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D);
				worldIn.playSound(d8, d4, d6, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}

			if (rand.nextInt(200) == 0) {
				worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}
		}

		if (rand.nextInt(10) == 0 && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.DOWN)) {
			Material material = worldIn.getBlockState(pos.down(2)).getMaterial();

			if (!material.blocksMovement() && !material.isLiquid()) {
				double px = d0 + rand.nextFloat();
				double py = d1 - 1.05D;
				double pz = d2 + rand.nextFloat();

				Particle fx = new ParticleColoredDripParticle(worldIn, px, py, pz, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
				FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return !blockState.getMaterial().isLiquid() &&
			super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return !blockState.getMaterial().isLiquid() &&
			super.displaceIfPossible(world, pos);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return flammable ? 30 : 0;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, @Nullable EnumFacing face) {
		return flammability;
	}

	private static boolean isFlammable(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		return block.isFlammable(world, pos, EnumFacing.UP);
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
	public Material getMaterial(IBlockState state) {
		// Fahrenheit 451 = 505.928 Kelvin
		// The temperature at which book-paper catches fire, and burns.
		if (temperature > 505) {
			return Material.LAVA;
		} else {
			return super.getMaterial(state);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {

	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		// Start fires if the fluid is lava-like
		if (getMaterial(state) == Material.LAVA) {
			int rangeUp = rand.nextInt(3);

			for (int i = 0; i < rangeUp; ++i) {
				x += rand.nextInt(3) - 1;
				++y;
				z += rand.nextInt(3) - 1;
				IBlockState blockState = world.getBlockState(new BlockPos(x, y, z));
				if (blockState.getMaterial() == Material.AIR) {
					if (isNeighborFlammable(world, x, y, z)) {
						world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
						return;
					}
				} else if (blockState.getMaterial().blocksMovement()) {
					return;
				}
			}

			if (rangeUp == 0) {
				int startX = x;
				int startZ = z;

				for (int i = 0; i < 3; ++i) {
					x = startX + rand.nextInt(3) - 1;
					z = startZ + rand.nextInt(3) - 1;

					BlockPos posAbove = new BlockPos(pos.getX(), y + 1, z);
					if (world.isAirBlock(posAbove) && isFlammable(world, new BlockPos(x, y, z))) {
						world.setBlockState(posAbove, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}

		// explode if very flammable and near fire
		int flammability = getFlammability(world, pos, null);
		if (flammability > 0) {
			// Explosion size is determined by flammability, up to size 4.
			float explosionSize = 4F * flammability / 300F;
			if (explosionSize > 1.0 && isNearFire(world, pos.getX(), pos.getY(), pos.getZ())) {
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				world.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), explosionSize, true, true);
			}
		}
	}

	private static boolean isNeighborFlammable(World world, int x, int y, int z) {
		return isFlammable(world, new BlockPos(x - 1, y, z)) ||
			isFlammable(world, new BlockPos(x + 1, y, z)) ||
			isFlammable(world, new BlockPos(x, y, z - 1)) ||
			isFlammable(world, new BlockPos(x, y, z + 1)) ||
			isFlammable(world, new BlockPos(x, y - 1, z)) ||
			isFlammable(world, new BlockPos(x, y + 1, z));
	}

	private static boolean isNearFire(World world, int x, int y, int z) {
		AxisAlignedBB boundingBox = new AxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
		return world.isFlammableWithin(boundingBox);
	}
}
