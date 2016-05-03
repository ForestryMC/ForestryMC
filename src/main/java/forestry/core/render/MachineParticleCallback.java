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

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IStringSerializable;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.proxy.Proxies;

public class MachineParticleCallback<P extends Enum<P> & IBlockType & IStringSerializable> extends ParticleHelper.DefaultCallback<BlockBase> {
	
	private final PropertyEnum<P> TYPE;
	
	public MachineParticleCallback(BlockBase block, PropertyEnum<P> TYPE) {
		super(block);
		this.TYPE = TYPE;
	}
	
	@Override
	protected void setTexture(EntityDiggingFX fx, BlockPos pos, IBlockState state) {
		P protety = state.getValue(TYPE);
		if(protety.getMachineProperties() instanceof IMachinePropertiesTesr){
			fx.setParticleIcon(Proxies.common.getClientInstance().getTextureMapBlocks().getAtlasSprite(((IMachinePropertiesTesr)protety.getMachineProperties()).getParticleTextureLocation()));
		}
	}

}
