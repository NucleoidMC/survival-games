package supercoder79.survivalgames.game.map.gen.processor;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class SurvivalGamesProcessorTypes {
	public static void init() {
		Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("survivalgames", "loot"), LootChestProcessor.CODEC);
		Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("survivalgames", "chance_at"), ChanceAtProcessor.CODEC);
	}
}
