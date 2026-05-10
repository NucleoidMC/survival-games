package supercoder79.survivalgames.game;

import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.widget.BossBarWidget;

public final class SurvivalGamesBar {
	private final BossBarWidget widget;

	private SurvivalGamesBar(BossBarWidget widget) {
		this.widget = widget;
	}

	public static SurvivalGamesBar create(GlobalWidgets widgets) {
		return new SurvivalGamesBar(widgets.addBossBar(Component.literal("Worldborder safe! Shrinking in ..."), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS));
	}

	public void tickSafe(long ticks, long totalTicks) {
		String time = formatTime(ticks);

		this.widget.setTitle(Component.literal("Worldborder safe! Shrinking in " + time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void tickActive(long ticks, long totalTicks) {
		String time = formatTime(ticks);

		this.widget.setTitle(Component.literal("Worldborder shrinking! Finished in " + time));
		this.widget.setProgress((float) ticks / totalTicks);
	}

	public void setFinished() {
		this.widget.setStyle(BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
		this.widget.setTitle(Component.literal("Worldborder finished. Fight!"));
		this.widget.setProgress(1.0f);
	}

	public void setActive() {
		this.widget.setStyle(BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
	}

	private static String formatTime(long ticksUntil) {
		long secondsUntil = ticksUntil / 20;

		long minutes = secondsUntil / 60;
		long seconds = secondsUntil % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}
}
