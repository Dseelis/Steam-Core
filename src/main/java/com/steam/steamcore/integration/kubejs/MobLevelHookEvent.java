package com.steam.steamcore.integration.kubejs;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public final class MobLevelHookEvent {
    private final Level level;
    private final Mob mob;
    private int levelValue;
    private boolean cancelled;
    public MobLevelHookEvent(Level level, Mob mob, int levelValue) { this.level = level; this.mob = mob; this.levelValue = levelValue; }
    public Level getLevel() { return level; }
    public Mob getMob() { return mob; }
    public int getLevelValue() { return levelValue; }
    public void setLevel(int level) { levelValue = Math.max(1, Math.min(10, level)); }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
