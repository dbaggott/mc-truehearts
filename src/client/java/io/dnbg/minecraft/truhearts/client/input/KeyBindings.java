package io.dnbg.minecraft.truhearts.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/**
 * Client-side key bindings for TruHearts. Registered at client init so they
 * appear in the vanilla Controls menu grouped under a "TruHearts" category.
 *
 * <p>All bindings default to {@link InputConstants#UNKNOWN} (unbound) so the
 * mod doesn't shadow a key the player might already have mapped to something
 * else. Players bind whatever key they want in Controls.
 *
 * <p>The unbound sentinel comes from Minecraft's own {@link InputConstants}
 * rather than {@code org.lwjgl.glfw.GLFW}: LWJGL is not on the compile
 * classpath from MC 26.3 onward, whereas {@code InputConstants.UNKNOWN}
 * resolves identically on every line the mod supports.
 *
 * <p>The category's visible label comes from the {@code key.category.truhearts.main}
 * translation key (composed as {@code key.category} + {@code namespace.path} of
 * the {@link Identifier}) — see {@code en_us.json}.
 */
public final class KeyBindings {
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath("truhearts", "main")
	);

	/** Master toggle: turns every TruHearts overlay on/off. */
	public static final KeyMapping TOGGLE = new KeyMapping(
		"key.truhearts.toggle",
		InputConstants.UNKNOWN.getValue(),
		CATEGORY
	);

	/**
	 * Sub-toggle: turns just the recent-damage log on/off. The master
	 * {@link #TOGGLE} still short-circuits everything when off — this only
	 * matters when the master is on.
	 */
	public static final KeyMapping TOGGLE_DAMAGE_LOG = new KeyMapping(
		"key.truhearts.toggle_damage_log",
		InputConstants.UNKNOWN.getValue(),
		CATEGORY
	);

	private KeyBindings() {
	}

	public static void register() {
		KeyMappingHelper.registerKeyMapping(TOGGLE);
		KeyMappingHelper.registerKeyMapping(TOGGLE_DAMAGE_LOG);
	}
}
