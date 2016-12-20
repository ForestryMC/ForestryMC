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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlveary;
import forestry.apiculture.gui.GuiAlveary;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.ITitled;

public abstract class TileAlveary extends MultiblockTileEntityForestry<MultiblockLogicAlveary> implements IBeeHousing, IAlvearyComponent, IOwnedTile, IStreamableGui, ITitled, IClimatised, IHintSource {
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
		if (worldObj.isRemote) {
			this.worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onMachineBroken() {
		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
		markDirty();
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

	@Nonnull
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

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("apiary");
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
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiAlveary(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerAlveary(player.inventory, this);
	}
}
