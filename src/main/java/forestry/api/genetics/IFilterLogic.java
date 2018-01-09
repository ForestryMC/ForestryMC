package forestry.api.genetics;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

public interface IFilterLogic extends INbtWritable, INbtReadable {
	void writeGuiData(PacketBuffer data);

	@SideOnly(Side.CLIENT)
	void readGuiData(PacketBuffer data) throws IOException;

	Collection<EnumFacing> getValidDirections(ItemStack itemStack, EnumFacing from);

	boolean isValid(ItemStack itemStack, EnumFacing facing);

	boolean isValid(EnumFacing facing, ItemStack itemStack, IFilterData filterData);

	boolean isValidAllelePair(EnumFacing orientation, String activeUID, String inactiveUID);

	IFilterRuleType getRule(EnumFacing facing);

	boolean setRule(EnumFacing facing, IFilterRuleType rule);

	@Nullable
	IAllele getGenomeFilter(EnumFacing facing, int index, boolean active);

	boolean setGenomeFilter(EnumFacing facing, int index, boolean active, @Nullable IAllele allele);

	void sendToServer(EnumFacing facing, int index, boolean active, @Nullable IAllele allele);

	void sendToServer(EnumFacing facing, IFilterRuleType rule);

	INetworkHandler getNetworkHandler();

	interface INetworkHandler{
		/**
		 * Sends the data of the logic to the client of all players that have the gui currently open.
		 *
		 * @param player The player that changed the filter.
		 */
		void sendToPlayers(IFilterLogic logic, WorldServer server, EntityPlayer player);
	}
}
