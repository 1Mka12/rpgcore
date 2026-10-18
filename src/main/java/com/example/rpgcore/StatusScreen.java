package com.example.rpgcore;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StatusScreen extends Screen {
    public StatusScreen() {
        super(Component.translatable("screen.rpgcore.status"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ClientEvents.STATUS_KEY.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) return;
        RpgData d = p.getData(RpgCore.RPG);

        int w = 200, h = 140;
        int x = (width - w) / 2;
        int y = (height - h) / 2;

        g.fill(x, y, x + w, y + h, 0xCC101018);
        g.renderOutline(x, y, w, h, 0xFFAAAAAA);
        g.drawCenteredString(font, title, width / 2, y + 8, 0xFFFFFF);
        g.drawString(font, p.getName(), x + 10, y + 24, 0xFFD700);

        g.drawString(font, Component.translatable("screen.rpgcore.level", d.level()),
                x + 10, y + 38, 0xFFFFFF);

        int barX = x + 10, barY = y + 52, barW = w - 20;
        float frac = Math.min(1f, d.xp() / (float) d.xpNeeded());
        g.fill(barX, barY, barX + barW, barY + 8, 0xFF303030);
        g.fill(barX, barY, barX + (int) (barW * frac), barY + 8, 0xFF3FBF3F);
        g.drawString(font, Component.translatable("screen.rpgcore.xp", d.xp(), d.xpNeeded()),
                x + 10, y + 64, 0xAAAAAA);

        String hp = String.format("%.0f / %.0f", p.getHealth(), p.getMaxHealth());
        String atk = String.format("%.1f", p.getAttributeValue(Attributes.ATTACK_DAMAGE));
        String arm = String.format("%.0f", p.getAttributeValue(Attributes.ARMOR));
        String spd = String.format("%.1f", p.getAttributeValue(Attributes.MOVEMENT_SPEED) * 10);

        g.drawString(font, Component.translatable("screen.rpgcore.health", hp), x + 10, y + 82, 0xFF5555);
        g.drawString(font, Component.translatable("screen.rpgcore.attack", atk), x + 10, y + 94, 0xFFAA00);
        g.drawString(font, Component.translatable("screen.rpgcore.armor", arm), x + 10, y + 106, 0x55AAFF);
        g.drawString(font, Component.translatable("screen.rpgcore.speed", spd), x + 10, y + 118, 0x55FFAA);
    }
}
