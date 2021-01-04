package forestry.sorting;

import forestry.api.genetics.filter.IFilterData;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.api.genetics.filter.IFilterRuleType;

import genetics.api.alleles.IAllele;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public final class FakeFilterLogic implements IFilterLogic {
	public static final FakeFilterLogic INSTANCE = new FakeFilterLogic();

	private FakeFilterLogic() {
	}

	@Override
	public void writeGuiData(PacketBuffer data) {

	}

	@Override
	public void readGuiData(PacketBuffer data) {

	}

	@Override
	public Collection<Direction> getValidDirections(ItemStack itemStack, Direction from) {
		return Collections.emptySet();
	}

	@Override
	public boolean isValid(ItemStack itemStack, Direction facing) {
		return false;
	}

	@Override
	public boolean isValid(Direction facing, ItemStack itemStack, IFilterData filterData) {
		return false;
	}

	@Override
	public boolean isValidAllelePair(Direction orientation, String activeUID, String inactiveUID) {
		return false;
	}

	@Override
	public IFilterRuleType getRule(Direction facing) {
		return DefaultFilterRuleType.CLOSED;
	}

	@Override
	public boolean setRule(Direction facing, IFilterRuleType rule) {
		return false;
	}

	@Nullable
	@Override
	public IAllele getGenomeFilter(Direction facing, int index, boolean active) {
		return null;
	}

	@Override
	public boolean setGenomeFilter(Direction facing, int index, boolean active, @Nullable IAllele allele) {
		return false;
	}

	@Override
	public void sendToServer(Direction facing, int index, boolean active, @Nullable IAllele allele) {

	}

	@Override
	public void sendToServer(Direction facing, IFilterRuleType rule) {

	}

	@Override
	public INetworkHandler getNetworkHandler() {
		return (l, s, p) -> {
		};
	}

	@Override
	public void read(CompoundNBT nbt) {

	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		return nbt;
	}
}
