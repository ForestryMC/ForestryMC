/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.render;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.utils.ResourceUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MachineParticleCallback<P extends Enum<P> & IBlockType & IStringSerializable> extends ParticleHelper.DefaultCallback<BlockBase> {

    private final P blockType;

    public MachineParticleCallback(BlockBase block, P blockType) {
        super(block);
        this.blockType = blockType;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void setTexture(DiggingParticle fx, World world, BlockPos pos, BlockState state) {
        IMachineProperties<?> machineProperties = blockType.getMachineProperties();
        if (machineProperties instanceof IMachinePropertiesTesr) {
            IMachinePropertiesTesr machinePropertiesTesr = (IMachinePropertiesTesr) machineProperties;
            fx.sprite = ResourceUtil.getBlockSprite(machinePropertiesTesr.getParticleTexture());
        }
    }

}
