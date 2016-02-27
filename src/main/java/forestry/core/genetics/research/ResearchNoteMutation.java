package forestry.core.genetics.research;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.utils.StringUtil;

public class ResearchNoteMutation<C extends IChromosomeType> extends ResearchNote {
	public static final String MUTATION_NBT_KEY = "mut";

	private final IMutation<C> mutation;

	public ResearchNoteMutation(@Nonnull GameProfile researcher, @Nonnull IMutation<C> mutation) {
		super(EnumNoteType.MUTATION, researcher);
		this.mutation = mutation;
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagCompound mutationNbt = new NBTTagCompound();
		mutation.writeToNBT(mutationNbt);
		nbt.setTag(MUTATION_NBT_KEY, mutationNbt);
	}

	@Override
	public boolean registerResults(@Nonnull World world, @Nonnull EntityPlayer player) {
		ISpeciesRoot<C> root = mutation.getRoot();
		IBreedingTracker<C> tracker = root.getBreedingTracker(world, player.getGameProfile());
		if (tracker.isResearched(mutation)) {
			player.addChatMessage(new ChatComponentTranslation("for.chat.cannotmemorizeagain"));
			return false;
		}

		IAlleleSpecies<C> species0 = mutation.getSpecies0();
		IAlleleSpecies<C> species1 = mutation.getSpecies1();
		IGenome<C> resultGenome = root.templateAsGenome(mutation.getResultTemplate());
		IAlleleSpecies<C> speciesResult = resultGenome.getPrimary();

		tracker.registerSpecies(species0);
		tracker.registerSpecies(species1);
		tracker.registerSpecies(speciesResult);

		tracker.researchMutation(mutation);
		player.addChatMessage(new ChatComponentTranslation("for.chat.memorizednote"));

		player.addChatMessage(new ChatComponentTranslation("for.chat.memorizednote2",
				EnumChatFormatting.GRAY + species0.getName(),
				EnumChatFormatting.GRAY + species1.getName(),
				EnumChatFormatting.GREEN + speciesResult.getName()));

		return true;
	}

	@Override
	public void addTooltip(@Nonnull List<String> list) {
		ISpeciesRoot<C> root = mutation.getRoot();
		String species1 = mutation.getSpecies0().getName();
		String species2 = mutation.getSpecies1().getName();
		String mutationChanceKey = EnumMutateChance.rateChance(mutation.getBaseChance()).toString().toLowerCase(Locale.ENGLISH);
		String mutationChance = StringUtil.localize("researchNote.chance." + mutationChanceKey);
		String speciesResult = mutation.getResultTemplate().get(root.getKaryotypeKey()).getName();

		list.add(StringUtil.localize("researchNote.discovery.0"));
		list.add(StringUtil.localize("researchNote.discovery.1").replace("%SPEC1", species1).replace("%SPEC2", species2));
		list.add(StringUtil.localizeAndFormat("researchNote.discovery.2", mutationChance));
		list.add(StringUtil.localizeAndFormat("researchNote.discovery.3", speciesResult));

		if (mutation.getSpecialConditions().size() > 0) {
			for (String line : mutation.getSpecialConditions()) {
				list.add(EnumChatFormatting.GOLD + line);
			}
		}
	}
}
