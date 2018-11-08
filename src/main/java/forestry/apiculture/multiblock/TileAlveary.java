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
package forestry.apiculture.multiblock;

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.climate.ClimateCapabilities;
import forestry.api.climate.IClimateListener;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.apiculture.gui.GuiAlveary;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.ITitled;

public abstract class TileAlveary extends MultiblockTileEntityForestry<MultiblockLogicAlveary> implements IBeeHousing, IAlvearyComponent, IOwnedTile, IStreamableGui, ITitled, IClimatised {
	private final String unlocalizedTitle;

	protected TileAlveary() {
		this(BlockAlvearyType.PLAIN);
	}

	protected TileAlveary(BlockAlvearyType type) {
		super(new MultiblockLogicAlveary());
		this.unlocalizedTitle = "tile.for.alveary." + type + ".name";
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		// Re-render this block on the client
		if (world.isRemote) {
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		world.notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onMachineBroken() {
		// Re-render this block on the client
		if (world.isRemote) {
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		world.notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
		markDirty();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
			capability == ClimateCapabilities.CLIMATE_LISTENER ||
			super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return super.getCapability(capability, facing);
		}

		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing != null) {
				SidedInvWrapper sidedInvWrapper = new SidedInvWrapper(getInternalInventory(), facing);
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(sidedInvWrapper);
			} else {
				InvWrapper invWrapper = new InvWrapper(getInternalInventory());
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(invWrapper);
			}
		}
		if (capability == ClimateCapabilities.CLIMATE_LISTENER) {
			IClimateListener listener = getMultiblockLogic().getController().getClimateListener();
			return ClimateCapabilities.CLIMATE_LISTENER.cast(listener);
		}
		return null;
	}

	/* IHousing */
	@Override
	public Biome getBiome() {
		return getMultiblockLogic().getController().getBiome();
	}

	/* IBeeHousing */
	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return getMultiblockLogic().getController().getBeeModifiers();
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return getMultiblockLogic().getController().getBeeListeners();
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return getMultiblockLogic().getController().getBeeInventory();
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return getMultiblockLogic().getController().getBeekeepingLogic();
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		return getMultiblockLogic().getController().getBeeFXCoordinates();
	}

	/* IClimatised */
	@Override
	public EnumTemperature getTemperature() {
		return getMultiblockLogic().getController().getTemperature();
	}

	@Override
	public EnumHumidity getHumidity() {
		return getMultiblockLogic().getController().getHumidity();
	}

	@Override
	public int getBlockLightValue() {
		return getMultiblockLogic().getController().getBlockLightValue();
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return getMultiblockLogic().getController().canBlockSeeTheSky();
	}

	@Override
	public boolean isRaining() {
		return getMultiblockLogic().getController().isRaining();
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return getMultiblockLogic().getController().getErrorLogic();
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return getMultiblockLogic().getController().getOwnerHandler();
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}

	@Override
	public String getUnlocalizedTitle() {
		return unlocalizedTitle;
	}

	/* IClimatised */
	@Override
	public float getExactTemperature() {
		return getMultiblockLogic().getController().getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return getMultiblockLogic().getController().getExactHumidity();
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(PacketBufferForestry data) {
		getMultiblockLogic().getController().writeGuiData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readGuiData(PacketBufferForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiAlveary(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerAlveary(player.inventory, this);
	}
}
