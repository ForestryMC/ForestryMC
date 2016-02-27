package forestry.core.genetics.research;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INbtWritable;
import forestry.core.utils.PlayerUtil;

public abstract class ResearchNote implements INbtWritable {
	private static final String TYPE_NBT_KEY = "typ";
	public static final String RESEARCHER_NBT_KEY = "res";
	@Nonnull
	private final EnumNoteType type;
	@Nonnull
	private final GameProfile researcher;

	@Nullable
	public static ResearchNote create(@Nullable NBTTagCompound nbt) {
		if (nbt == null) {
			return null;
		}
		if (nbt.hasKey(TYPE_NBT_KEY)) {
			EnumNoteType type = EnumNoteType.VALUES[nbt.getByte(TYPE_NBT_KEY)];
			return type.createResearchNote(nbt);
		}
		return null;
	}

	public ResearchNote(@Nonnull EnumNoteType type, @Nonnull GameProfile researcher) {
		this.type = type;
		this.researcher = researcher;
	}

	@Nonnull
	public GameProfile getResearcher() {
		return researcher;
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagCompound researcherNbt = new NBTTagCompound();
		PlayerUtil.writeGameProfile(researcherNbt, researcher);
		nbt.setTag(RESEARCHER_NBT_KEY, researcherNbt);
		nbt.setByte(TYPE_NBT_KEY, (byte) type.ordinal());
	}

	public abstract void addTooltip(@Nonnull List<String> list);

	public abstract boolean registerResults(@Nonnull World world, @Nonnull EntityPlayer player);
}
