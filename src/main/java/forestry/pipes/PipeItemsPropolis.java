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
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpriteProvider;
import forestry.api.genetics.IAllele;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;

public class PipeItemsPropolis extends Pipe<PipeTransportItems> {

	public final PipeLogicPropolis pipeLogic;

	public PipeItemsPropolis(Item item) {
		super(new PipeTransportItems(), item);
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

	@Override
	public int getIconIndex(EnumFacing direction) {
		if (direction == null) {
			return 0;
		}

		return direction.ordinal() + 1;
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if (!Proxies.common.isSimulating(getWorld())) {
			return true;
		}

		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() != null) {
			if (Block.getBlockFromItem(player.getCurrentEquippedItem().getItem()) instanceof BlockGenericPipe) {
				return false;
			}
		}

		player.openGui(ForestryAPI.instance, GuiId.PropolisPipeGUI.ordinal(), player.worldObj, container.getPos().getX(), container.getPos().getY(), container.getPos().getZ());

		return true;
	}

	public void eventHandler(PipeEventItem.FindDest event) {
		LinkedList<EnumFacing> filteredOrientations = new LinkedList<EnumFacing>();
		LinkedList<EnumFacing> typedOrientations = new LinkedList<EnumFacing>();
		LinkedList<EnumFacing> defaultOrientations = new LinkedList<EnumFacing>();

		// We need a bee!
		EnumFilterType type = EnumFilterType.getType(event.item.getItemStack());
		IBee bee = null;

		if (type != EnumFilterType.ITEM) {
			bee = BeeManager.beeRoot.getMember(event.item.getItemStack());
		}

		// Filtered outputs
		for (EnumFacing dir : event.destinations) {

			// Continue if this direction is closed.
			if (pipeLogic.isClosed(dir)) {
				continue;
			}

			if (pipeLogic.isIndiscriminate(dir)) {
				defaultOrientations.add(dir);
				continue;
			}

			// We need to match the type for this orientation's filter
			if (!pipeLogic.matchType(dir, type, bee)) {
				continue;
			}

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
				if (matchAllele(pattern[0], bee.getIdent()) && matchAllele(pattern[1], bee.getGenome().getSecondary().getUID())) {
					filteredOrientations.add(dir);
				}
			}
		}

		event.destinations.clear();

		if (filteredOrientations.size() > 0) {
			event.destinations.addAll(filteredOrientations);
		} else if (typedOrientations.size() > 0) {
			event.destinations.addAll(typedOrientations);
		} else {
			event.destinations.addAll(defaultOrientations);
		}
	}

	private static boolean matchAllele(IAllele filter, String ident) {
		if (filter == null) {
			return true;
		} else {
			return filter.getUID().equals(ident);
		}
	}

	public void eventHandler(PipeEventItem.Entered event) {
		// A bit of speed to perhaps prevent bees from popping out of the pipe.
		try {
			event.item.setSpeed(TransportConstants.PIPE_NORMAL_SPEED * 20F);
		} catch (Throwable ignored) {
		}
	}

	public void eventHandler(PipeEventItem.AdjustSpeed event) {
		transport.defaultReajustSpeed(event.item);
	}
}
