package com.steam.steamcore.integration.kubejs;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class BossProgressHookEvent {
    private final Level level;
    private final LivingEntity boss;
    private final String bossId;
    private int points;
    private boolean cancelled;
    public BossProgressHookEvent(Level level, LivingEntity boss, String bossId, int points) { this.level = level; this.boss = boss; this.bossId = bossId; this.points = points; }
    public Level getLevel() { return level; }
    public LivingEntity getBoss() { return boss; }
    public String getBossId() { return bossId; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = Math.max(0, points); }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
