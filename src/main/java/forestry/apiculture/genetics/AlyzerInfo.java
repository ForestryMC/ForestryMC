package forestry.apiculture.genetics;

public class AlyzerInfo {
    public static final AlyzerInfo EMPTY = new AlyzerInfo(AlyzerPage.NONE);

    public final AlyzerPage page;

    public AlyzerInfo(AlyzerPage page) {
        this.page = page;
    }

    public enum AlyzerPage {
        NONE,
        PAGE_0,
        PAGE_1,
        PAGE_2,
        PAGE_3
    }
}
