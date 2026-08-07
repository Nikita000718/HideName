package com.felix.hidename;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = HideName.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Map<UUID, Long> VISIBLE_PLAYERS = new ConcurrentHashMap<>();
    public static long SHOW_DURATION_MS = 5000L; // 5 секунд

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && event.getAction() == GLFW.GLFW_PRESS) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null && mc.screen == null && mc.hitResult != null) {
                if (mc.hitResult.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHit = (EntityHitResult) mc.hitResult;
                    if (entityHit.getEntity() instanceof Player targetPlayer) {
                        long expireTimestamp = System.currentTimeMillis() + SHOW_DURATION_MS;
                        VISIBLE_PLAYERS.put(targetPlayer.getUUID(), expireTimestamp);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (event.getEntity() instanceof Player player) {
            Minecraft mc = Minecraft.getInstance();

            if (player == mc.player) {
                return;
            }

            Long expireTime = VISIBLE_PLAYERS.get(player.getUUID());
            long currentTime = System.currentTimeMillis();

            if (expireTime != null && currentTime < expireTime) {
                event.setCanRender(TriState.TRUE);
            } else {
                event.setCanRender(TriState.FALSE);
                if (expireTime != null) {
                    VISIBLE_PLAYERS.remove(player.getUUID());
                }
            }
        }
    }
}