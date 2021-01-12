package me.linus.momentum.module.modules.render;

import me.linus.momentum.module.Module;
import me.linus.momentum.setting.checkbox.Checkbox;
import me.linus.momentum.setting.color.SubColor;
import me.linus.momentum.setting.slider.Slider;
import me.linus.momentum.util.render.RenderUtil;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/**
 * @author linustouchtips
 * @since 11/30/2020
 */

public class BreakESP extends Module {
    public BreakESP() {
        super("BreakESP", Category.RENDER, "Highlights blocks being broken");
    }

    public static Slider range = new Slider("Range", 0.0D, 12.0D, 20.0D, 0);
    public static Checkbox outline = new Checkbox("Outline", false);
    public static Checkbox showInfo = new Checkbox("Render Info", true);

    public static Checkbox color = new Checkbox("Color", true);
    public static SubColor colorPicker = new SubColor(color, new Color(255, 0, 0, 55));

    @Override
    public void setup() {
        addSetting(range);
        addSetting(outline);
        addSetting(showInfo);
        addSetting(color);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent eventRender) {
        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null && destroyBlockProgress.getPosition().getDistance((int) mc.player.posX,(int)  mc.player.posY,(int)  mc.player.posZ) <= range.getValue()) {
                RenderUtil.drawBoxBlockPos(destroyBlockProgress.getPosition(), 0, colorPicker.getColor());

                if (outline.getValue())
                    RenderUtil.drawBoundingBoxBlockPos(destroyBlockProgress.getPosition(), 0, new Color(colorPicker.getColor().getRed(), colorPicker.getColor().getGreen(), colorPicker.getColor().getBlue(), 144));

                if (showInfo.getValue())
                    RenderUtil.drawNametagFromBlockPos(destroyBlockProgress.getPosition(), mc.world.getEntityByID(integer).getName() + ": " + (destroyBlockProgress.getPartialBlockDamage() * 10) + "%");
            }
        });
    }
}
