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
package forestry.greenhouse.multiblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import forestry.api.greenhouse.IInternalBlock;
import forestry.api.greenhouse.IInternalBlockFace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class InternalBlock implements IInternalBlock {
	private final BlockPos pos;
	private final World world;
	private final List<IInternalBlockFace> faces = new ArrayList();
	private final IInternalBlock root;
	
	public InternalBlock(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
		this.root = null;
		
		for(EnumFacing facing : EnumFacing.VALUES){
			this.faces.add(new InternalBlockFace(facing, getPos().offset(facing), false));
		}
	}
	
	public InternalBlock(World world, BlockPos pos, EnumFacing rootFace, IInternalBlock root) {
		this.world = world;
		this.pos = pos;
		this.root = root;
		
		for(EnumFacing face : EnumFacing.VALUES){
			this.faces.add(new InternalBlockFace(face, getPos().offset(face), face == rootFace));
		}
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public BlockPos getPos() {
		return pos;
	}
	
	@Override
	public Collection<IInternalBlockFace> getFaces() {
		return faces;
	}
	
	@Override
	public IInternalBlock getRoot() {
		return root;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof IInternalBlock)){
			return false;
		}
		IInternalBlock internalBlock = (IInternalBlock) obj;
		return internalBlock.getPos().equals(getPos());
	}
}
