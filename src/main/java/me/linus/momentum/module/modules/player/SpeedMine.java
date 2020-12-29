package me.linus.momentum.module.modules.player;

import me.linus.momentum.event.events.world.DamageBlockEvent;
import me.linus.momentum.module.Module;
import me.linus.momentum.setting.mode.Mode;
import me.linus.momentum.util.world.BlockUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author linustouchtips
 * @since 12/03/2020
 */

public class SpeedMine extends Module {
    public SpeedMine() {
        super("SpeedMine", Category.PLAYER, "Allows you to mine faster");
    }

    private static final Mode mode = new Mode("Mode", "Packet", "Damage", "Creative", "Instant", "Delay", "Vanilla");

    @Override
    public void setup() {
        addSetting(mode);
    }

    @Override
    public void onUpdate() {
        if (mode.getValue() == 3)
            mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.HASTE, 80950, 1, false, false)));
    }

    @SubscribeEvent
    public void onDamageBlock(DamageBlockEvent event) {
        if (nullCheck())
            return;

        if (!this.isEnabled())
            return;

        if (BlockUtil.canBreak(event.getPos())) {
            switch (mode.getValue()) {
                case 0:
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    event.setCanceled(true);
                    break;
                case 1:
                    if (mc.playerController.curBlockDamageMP >= 0.5f)
                        mc.playerController.curBlockDamageMP = 1.0f;
                    break;
                case 2:
                    mc.playerController.curBlockDamageMP = 1.0f;
                    break;
                case 3:
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    mc.playerController.onPlayerDestroyBlock(event.getPos());
                    mc.world.setBlockToAir(event.getPos());
                    break;
                case 4:
                    mc.playerController.blockHitDelay = 0;
                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == 3)
            mc.player.removePotionEffect(MobEffects.HASTE);
    }

    @Override
    public String getHUDData() {
        return " " + mode.getMode(mode.getValue());
    }
}
