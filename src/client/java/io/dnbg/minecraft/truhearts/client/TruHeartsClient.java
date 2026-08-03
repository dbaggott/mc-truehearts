package io.dnbg.minecraft.truhearts.client;

import io.dnbg.minecraft.truhearts.client.config.TruHeartsConfig;
import io.dnbg.minecraft.truhearts.client.hud.DamageLog;
import io.dnbg.minecraft.truhearts.client.hud.HpReadout;
import io.dnbg.minecraft.truhearts.client.hud.ToggleToast;
import io.dnbg.minecraft.truhearts.client.input.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Client entry point. Register HUD callbacks, attack callbacks, and any
 * other client-only wiring here.
 *
 * <p>Feature classes (HUD overlay, damage tracker, …) live as siblings
 * under {@code client.hud} / sibling sub-packages — this file stays as a
 * small wiring hub so new features land as one-line additions here.
 *
 * <h2>Multi-MC-version support</h2>
 *
 * <p>A single jar covers MC 26.1 through the whole 26.3 line. That holds
 * because the mod restricts itself to APIs whose shape is stable across
 * those versions. Two places needed an explicit accommodation:
 *
 * <ul>
 *   <li>{@link ToggleToast} renders the toggle message through the mod's
 *       own HUD element rather than calling vanilla's
 *       {@code setOverlayMessage}, which sits on {@code Gui} in 26.1 and
 *       on {@code Gui.hud} in 26.2 — no single source form compiles
 *       against both.</li>
 *   <li>{@link KeyBindings} takes its unbound-key sentinel from
 *       {@code InputConstants} rather than {@code org.lwjgl.glfw.GLFW},
 *       which is not on the compile classpath from 26.3 onward.</li>
 * </ul>
 *
 * <p>The supported range itself is declared in {@code fabric.mod.json};
 * the README's "Minecraft version range" section explains how one range
 * spans snapshots, release candidates, and the final release.
 */
public class TruHeartsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HpReadout.register();
		DamageLog.register();
		ToggleToast.register();
		KeyBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(TruHeartsClient::onClientTickEnd);
	}

	/**
	 * Per-tick client-side hook. Drains any pending toggle-key clicks
	 * (master + per-feature) and hands the tick to {@link DamageLog} so
	 * it can refresh its damage-baseline tracking.
	 *
	 * <p>{@code consumeClick()} can return true more than once per tick
	 * under lag, hence the drain loop per keybind.
	 *
	 * <p>Toggle confirmations go through {@link ToggleToast} rather than
	 * vanilla's {@code setOverlayMessage} — see the "Multi-MC-version
	 * support" section of this class's javadoc for why.
	 */
	private static void onClientTickEnd(Minecraft client) {
		while (KeyBindings.TOGGLE.consumeClick()) {
			TruHeartsConfig cfg = TruHeartsConfig.get();
			cfg.enabled = !cfg.enabled;
			cfg.save();
			ToggleToast.show(Component.translatable(
				"truhearts.toggle." + (cfg.enabled ? "on" : "off")));
		}
		while (KeyBindings.TOGGLE_DAMAGE_LOG.consumeClick()) {
			TruHeartsConfig cfg = TruHeartsConfig.get();
			cfg.recentDamageEnabled = !cfg.recentDamageEnabled;
			cfg.save();
			ToggleToast.show(Component.translatable(
				"truhearts.toggle.damage_log." + (cfg.recentDamageEnabled ? "on" : "off")));
		}
		if (client.player != null) {
			DamageLog.onClientTickEnd(client.player);
		} else {
			// Player disconnected / world unloaded — clear the log so a
			// re-join doesn't attribute stale damage or leak old entries.
			DamageLog.reset();
		}
	}
}
