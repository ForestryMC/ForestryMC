package forestry.book.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBack;
import forestry.book.gui.buttons.GuiButtonPage;
import forestry.core.config.Constants;
import forestry.core.gui.GuiWindow;
import forestry.core.gui.IGuiSizable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class GuiForesterBook extends GuiWindow implements IGuiSizable {
    public static final ResourceLocation TEXTURE = new ResourceLocation(
            Constants.MOD_ID,
            Constants.TEXTURE_PATH_GUI + "/almanac/almanac.png"
    );
    static final int LEFT_PAGE_START_X = 16;
    static final int RIGHT_PAGE_START_X = 132;
    static final int PAGE_START_Y = 12;
    static final int LEFT_PAGE_START_Y = 25;
    static final int RIGHT_PAGE_START_Y = PAGE_START_Y;
    public static final int PAGE_WIDTH = 108;
    public static final int PAGE_HEIGHT = 155;

    private static final int X_SIZE = 256;
    private static final int Y_SIZE = 181;
    @Nullable
    private static GuiForesterBook guiScreen;
    protected final IForesterBook book;

    protected GuiForesterBook(IForesterBook book) {
        super(X_SIZE, Y_SIZE, new StringTextComponent("FORESTER_BOOK_TITLE"));    //TODO localise
        this.book = book;
        setGuiScreen(this);
    }

    public IForesterBook getBook() {
        return book;
    }

    @Override
    public void init() {
        super.init();
        this.buttons.clear();
        if (hasButtons()) {
            GuiButtonPage leftButton = addButton(new GuiButtonPage(
                    guiLeft + 24,
                    guiTop + Y_SIZE - 20,
                    true,
                    this::actionPerformed
            ));
            GuiButtonPage rightButton = addButton(new GuiButtonPage(
                    guiLeft + X_SIZE - 44,
                    guiTop + Y_SIZE - 20,
                    false,
                    this::actionPerformed
            ));
            GuiButtonBack backButton = addButton(new GuiButtonBack(
                    guiLeft + X_SIZE / 2 - 12,
                    guiTop + Y_SIZE - 20,
                    this::actionPerformed
            ));
            initButtons(leftButton, rightButton, backButton);
        }
    }

    protected boolean hasButtons() {
        return true;
    }

    protected void initButtons(GuiButtonPage leftButton, GuiButtonPage rightButton, GuiButtonBack backButton) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        TextureManager manager = this.minecraft.textureManager;

        manager.bindTexture(TEXTURE);
        blit(transform, guiLeft, guiTop, 0, 0, X_SIZE, Y_SIZE);

        super.render(transform, mouseX, mouseY, partialTicks);

        boolean unicode = minecraft.fontRenderer.getBidiFlag();
        //minecraft.fontRenderer.setBidiFlag(true);
        //drawCenteredString(minecraft.fontRenderer, title.applyTextStyle(TextFormatting.UNDERLINE), guiLeft + LEFT_PAGE_START_X + 52, guiTop + PAGE_START_Y, 0xD3D3D3);

        drawText(transform);

        //minecraft.fontRenderer.setBidiFlag(unicode);

        drawTooltips(transform, mouseY, mouseX);
    }

    @Override
    protected void drawTooltips(MatrixStack transform, int mouseY, int mouseX) {
        super.drawTooltips(transform, mouseY, mouseX);
        PlayerInventory playerInv = minecraft.player.inventory;

        if (playerInv.getItemStack().isEmpty()) {
            List<ITextComponent> tooltip = getTooltip(mouseX, mouseY);
            if (!tooltip.isEmpty()) {
                MainWindow mainWindow = getMC().getMainWindow();
                GuiUtils.drawHoveringText(
                        transform,
                        tooltip,
                        mouseX,
                        mouseY,
                        mainWindow.getScaledWidth(),
                        mainWindow.getScaledHeight(),
                        -1,
                        getMC().fontRenderer
                );
            }
        }
    }

    protected void drawText(MatrixStack transform) {
    }

    protected List<ITextComponent> getTooltip(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Nullable
    public static GuiForesterBook getGuiScreen() {
        return guiScreen;
    }

    public static void setGuiScreen(@Nullable GuiForesterBook guiScreen) {
        GuiForesterBook.guiScreen = guiScreen;
    }

    protected abstract void actionPerformed(Button button);

}
