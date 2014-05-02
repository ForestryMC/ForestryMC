/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.Position;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TransportConstants;
import buildcraft.transport.TravelingItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.IAllele;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginApiculture;

public class PipeItemsPropolis extends Pipe<PipeTransportItems> implements IPipeTransportItemsHook {

	public final PipeLogicPropolis pipeLogic;

	public PipeItemsPropolis(int itemID) {
		super(new PipeTransportItems(), itemID);
		pipeLogic = new PipeLogicPropolis(this);
		transport.allowBouncing = true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		pipeLogic.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		pipeLogic.writeToNBT(nbt);
	}
	IIconProvider provider;

	@SideOnly(Side.CLIENT)
	@Override
	public IIconProvider getIconProvider() {
		if (provider == null)
			provider = new PipeIconProvider();

		return provider;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return 0;

		return direction.ordinal() + 1;
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if (!Proxies.common.isSimulating(getWorld()))
			return true;

		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID < Block.blocksList.length)
			if (Block.blocksList[player.getCurrentEquippedItem().itemID] instanceof BlockGenericPipe)
				return false;

		player.openGui(ForestryAPI.instance, GuiId.PropolisPipeGUI.ordinal(), player.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(LinkedList<ForgeDirection> possibleOrientations, Position pos, TravelingItem item) {

		LinkedList<ForgeDirection> filteredOrientations = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> typedOrientations = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> defaultOrientations = new LinkedList<ForgeDirection>();

		// We need a bee!
		EnumFilterType type = EnumFilterType.getType(item.getItemStack());
		IBee bee = null;

		if (type != EnumFilterType.ITEM)
			bee = PluginApiculture.beeInterface.getMember(item.getItemStack());

		// Filtered outputs
		for (ForgeDirection dir : possibleOrientations) {

			// Continue if this direction is closed.
			if (pipeLogic.isClosed(dir))
				continue;

			if (pipeLogic.isIndiscriminate(dir)) {
				defaultOrientations.add(dir);
				continue;
			}

			// We need to match the type for this orientation's filter
			if (!pipeLogic.matchType(dir, type, bee))
				continue;

			// Passing the type filter is enough for non-bee items.
			if (type == EnumFilterType.ITEM) {
				filteredOrientations.add(dir);
				continue;
			}

			ArrayList<IAllele[]> filters = pipeLogic.getGenomeFilters(dir);
			// If we have no genome filters, this is only a typed route.
			if (filters.size() <= 0) {
				typedOrientations.add(dir);
				continue;
			}

			// Bees need to match one of the genome filters
			for (IAllele[] pattern : filters) {
				if (pipeLogic.matchAllele(pattern[0], bee.getIdent()) && pipeLogic.matchAllele(pattern[1], bee.getGenome().getSecondary().getUID()))
					filteredOrientations.add(dir);
			}
		}

		if (filteredOrientations.size() > 0)
			return filteredOrientations;
		else if (typedOrientations.size() > 0)
			return typedOrientations;
		else
			return defaultOrientations;

	}

	@Override
	public void entityEntered(TravelingItem item, ForgeDirection orientation) {
		// A bit of speed to perhaps prevent bees from popping out of the pipe.
		try {
			item.setSpeed(TransportConstants.PIPE_NORMAL_SPEED * 20F);
		} catch (Throwable error) {
		}
	}

	@Override
	public void readjustSpeed(TravelingItem item) {
		transport.defaultReajustSpeed(item);
	}
}
