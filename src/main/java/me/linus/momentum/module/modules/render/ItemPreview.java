package me.linus.momentum.module.modules.render;

import me.linus.momentum.module.Module;
import me.linus.momentum.setting.checkbox.Checkbox;
import me.linus.momentum.util.render.FontUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static net.minecraft.client.renderer.ItemRenderer.RES_MAP_BACKGROUND;

/**
 * @author linustouchtips
 * @since 12/17/2020
 */

public class ItemPreview extends Module {
    public ItemPreview() {
        super("ItemPreview", Category.RENDER, "Renders better tooltips");
    }

    public static Checkbox shulkers = new Checkbox("Shulkers", true);
    public static Checkbox maps = new Checkbox("Maps", true);

    @Override
    public void setup() {
        addSetting(shulkers);
        addSetting(maps);
    }

    static RenderItem itemRender = mc.getRenderItem();

    public static void tooltipShulker(ItemStack itemStack, int x, int y, CallbackInfo callbackInfo) {
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
            NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
            if (blockEntityTag.hasKey("Items", 9)) {
                callbackInfo.cancel();

                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);
                GlStateManager.enableBlend();
                GlStateManager.disableRescaleNormal();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();

                int x1 = x + 12;
                int y1 = y - 12;
                int height = 57;
                int width = (int) Math.max(144, FontUtil.getStringWidth(itemStack.getDisplayName())+3);

                itemRender.zLevel = 300.0F;
                GuiScreen.drawRect(x1 - 4, y1 - 9, x1 + width + 1, y1 + 5, new Color(54, 54, 54, 164).getRGB());
                GuiScreen.drawRect(x1 - 4, y1 + 5, x1 + width + 1, y1 + height + 3, new Color(0, 0, 0, 144).getRGB());
                FontUtil.drawString(itemStack.getDisplayName(), x1 - 2, y1 - 8, new Color(255, 255, 255).getRGB());

                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();

                for (int i = 0; i < nonnulllist.size(); i++) {
                    int iX = x + (i % 9) * 16 + 11;
                    int iY = y + (i / 9) * 16 - 11 + 8;
                    ItemStack stack = nonnulllist.get(i);

                    itemRender.renderItemAndEffectIntoGUI(stack, iX, iY);
                    itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, stack, iX, iY, null);
                }
                
                RenderHelper.disableStandardItemLighting();
                itemRender.zLevel = 0.0F;

                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableRescaleNormal();
            }
        }
    }

    public static void tooltipMap(ItemStack itemStack, int x, int y, CallbackInfo callbackInfo) {
        if (!itemStack.isEmpty && itemStack.getItem() instanceof ItemMap) {
            MapData mapData = ((ItemMap) itemStack.getItem()).getMapData(itemStack, mc.world);

            if (mapData != null) {
                callbackInfo.cancel();

                GlStateManager.pushMatrix();
                GlStateManager.color(1f, 1f, 1f);

                int xl = x + 6;
                int yl = y + 6;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();

                GlStateManager.translate(xl, yl, 0.0);
                GlStateManager.scale(1f, 1f, 0f);
                mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
                RenderHelper.disableStandardItemLighting();
                GL11.glDepthRange(0, 0.01);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
                bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
                bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
                bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
                tessellator.draw();
                GL11.glDepthRange(0, 1.0);
                RenderHelper.enableStandardItemLighting();
                GlStateManager.disableDepth();
                GL11.glDepthRange(0, 0.01);
                mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
                GL11.glDepthRange(0, 1.0);
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            }
        }
    }
}
