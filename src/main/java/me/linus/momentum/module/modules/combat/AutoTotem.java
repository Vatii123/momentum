package me.linus.momentum.module.modules.combat;

import me.linus.momentum.module.Module;
import me.linus.momentum.setting.checkbox.Checkbox;
import me.linus.momentum.setting.slider.Slider;
import me.linus.momentum.util.world.InventoryUtil;
import me.linus.momentum.util.world.PlayerUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;

/**
 * @author linustouchtips
 * @since 11/28/2020
 */

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", Category.COMBAT, "Automatically replaces totems");
    }

    public static final Slider health = new Slider("Health", 0.1, 24.0, 36.0, 1);
    public static final Checkbox swordGap = new Checkbox("Sword Gapple", true);
    public static final Checkbox forceGap = new Checkbox("Force Gapple", true);
    public static final Checkbox hotbar = new Checkbox("Search Hotbar", false);

    @Override
    public void setup() {
        addSetting(health);
        addSetting(swordGap);
        addSetting(forceGap);
        addSetting(hotbar);
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        Item searching = Items.AIR;

        if (health.getValue() > PlayerUtil.getHealth())
            searching = Items.TOTEM_OF_UNDYING;

        else if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && swordGap.getValue())
            searching = Items.GOLDEN_APPLE;

        else if (forceGap.getValue() && Mouse.isButtonDown(1))
            searching = Items.GOLDEN_APPLE;

        if (mc.player.getHeldItemOffhand().getItem() == searching)
            return;

        if (mc.currentScreen != null)
            return;

        if (InventoryUtil.getInventoryItemSlot(searching, hotbar.getValue()) != -1) {
            InventoryUtil.moveItemToOffhand(InventoryUtil.getInventoryItemSlot(searching, hotbar.getValue()));
            return;
        }

        if (InventoryUtil.getInventoryItemSlot(searching, hotbar.getValue()) != -1)
            InventoryUtil.moveItemToOffhand(InventoryUtil.getInventoryItemSlot(searching, hotbar.getValue()));
    }

    @Override
    public String getHUDData() {
        return " " + InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING);
    }
}
