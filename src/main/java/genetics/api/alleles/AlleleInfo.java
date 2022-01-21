package genetics.api.alleles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class AlleleInfo {
	public final ResourceLocation fileName;
	public final CompoundTag data;
	public boolean dominant = false;
	public int weight = 0;
	/* True if this allele could replaced other data */
	public boolean replace = false;
	/* True if this allele has replaced other data */
	public boolean replaced = false;
	public String parent = "";
	public String name = "";
	public String type = "";

	public AlleleInfo(ResourceLocation fileName, CompoundTag data) {
		this.fileName = fileName;
		this.data = data;
	}
}
