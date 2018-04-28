package forestry.core.translation;

public interface TranslatorProxy {

	String translateToLocal(String key);

	boolean canTranslateToLocal(String key);

	String translateToLocalFormatted(String key, Object... format);


}
