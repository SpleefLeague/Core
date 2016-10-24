package com.spleefleague.core.command.commands;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.DatabaseConnection;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class grantcurrency extends BasicCommand {

    public grantcurrency(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length != 3) {
            sendUsage(p);
            return;
        }
        int amount = 0;
        try {
            amount = Integer.parseInt(args[2]);
        }catch(NumberFormatException ex) {
            error(p, "The number of coins/credits must be a non-zero integer.");
            return;
        }
        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
        String currencyType = args[1].toLowerCase();
        if(targetPlayer != null) {
            SLPlayer targetSlPlayer = SpleefLeague.getInstance().getPlayerManager().get(targetPlayer);
            switch(currencyType) {
                case "coin":
                case "coins":
                    targetSlPlayer.changeCoins(amount);
                    if(amount > 0) {
                        success(p, "You've just granted " + amount + " coins to " + targetPlayer.getName() + ".");
                    }else {
                        success(p, "You've just took " + -amount + " coins from " + targetPlayer.getName() + ".");
                    }
                    success(p, "Now this player has exactly " + targetSlPlayer.getCoins() + " coins.");
                    break;
                case "credit":
                case "credits":
                case "premiumcredit":
                case "premiumcredits":
                    targetSlPlayer.changePremiumCredits(amount);
                    if(amount > 0) {
                        success(p, "You've just granted " + amount + " premium credits to " + targetPlayer.getName() + ".");
                    }else {
                        success(p, "You've just took " + -amount + " premium credits from " + targetPlayer.getName() + ".");
                    }
                    success(p, "Now this player has exactly " + targetSlPlayer.getPremiumCredits()+ " premium credits.");
                    break;
                default:
                    error(p, "You specified unknown type of currency. Possible ones are: coins, credits.");
                    break;
            }
        }else {
            grantCurrencyOffline(p, args[0], currencyType, amount);
        }
    }
    
    private void grantCurrencyOffline(Player p, String target, String currencyType, int amount) {
        switch(currencyType) {
            case "coin":
            case "coins":
                updateCurrencyInDatabase(target, "coins", amount);
                if(amount > 0) {
                    success(p, "You've just granted " + amount + " coins to " + target + " (at least we tried to do so in offline mode).");
                }else {
                    success(p, "You've just took " + -amount + " coins from " + target + " (at least we tried to do so in offline mode).");
                }
                break;
            case "credit":
            case "credits":
            case "premiumcredit":
            case "premiumcredits":
                updateCurrencyInDatabase(target, "premiumCredits", amount);
                if(amount > 0) {
                    success(p, "You've just granted " + amount + " premium credits to " + target + " (at least we tried to do so in offline mode).");
                }else {
                    success(p, "You've just took " + -amount + " premium credits from " + target + " (at least we tried to do so in offline mode).");
                }
                break;
            default:
                error(p, "You specified unknown type of currency. Possible ones are: coins, credits.");
                break;
        }
    }
    
    private void updateCurrencyInDatabase(String targetName, String currencyFieldName, int amount) {
        MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Players");
        Document index = new Document("username", targetName);
        DatabaseConnection.updateFields(collection, index, Pair.<String, Object>of(currencyFieldName, amount));
    }

}
