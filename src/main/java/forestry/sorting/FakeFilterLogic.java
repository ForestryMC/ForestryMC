package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRuleType;

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
	public Collection<EnumFacing> getValidDirections(ItemStack itemStack, EnumFacing from) {
		return Collections.emptySet();
	}

	@Override
	public boolean isValid(ItemStack itemStack, EnumFacing facing) {
		return false;
	}

	@Override
	public boolean isValid(EnumFacing facing, ItemStack itemStack, IFilterData filterData) {
		return false;
	}

	@Override
	public boolean isValidAllelePair(EnumFacing orientation, String activeUID, String inactiveUID) {
		return false;
	}

	@Override
	public IFilterRuleType getRule(EnumFacing facing) {
		return DefaultFilterRuleType.CLOSED;
	}

	@Override
	public boolean setRule(EnumFacing facing, IFilterRuleType rule) {
		return false;
	}

	@Nullable
	@Override
	public IAllele getGenomeFilter(EnumFacing facing, int index, boolean active) {
		return null;
	}

	@Override
	public boolean setGenomeFilter(EnumFacing facing, int index, boolean active, @Nullable IAllele allele) {
		return false;
	}

	@Override
	public void sendToServer(EnumFacing facing, int index, boolean active, @Nullable IAllele allele) {

	}

	@Override
	public INetworkHandler getNetworkHandler() {
		return (l, s, p) -> {
		};
	}

	@Override
	public void sendToServer(EnumFacing facing, IFilterRuleType rule) {

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return nbt;
	}
}
