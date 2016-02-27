package forestry.core.genetics.research;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosomeType;
import forestry.core.utils.StringUtil;

public class ResearchNoteSpecies<C extends IChromosomeType> extends ResearchNote {
	public static final String SPECIES_NBT_KEY = "spe";
	@Nonnull
	private final IAlleleSpecies<C> species;

	public ResearchNoteSpecies(@Nonnull GameProfile researcher, @Nonnull IAlleleSpecies<C> species) {
		super(EnumNoteType.SPECIES, researcher);
		this.species = species;
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString(SPECIES_NBT_KEY, species.getUID());
	}

	@Override
	public void addTooltip(@Nonnull List<String> list) {
		list.add("researchNote.discovered.0");
		list.add(StringUtil.localizeAndFormat("researchNote.discovered.1", species.getName(), species.getBinomial()));
	}

	@Override
	public boolean registerResults(@Nonnull World world, @Nonnull EntityPlayer player) {
		//TODO: implement or remove this type of research
		return false;
	}
}
