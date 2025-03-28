package me.calrl.hubbly.action.actions;

import me.calrl.hubbly.Hubbly;
import me.calrl.hubbly.action.Action;
import me.calrl.hubbly.interfaces.SubCommand;
import me.calrl.hubbly.utils.xseries.XPotion;
import net.minecraft.data.worldgen.features.EndFeatures;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectAction implements Action {
    @Override
    public String getIdentifier() {
        return "EFFECT";
    }

    @Override
    public void execute(Hubbly plugin, Player player, String data) {
        List<String> effectData = new ArrayList<>(Arrays.asList(data.split(";")));
        PotionEffect effect = XPotion.parsePotionEffectFromString(effectData.getFirst());
        int strength = Integer.parseInt(effectData.getLast());
        int duration = Integer.parseInt(effectData.get(1));

        player.addPotionEffect(new PotionEffect(effect.getType(), duration, strength));
    }
}
