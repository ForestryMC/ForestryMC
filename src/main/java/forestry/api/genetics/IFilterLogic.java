package forestry.api.genetics;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.alleles.IAllele;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

public interface IFilterLogic extends INbtWritable, INbtReadable {
	void writeGuiData(PacketBuffer data);

	@OnlyIn(Dist.CLIENT)
	void readGuiData(PacketBuffer data);

	Collection<Direction> getValidDirections(ItemStack itemStack, Direction from);

	boolean isValid(ItemStack itemStack, Direction facing);

	boolean isValid(Direction facing, ItemStack itemStack, IFilterData filterData);

	boolean isValidAllelePair(Direction orientation, String activeUID, String inactiveUID);

	IFilterRuleType getRule(Direction facing);

	boolean setRule(Direction facing, IFilterRuleType rule);

	@Nullable
	IAllele getGenomeFilter(Direction facing, int index, boolean active);

	boolean setGenomeFilter(Direction facing, int index, boolean active, @Nullable IAllele allele);

	void sendToServer(Direction facing, int index, boolean active, @Nullable IAllele allele);

	void sendToServer(Direction facing, IFilterRuleType rule);

	INetworkHandler getNetworkHandler();

	interface INetworkHandler {
		/**
		 * Sends the data of the logic to the client of all players that have the gui currently open.
		 *
		 * @param player The player that changed the filter.
		 */
		void sendToPlayers(IFilterLogic logic, ServerWorld server, PlayerEntity player);
	}
}
