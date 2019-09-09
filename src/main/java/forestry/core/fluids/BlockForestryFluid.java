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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.entities.ParticleColoredDripParticle;

public class BlockForestryFluid extends FlowingFluidBlock implements IItemModelRegister {

	private final boolean flammable;
	private final int flammability;
	private final Color color;

	public BlockForestryFluid(ForestryFluids forestryFluid) {
		this(forestryFluid, 0, false);
	}

	public BlockForestryFluid(ForestryFluids forestryFluid, int flammability, boolean flammable) {
		this((FlowingFluid) forestryFluid.getFluid(), flammability, flammable, forestryFluid.getParticleColor());
	}

	public BlockForestryFluid(FlowingFluid fluid, int flammability, boolean flammable, Color color) {
		super(fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());

		this.flammability = flammability;
		this.flammable = flammable;

		this.color = color;
	}

	@Override
	public void randomTick(BlockState blockState, World world, BlockPos pos, Random rand) {
		double d0 = pos.getX();
		double d1 = pos.getY();
		double d2 = pos.getZ();

		if (this.material == Material.WATER) {
			int i = blockState.get(LEVEL);

			if (i > 0 && i < 8) {
				if (fluid.getAttributes().getViscosity(world, pos) < 5000 && rand.nextInt(64) == 0) {
					world.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() + 0.5F, false);
				}
			} else if (rand.nextInt(10) == 0) {
				world.addParticle(ParticleTypes.UNDERWATER, d0 + rand.nextFloat(), d1 + rand.nextFloat(), d2 + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			}
		}

		if (this.material == Material.LAVA && world.getBlockState(pos.up()).getMaterial() == Material.AIR && !world.getBlockState(pos.up()).isOpaqueCube(world, pos.up())) {
			if (rand.nextInt(100) == 0) {
				double d8 = d0 + rand.nextFloat();
				double d4 = d1 + 1;
				double d6 = d2 + rand.nextFloat();
				world.addParticle(ParticleTypes.LAVA, d8, d4, d6, 0.0D, 0.0D, 0.0D);
				world.playSound(d8, d4, d6, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}

			if (rand.nextInt(200) == 0) {
				world.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
			}
		}

		if (rand.nextInt(10) == 0 && Block.func_220055_a(world, pos.down(), Direction.DOWN)) {
			Material material = world.getBlockState(pos.down(2)).getMaterial();

			if (!material.blocksMovement() && !material.isLiquid()) {
				double px = d0 + rand.nextFloat();
				double py = d1 - 1.05D;
				double pz = d2 + rand.nextFloat();

				Particle fx = new ParticleColoredDripParticle(world, px, py, pz, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
				Minecraft.getInstance().particles.addEffect(fx);
			}
		}
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return flammable ? 30 : 0;
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return flammability;
	}

	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return flammable;
	}

	private static boolean isFlammable(IBlockReader world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		return blockState.isFlammable(world, pos, Direction.UP);
	}

	@Override
	public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return flammable && flammability == 0;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public Material getMaterial(BlockState state) {
		// Fahrenheit 451 = 505.928 Kelvin
		// The temperature at which book-paper catches fire, and burns.
		if (fluid.getAttributes().getTemperature() > 505) {
			return Material.LAVA;
		} else {
			return super.getMaterial(state);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {

	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random rand) {
		super.tick(state, world, pos, rand);

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
				BlockState blockState = world.getBlockState(new BlockPos(x, y, z));
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
		int flammability = getFlammability(state, world, pos, null);
		if (flammability > 0) {
			// Explosion size is determined by flammability, up to size 4.
			float explosionSize = 4F * flammability / 300F;
			if (explosionSize > 1.0 && isNearFire(world, pos.getX(), pos.getY(), pos.getZ())) {
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), explosionSize, true, Explosion.Mode.DESTROY);
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
