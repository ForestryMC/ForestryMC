package forestry.core.gui;

import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.function.Supplier;

public class CycleTimer {
    private long startTime;
    private long drawTime;
    private long pausedDuration = 0L;

    public CycleTimer(int offset) {
        long time = System.currentTimeMillis();
        this.startTime = time - (long) (offset * 1000);
        this.drawTime = time;
    }

    public <T> T getCycledItem(List<T> list, Supplier<T> fallback) {
        if (list.isEmpty()) {
            return fallback.get();
        } else {
            long index = (this.drawTime - this.startTime) / 1000L % (long) list.size();
            return list.get(Math.toIntExact(index));
        }
    }

    public void onDraw() {
        if (!Screen.hasShiftDown()) {
            if (this.pausedDuration > 0L) {
                this.startTime += this.pausedDuration;
                this.pausedDuration = 0L;
            }

            this.drawTime = System.currentTimeMillis();
        } else {
            this.pausedDuration = System.currentTimeMillis() - this.drawTime;
        }

    }
}
