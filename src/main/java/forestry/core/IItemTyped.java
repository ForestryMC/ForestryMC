package forestry.core;

public interface IItemTyped {
	@SuppressWarnings("rawtypes")
	public Enum getTypeFromMeta(int meta);
}
