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
package forestry.core.render;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.utils.ResourceUtil;

public class MachineParticleCallback<P extends Enum<P> & IBlockType & StringRepresentable> extends ParticleHelper.DefaultCallback<BlockBase<?>> {

	private final P blockType;

	public MachineParticleCallback(BlockBase<?> block, P blockType) {
		super(block);
		this.blockType = blockType;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void setTexture(TerrainParticle fx, Level world, BlockPos pos, BlockState state) {
		IMachineProperties<?> properties = blockType.getMachineProperties();
		if (properties instanceof IMachinePropertiesTesr) {
			IMachinePropertiesTesr<?> propertiesTesr = (IMachinePropertiesTesr<?>) properties;
			fx.sprite = ResourceUtil.getBlockSprite(propertiesTesr.getParticleTexture());
		}
	}

}
