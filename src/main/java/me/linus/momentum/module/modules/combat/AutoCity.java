package me.linus.momentum.module.modules.combat;

import me.linus.momentum.gui.theme.ThemeColor;
import me.linus.momentum.module.Module;
import me.linus.momentum.setting.checkbox.Checkbox;
import me.linus.momentum.setting.color.ColorPicker;
import me.linus.momentum.setting.slider.Slider;
import me.linus.momentum.util.client.MessageUtil;
import me.linus.momentum.util.combat.EnemyUtil;
import me.linus.momentum.util.player.InventoryUtil;
import me.linus.momentum.util.render.RenderUtil;
import me.linus.momentum.util.render.builder.RenderBuilder;
import me.linus.momentum.util.world.BlockUtil;
import me.linus.momentum.util.world.BlockUtil.BlockResistance;
import me.linus.momentum.util.world.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author linustouchtips
 * @since 12/20/2020
 */

public class AutoCity extends Module {
    public AutoCity() {
        super("AutoCity", Category.COMBAT, "Automatically breaks blocks at the enemies' feet");
    }

    public static Slider range = new Slider("Range", 0.0D, 7.0D, 10.0D, 0);
    public static Slider enemyRange = new Slider("Enemy Range", 0.0D, 7.0D, 10.0D, 0);
    public static Checkbox autoSwitch = new Checkbox("Auto Switch", true);
    public static Checkbox packet = new Checkbox("Packet", true);
    public static Checkbox disable = new Checkbox("Disable", true);

    public static Checkbox color = new Checkbox("Color", true);
    public static ColorPicker colorPicker = new ColorPicker(color, "Color Picker", ThemeColor.RAW);

    @Override
    public void setup() {
        addSetting(range);
        addSetting(enemyRange);
        addSetting(autoSwitch);
        addSetting(packet);
        addSetting(disable);
    }

    EntityPlayer currentTarget;
    BlockPos breakTarget;
    List<BlockPos> cityBlocks = new ArrayList<>();

    @Override
    public void onEnable() {
        if (nullCheck())
            return;

        super.onEnable();

        cityBlocks.addAll(EnemyUtil.getCityBlocks(currentTarget, false));

       if (BlockUtil.getBlockResistance(breakTarget) == BlockResistance.Blank && disable.getValue()) {
            MessageUtil.sendClientMessage("Finished citying!");
            this.disable();
            return;
       }

       breakTarget = cityBlocks.stream().filter(blockPos -> mc.player.getDistanceSq(blockPos) > range.getValue()).min(Comparator.comparing(blockPos -> mc.player.getDistanceSq(blockPos))).orElse(null);
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        if (currentTarget == null) {
            this.disable();
            MessageUtil.sendClientMessage("There is nobody to city!");
        }

        cityBlocks.clear();
        currentTarget = WorldUtil.getClosestPlayer(enemyRange.getValue());

        if (autoSwitch.getValue())
            InventoryUtil.switchToSlot(Items.DIAMOND_PICKAXE);

        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakTarget, EnumFacing.DOWN));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakTarget, EnumFacing.DOWN));

        if (packet.getValue())
            this.disable();
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent eventRender) {
        if (breakTarget != null)
            RenderUtil.drawBoxBlockPos(breakTarget, 0, colorPicker.getColor(), RenderBuilder.RenderMode.Fill);
    }

    @Override
    public String getHUDData() {
         return currentTarget != null ? " " + currentTarget.getName() : " None";
    }
}
