package me.linus.momentum.module.modules.render;

import me.linus.momentum.module.Module;
import me.linus.momentum.setting.checkbox.Checkbox;
import me.linus.momentum.setting.color.ColorPicker;
import me.linus.momentum.setting.mode.Mode;
import me.linus.momentum.setting.slider.Slider;
import me.linus.momentum.setting.slider.SubSlider;
import me.linus.momentum.util.render.RenderUtil;
import me.linus.momentum.util.render.builder.RenderBuilder.RenderMode;
import me.linus.momentum.util.world.BlockUtil;
import me.linus.momentum.util.world.hole.HoleUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linustouchtips
 * @since 11/29/2020
 */

public class HoleESP extends Module {
    public HoleESP() {
        super("HoleESP", Category.RENDER, "Highlights safe holes to stand in while crystalling");
    }

    public static Mode main = new Mode("Main",  "Glow", "Fill", "None");
    public static SubSlider mainHeight = new SubSlider(main, "Main Height", -1.0D, 1.0D, 3.0D, 1);

    public static Mode outline = new Mode("Outline", "WireFrame", "Claw", "None");
    public static SubSlider outlineHeight = new SubSlider(outline, "Outline Height", -1.0D, 0.0D, 3.0D, 1);

    public static Mode highlight = new Mode("Highlight", "None", "NoRender", "Glow");

    public static Checkbox obsidianColor = new Checkbox("Obsidian Color", true);
    public static ColorPicker obsidianPicker = new ColorPicker(obsidianColor, "Obsidian Picker", new Color(144, 0, 255, 45));

    public static Checkbox bedrockColor = new Checkbox("Bedrock Color", true);
    public static ColorPicker bedrockPicker = new ColorPicker(bedrockColor, "Bedrock Picker", new Color(93, 235, 240, 45));

    public static Checkbox doubles = new Checkbox("Doubles", true);
    public static Checkbox viewFrustrum = new Checkbox("Only in View Frustrum", false);
    public static Slider lineWidth = new Slider("Line Width", 0.0D, 1.5D, 3.0D, 2);
    public static Slider lineAlpha = new Slider("Line Alpha", 0.0D, 144.0D, 255.0D, 0);
    public static Slider range = new Slider("Range", 0.0D, 7.0D, 20.0D, 0);

    @Override
    public void setup() {
        addSetting(main);
        addSetting(outline);
        addSetting(highlight);
        addSetting(obsidianColor);
        addSetting(bedrockColor);
        addSetting(doubles);
        addSetting(viewFrustrum);
        addSetting(lineWidth);
        addSetting(lineAlpha);
        addSetting(range);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent renderEvent) {
        RenderUtil.camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        renderMain(obsidianPicker.getColor(), bedrockPicker.getColor());
        renderOutline(obsidianPicker.getColor(), bedrockPicker.getColor());
    }

    public void renderMain(Color obbyColor, Color bRockColor) {

        findObsidianHoles().forEach(hole -> {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(hole.getX() - mc.getRenderManager().viewerPosX, hole.getY() - mc.getRenderManager().viewerPosY, hole.getZ() - mc.getRenderManager().viewerPosZ, hole.getX() + 1 - mc.getRenderManager().viewerPosX, hole.getY() + 1 - mc.getRenderManager().viewerPosY, hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);

            if (!RenderUtil.camera.isBoundingBoxInFrustum(axisAlignedBB) && viewFrustrum.getValue())
                return;

            switch (main.getValue()) {
                case 0:
                    RenderUtil.drawBoxBlockPos(hole, mainHeight.getValue() - 1, obbyColor, RenderMode.Glow);
                    break;
                case 1:
                    RenderUtil.drawBoxBlockPos(hole, mainHeight.getValue() - 1, obbyColor, RenderMode.Fill);
                    break;
            }

            if (mc.player.getDistanceSq(hole) < 1) {
                switch (highlight.getValue()) {
                    case 0:
                    case 1:
                        break;
                    case 2:
                        RenderUtil.drawBoxBlockPos(hole, mainHeight.getValue() - 1, obbyColor, RenderMode.Glow);
                        break;
                }
            }
        });

        findBedRockHoles().forEach(hole -> {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(hole.getX() - mc.getRenderManager().viewerPosX, hole.getY() - mc.getRenderManager().viewerPosY, hole.getZ() - mc.getRenderManager().viewerPosZ, hole.getX() + 1 - mc.getRenderManager().viewerPosX, hole.getY() + 1 - mc.getRenderManager().viewerPosY, hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);

            if (!RenderUtil.camera.isBoundingBoxInFrustum(axisAlignedBB) && viewFrustrum.getValue())
                return;

            switch (main.getValue()) {
                case 0:
                    RenderUtil.drawBoxBlockPos(hole, mainHeight.getValue() - 1, bRockColor, RenderMode.Glow);
                    break;
                case 1:
                    RenderUtil.drawBoxBlockPos(hole, mainHeight.getValue() - 1, bRockColor, RenderMode.Fill);
                    break;
            }

            if (mc.player.getDistanceSq(hole) < 1) {
                switch (highlight.getValue()) {
                    case 0:
                    case 1:
                        break;
                    case 2:
                        RenderUtil.drawBoxBlockPos(hole, mainHeight.getValue() - 1, bRockColor, RenderMode.Glow);
                        break;
                }
            }
        });
    }

    public void renderOutline(Color obbyColor, Color bRockColor) {
        findObsidianHoles().forEach(hole -> {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(hole.getX() - mc.getRenderManager().viewerPosX, hole.getY() - mc.getRenderManager().viewerPosY, hole.getZ() - mc.getRenderManager().viewerPosZ, hole.getX() + 1 - mc.getRenderManager().viewerPosX, hole.getY() + 1 - mc.getRenderManager().viewerPosY, hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);

            if (!RenderUtil.camera.isBoundingBoxInFrustum(axisAlignedBB) && viewFrustrum.getValue())
                return;

            switch (outline.getValue()) {
                case 0:
                    GL11.glLineWidth((float) lineWidth.getValue());
                    RenderUtil.drawBoxBlockPos(hole, outlineHeight.getValue() - 1, obbyColor, RenderMode.Outline);
                    break;
                case 1:
                    RenderUtil.drawBoxBlockPos(hole, outlineHeight.getValue() - 1, obbyColor, RenderMode.Claw);
                    break;
            }
        });

        findBedRockHoles().forEach(hole -> {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(hole.getX() - mc.getRenderManager().viewerPosX, hole.getY() - mc.getRenderManager().viewerPosY, hole.getZ() - mc.getRenderManager().viewerPosZ, hole.getX() + 1 - mc.getRenderManager().viewerPosX, hole.getY() + 1 - mc.getRenderManager().viewerPosY, hole.getZ() + 1 - mc.getRenderManager().viewerPosZ);

            if (!RenderUtil.camera.isBoundingBoxInFrustum(axisAlignedBB) && viewFrustrum.getValue())
                return;

            switch (outline.getValue()) {
                case 0:
                    GL11.glLineWidth((float) lineWidth.getValue());
                    RenderUtil.drawBoxBlockPos(hole, outlineHeight.getValue() - 1, bRockColor, RenderMode.Outline);
                    break;
                case 1:
                    RenderUtil.drawBoxBlockPos(hole, outlineHeight.getValue() - 1, bRockColor, RenderMode.Claw);
                    break;
            }
        });
    }

    List<BlockPos> findObsidianHoles() {
        return BlockUtil.getNearbyBlocks(mc.player, range.getValue(), false).stream().filter(HoleUtil::isObsidianHole).collect(Collectors.toList());
    }

    List<BlockPos> findBedRockHoles() {
        return BlockUtil.getNearbyBlocks(mc.player, range.getValue(), false).stream().filter(HoleUtil::isBedRockHole).collect(Collectors.toList());
    }

    @Override
    public String getHUDData() {
        return " " + main.getMode(main.getValue()) + ", " + outline.getMode(outline.getValue());
    }
}