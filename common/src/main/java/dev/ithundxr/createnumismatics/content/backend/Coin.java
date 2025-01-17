package dev.ithundxr.createnumismatics.content.backend;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import dev.ithundxr.createnumismatics.registry.NumismaticsIcons;
import dev.ithundxr.createnumismatics.registry.NumismaticsItems;
import dev.ithundxr.createnumismatics.util.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Locale;

import static dev.ithundxr.createnumismatics.registry.NumismaticsIcons.*;

/*
64 spurs to a cog
8 bevels to a cog
4 sprockets to a cog
8 cogs to a crown
8 crowns to a sun
 */
public enum Coin implements INamedIconOptions {
    SPUR(1, Rarity.COMMON, I_COIN_SPUR, "\uF011"),
    BEVEL(8, Rarity.COMMON, I_COIN_BEVEL, "\uF012"), // 8 spurs
    SPROCKET(16, Rarity.COMMON, I_COIN_SPROCKET, "\uF013"), // 16 spurs, 2 bevels
    COG(64, Rarity.UNCOMMON, I_COIN_COG, "\uF014"), // 64 spurs, 8 bevels, 4 sprockets
    CROWN(512, Rarity.RARE, I_COIN_CROWN, "\uF015"), // 512 spurs, 64 bevels, 32 sprockets, 8 cogs
    SUN(4096, Rarity.EPIC, I_COIN_SUN, "\uF016") // 4096 spurs, 512 bevels, 256 sprockets, 64 cogs, 8 crowns
    ;

    public final int value; // in terms of spurs
    public final Rarity rarity;
    public final NumismaticsIcons icon;
    public final String fontChar;

    Coin(int value, Rarity rarity, NumismaticsIcons icon, String fontChar) {
        this.value = value;
        this.rarity = rarity;
        this.icon = icon;
        this.icon.setCoin();
        this.fontChar = fontChar;
    }

    public static List<Component> labeledComponents() {
        Component[] tmp = new Component[values().length];
        for (Coin coin : values()) {
            tmp[coin.ordinal()] = Components.literal(coin.fontChar + " "
                + Components.translatable(coin.getTranslationKey()).getString());
        }
        return ImmutableList.copyOf(tmp);
    }

    /**
     * Convert this coin to spurs
     * @param amount Number of this coin
     * @return Number of spurs
     */
    public int toSpurs(int amount) {
        return amount * value;
    }

    /**
     * Convert spurs to this coin
     * @param amount Number of spurs
     * @return Couple of (amount of this coin, remainder of spurs)
     */
    public Couple<Integer> convert(int amount) {
        if (this == SPUR) return Couple.create(amount, 0);
        int remainder = amount % value;
        int converted = (amount - remainder) / value;
        return Couple.create(converted, remainder);
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getTranslatedName() {
        return Components.translatable(getTranslationKey()).getString().toLowerCase(Locale.ROOT);
    }

    public String getName(int amount) {
        return getTranslatedName() + (amount != 1 ? "s" : "");
    }

    public String getDisplayName() {
        return TextUtils.titleCaseConversion(getName());
    }

    @Override
    public AllIcons getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return "item.numismatics." + getName();
    }

    public Coin getDescription() {
        return switch (this) {
            case SPUR, BEVEL, SPROCKET -> SPUR;
            case COG, CROWN, SUN -> COG;
        };
    }

    public ItemStack asStack() {
        return asStack(1);
    }

    public ItemStack asStack(int amount) {
        return NumismaticsItems.getCoin(this).asStack(amount);
    }

    public static Coin closest(int value) {
        Coin closest = Coin.SPUR;

        for (Coin coin : values()) {
            if (Math.abs(coin.value - value) <= Math.abs(closest.value - value))
                closest = coin;
        }
        return closest;
    }
}
