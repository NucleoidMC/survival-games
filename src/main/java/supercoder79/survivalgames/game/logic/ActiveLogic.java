package supercoder79.survivalgames.game.logic;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import xyz.nucleoid.stimuli.event.EventResult;

public interface ActiveLogic {
    void tick(long time);

    default EventResult onEntityDeath(Entity entity, DamageSource source) {
        return EventResult.PASS;
    }
}
