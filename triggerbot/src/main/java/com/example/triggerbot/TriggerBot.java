package com.example.triggerbot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Random;

public final class TriggerBot {
    private static final Random RNG = new Random();

    private static int lastTargetId = -1;
    private static long acquiredAt = 0;
    private static long delay = 0;

    private TriggerBot() {}

    public static void reset() {
        lastTargetId = -1;
    }

    public static void tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        TriggerBotConfig cfg = TriggerBotConfig.INSTANCE;

        if (p == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        if (mc.currentScreen != null || !p.isAlive() || p.isSpectator()) {
            reset();
            return;
        }

        if (!(mc.crosshairTarget instanceof EntityHitResult hit)) {
            reset();
            return;
        }

        Entity target = hit.getEntity();

        if (!isValidTarget(p, target)) {
            reset();
            return;
        }

        if (cfg.pauseWhileUsingItem && p.isUsingItem()) {
            return;
        }

        if (!holdingAllowedItem(p)) {
            if (cfg.autoSwitch) {
                switchToWeapon(p);
            }
            return;
        }

        long now = System.currentTimeMillis();

        if (target.getId() != lastTargetId) {
            lastTargetId = target.getId();
            acquiredAt = now;

            int min = Math.min(cfg.minDelayMs, cfg.maxDelayMs);
            int max = Math.max(cfg.minDelayMs, cfg.maxDelayMs);

            delay = min + (max > min
                    ? RNG.nextInt(max - min + 1)
                    : 0);
        }

        if (now - acquiredAt < delay) {
            return;
        }

        if (p.getAttackCooldownProgress(0.5f)
                < cfg.minCooldownPercent / 100f) {
            return;
        }

        if (cfg.criticalsOnly && !canCrit(p)) {
            return;
        }

        mc.interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);

        reset();
    }

    private static boolean canCrit(ClientPlayerEntity p) {
        return !p.isOnGround()
                && p.getVelocity().y < 0
                && !p.isClimbing()
                && !p.isTouchingWater()
                && !p.hasVehicle();
    }

    private static boolean isValidTarget(
            ClientPlayerEntity p,
            Entity t
    ) {
        TriggerBotConfig cfg = TriggerBotConfig.INSTANCE;

        if (!(t instanceof LivingEntity le) || t == p) {
            return false;
        }

        if (!le.isAlive() || !t.isAttackable()) {
            return false;
        }

        if (cfg.ignoreInvisible && t.isInvisible()) {
            return false;
        }

        if (cfg.ignoreTeammates && t.isTeammate(p)) {
            return false;
        }

        String id = Registries.ENTITY_TYPE
                .getId(t.getType())
                .toString();

        if (cfg.ignoredEntities.contains(id)) {
            return false;
        }

        if (cfg.alwaysAttackEntities.contains(id)) {
            return true;
        }

        if (le instanceof PlayerEntity pl) {
            if (pl.isCreative() || pl.isSpectator()) {
                return false;
            }
            return cfg.attackPlayers;
        }

        if (cfg.ignorePets
                && le instanceof TameableEntity te
                && te.isTamed()) {
            return false;
        }

        if (le instanceof VillagerEntity
                || le instanceof WanderingTraderEntity) {
            return cfg.attackVillagers;
        }

        if (le instanceof Monster) {
            return cfg.attackHostile;
        }

        if (le instanceof Angerable) {
            return cfg.attackNeutral;
        }

        if (le instanceof PassiveEntity
                || le instanceof WaterCreatureEntity
                || le instanceof AmbientEntity) {
            return cfg.attackPassive;
        }

        return cfg.attackOther;
    }

    private static boolean holdingAllowedItem(ClientPlayerEntity p) {
        TriggerBotConfig cfg = TriggerBotConfig.INSTANCE;

        if (cfg.anyItem) {
            return true;
        }

        ItemStack stack = p.getMainHandStack();

        if (stack.isEmpty()) {
            return cfg.allowEmptyHand;
        }

        return isAllowedWeapon(stack);
    }

    private static boolean isAllowedWeapon(ItemStack stack) {
        TriggerBotConfig cfg = TriggerBotConfig.INSTANCE;

        if (cfg.weaponSword && stack.isIn(ItemTags.SWORDS)) {
            return true;
        }

        if (cfg.weaponAxe && stack.isIn(ItemTags.AXES)) {
            return true;
        }

        if (cfg.weaponMace && stack.isOf(Items.MACE)) {
            return true;
        }

        if (cfg.weaponTrident && stack.isOf(Items.TRIDENT)) {
            return true;
        }

        return cfg.customItems.contains(
                Registries.ITEM.getId(stack.getItem()).toString()
        );
    }

    private static void switchToWeapon(ClientPlayerEntity p) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getStack(i);

            if (!stack.isEmpty() && isAllowedWeapon(stack)) {
                p.getInventory().setSelectedSlot(i);
                return;
            }
        }
    }
}
