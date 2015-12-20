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
package forestry.core.tiles;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.IMachinePropertiesTESR;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;

public class MachineDefinition {

	public final Class<? extends TileForestry> teClass;

	private final String teIdent;
	private Block block;
	private final int meta;
	private boolean legacy;

	public final TileEntitySpecialRenderer renderer;
	
	private float minX, minY, minZ, maxX, maxY, maxZ;

	public MachineDefinition(IMachineProperties properties) {
		this(properties.getMeta(), properties.getTeIdent(), properties.getTeClass(), null);
	}

	public MachineDefinition(IMachinePropertiesTESR properties) {
		this(properties.getMeta(), properties.getTeIdent(), properties.getTeClass(), properties.getRenderer());
	}

	public MachineDefinition(int meta, String teIdent, Class<? extends TileForestry> teClass, TileEntitySpecialRenderer renderer) {
		this.meta = meta;
		this.teIdent = teIdent;
		this.teClass = teClass;
		this.renderer = renderer;

		this.faceMap = new int[8];
		for (int i = 0; i < 8; i++) {
			faceMap[i] = 0;
		}

		minX = 0;
		minY = 0;
		minZ = 0;
		maxX = 1;
		maxY = 1;
		maxZ = 1;
	}

	public Block getBlock() {
		return block;
	}

	public int getMeta() {
		return meta;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public MachineDefinition setLegacy() {
		legacy = true;
		return this;
	}
	
	public MachineDefinition setBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		return this;
	}

	public void register() {
		registerTileEntity();
		if (renderer != null) {
			Proxies.render.registerTESR(this);
		}
	}
	
	public AxisAlignedBB getBoundingBox(BlockPos pos, IBlockState state) {
		return AxisAlignedBB.fromBounds(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ);
	}

	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 startVec, Vec3 endVec) {
		return BlockUtil.collisionRayTrace(world, pos, startVec, endVec, minX, minY, minZ, maxX, maxY, maxZ);
	}

	/**
	 * Registers the tile entity with MC.
	 */
	private void registerTileEntity() {
		GameRegistry.registerTileEntity(teClass, teIdent);
	}

	public TileEntity createMachine() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName());
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		if (legacy) {
			return;
		}
		list.add(new ItemStack(item, 1, meta));
	}

	/* INTERACTION */
	public boolean isSolidOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		TileForestry tile = TileUtil.getTile(world, pos, teClass);
		if (tile == null) {
			return false;
		}

		return tile.rotate(axis);
	}

	/* TEXTURES */
	private final int[] faceMap;

	public MachineDefinition setFaces(int... faces) {

		if (faces.length > 6) {
			System.arraycopy(faces, 0, faceMap, 0, faces.length);
		} else {
			System.arraycopy(faces, 0, faceMap, 0, 6);
			faceMap[6] = faces[0];
			faceMap[7] = faces[1];
		}

		return this;
	}
}
