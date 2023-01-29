/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core.fluids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.config.Constants;
import forestry.core.entities.EntityFXColoredDropParticle;
import forestry.core.render.TextureManager;

public class BlockForestryFluid extends BlockFluidClassic {

    private final boolean flammable;
    private final int flammability;
    private final Color color;

    @SideOnly(Side.CLIENT)
    private List<IIcon> icons;

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
    public boolean canDrain(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public Fluid getFluid() {
        return FluidRegistry.getFluid(fluidName);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1 || this.icons.size() == 1) {
            return this.icons.get(0);
        } else {
            return this.icons.get(1);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {

        this.icons = new ArrayList<>(2);

        String still = "liquid/" + fluidName + "_still";
        this.icons.add(TextureManager.registerTex(iconRegister, still));

        if (flowTextureExists()) {
            String flow = "liquid/" + fluidName + "_flow";
            this.icons.add(TextureManager.registerTex(iconRegister, flow));
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean flowTextureExists() {
        try {
            ResourceLocation resourceLocation = new ResourceLocation(
                    Constants.ID,
                    "textures/blocks/liquid/" + fluidName + "_flow.png");
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            return resourceManager.getResource(resourceLocation) != null;
        } catch (java.lang.Exception e) {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)
                && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {
            double px = (double) ((float) x + rand.nextFloat());
            double py = (double) y - 1.05D;
            double pz = (double) ((float) z + rand.nextFloat());

            EntityFX fx = new EntityFXColoredDropParticle(
                    world,
                    px,
                    py,
                    pz,
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue());
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }

    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        return super.canDisplace(world, x, y, z);
    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        return super.displaceIfPossible(world, x, y, z);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return flammable ? 30 : 0;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return flammability;
    }

    private static boolean isFlammable(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z).isFlammable(world, x, y, z, ForgeDirection.UNKNOWN);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return flammable;
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
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
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);

        // Start fires if the fluid is lava-like
        if (getMaterial() == Material.lava) {
            int rangeUp = rand.nextInt(3);

            for (int i = 0; i < rangeUp; ++i) {
                x += rand.nextInt(3) - 1;
                ++y;
                z += rand.nextInt(3) - 1;
                Block block = world.getBlock(x, y, z);

                if (block.getMaterial() == Material.air) {
                    if (isNeighborFlammable(world, x, y, z)) {
                        world.setBlock(x, y, z, Blocks.fire);
                        return;
                    }
                } else if (block.getMaterial().blocksMovement()) {
                    return;
                }
            }

            if (rangeUp == 0) {
                int startX = x;
                int startZ = z;

                for (int i = 0; i < 3; ++i) {
                    x = startX + rand.nextInt(3) - 1;
                    z = startZ + rand.nextInt(3) - 1;

                    if (world.isAirBlock(x, y + 1, z) && isFlammable(world, x, y, z)) {
                        world.setBlock(x, y + 1, z, Blocks.fire);
                    }
                }
            }
        }

        // explode if very flammable and near fire
        int flammability = getFlammability(world, x, y, z, ForgeDirection.UNKNOWN);
        if (flammability > 0) {
            // Explosion size is determined by flammability, up to size 4.
            float explosionSize = 4F * flammability / 300F;
            if (explosionSize > 1.0 && isNearFire(world, x, y, z)) {
                world.setBlock(x, y, z, Blocks.fire);
                world.newExplosion(null, x, y, z, explosionSize, true, true);
            }
        }
    }

    private static boolean isNeighborFlammable(World world, int x, int y, int z) {
        return isFlammable(world, x - 1, y, z) || isFlammable(world, x + 1, y, z)
                || isFlammable(world, x, y, z - 1)
                || isFlammable(world, x, y, z + 1)
                || isFlammable(world, x, y - 1, z)
                || isFlammable(world, x, y + 1, z);
    }

    private static boolean isNearFire(World world, int x, int y, int z) {
        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
        return world.func_147470_e(boundingBox);
    }
}
