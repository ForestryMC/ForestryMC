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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.IMachinePropertiesTesr;

public class MachineParticleCallback<P extends Enum<P> & IBlockType & IStringSerializable> extends ParticleHelper.DefaultCallback<BlockBase> {

	private final P blockType;

	public MachineParticleCallback(BlockBase block, P blockType) {
		super(block);
		this.blockType = blockType;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void setTexture(ParticleDigging fx, World world, BlockPos pos, IBlockState state) {
		IMachineProperties<?> machineProperties = blockType.getMachineProperties();
		if (machineProperties instanceof IMachinePropertiesTesr) {
			Minecraft minecraft = Minecraft.getMinecraft();
			TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
			IMachinePropertiesTesr machinePropertiesTesr = (IMachinePropertiesTesr) machineProperties;
			String particleTextureLocation = machinePropertiesTesr.getParticleTextureLocation();
			TextureAtlasSprite particleTexture = textureMapBlocks.getAtlasSprite(particleTextureLocation);
			fx.setParticleTexture(particleTexture);
		}
	}

}
