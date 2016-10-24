package com.spleefleague.core.command.commands;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.core.utils.UtilChat;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        runConsole(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length != 3) {
            sendUsage(cs);
            return;
        }
        int amount = 0;
        try {
            amount = Integer.parseInt(args[2]);
        }catch(NumberFormatException ex) {
            error(cs, "The number of coins/credits must be a non-zero integer.");
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
                        UtilChat.s(Theme.SUCCESS, cs, "You've just granted &6%d coins &ato &e%s&a.", amount, targetPlayer.getName());
                    }else {
                        UtilChat.s(Theme.SUCCESS, cs, "You've just took &6%d coins &afrom &e%s&a.", -amount, targetPlayer.getName());
                    }
                    UtilChat.s(Theme.SUCCESS, cs, "Now this player has exactly &6%d coins&a.", targetSlPlayer.getCoins());
                    break;
                case "credit":
                case "credits":
                case "premiumcredit":
                case "premiumcredits":
                    targetSlPlayer.changePremiumCredits(amount);
                    if(amount > 0) {
                        UtilChat.s(Theme.SUCCESS, cs, "You've just granted &b%d premium credits &ato &e%s&a.", amount, targetPlayer.getName());
                    }else {
                        UtilChat.s(Theme.SUCCESS, cs, "You've just took &b%d premium credits &afrom &e%s&a.", -amount, targetPlayer.getName());
                    }
                    UtilChat.s(Theme.SUCCESS, cs, "Now this player has exactly &b%d premium credits&a.", targetSlPlayer.getPremiumCredits());
                    break;
                default:
                    UtilChat.s(Theme.ERROR, cs, "You specified unknown type of currency. Possible ones are: &bcoins&c, &bcredits&c.");
                    break;
            }
        }else {
            grantCurrencyOffline(cs, args[0], currencyType, amount);
        }
    }
    
    private void grantCurrencyOffline(CommandSender cs, String target, String currencyType, int amount) {
        switch(currencyType) {
            case "coin":
            case "coins":
                if(amount > 0) {
                    UtilChat.s(Theme.SUCCESS, cs, "You've just granted &6%d coins &ato &e%s &a(at least we are trying to do so in offline mode).", amount, target);
                }else {
                    UtilChat.s(Theme.SUCCESS, cs, "You've just took &6%d coins &afrom &e%s &a(at least we are trying to do so in offline mode).", -amount, target);
                }
                DatabaseConnection.find(SpleefLeague.getInstance().getPluginDB().getCollection("Players"), new Document("username", target), result -> {
                    Document doc = result.first();
                    if(doc == null) {
                        UtilChat.s(Theme.ERROR, cs, "We can't find player with name &e%s &c):", target);
                        return;
                    }
                    int total = Math.max(0, doc.getInteger("coins") + amount);
                    updateCurrencyInDatabase(target, "coins", total);
                    UtilChat.s(Theme.SUCCESS, cs, "Now &e%s &ashould has &6%d coins&a.", target, total);
                });
                break;
            case "credit":
            case "credits":
            case "premiumcredit":
            case "premiumcredits":
                if(amount > 0) {
                    UtilChat.s(Theme.SUCCESS, cs, "You've just granted &b%d premium credits &ato &e%s &a(at least we are trying to do so in offline mode).", amount, target);
                }else {
                    UtilChat.s(Theme.SUCCESS, cs, "You've just took &b%d premium credits &afrom &e%s &a(at least we are trying to do so in offline mode).", -amount, target);
                }
                DatabaseConnection.find(SpleefLeague.getInstance().getPluginDB().getCollection("Players"), new Document("username", target), result -> {
                    Document doc = result.first();
                    if(doc == null) {
                        UtilChat.s(Theme.ERROR, cs, "We can't find player with name &e%s &c):", target);
                        return;
                    }
                    int total = Math.max(0, doc.getInteger("premiumCredits") + amount);
                    updateCurrencyInDatabase(target, "premiumCredits", total);
                    UtilChat.s(Theme.SUCCESS, cs, "Now &e%s &ashould has &b%d premium credits&a.", target, total);
                });
                break;
            default:
                UtilChat.s(Theme.ERROR, cs, "You specified unknown type of currency. Possible ones are: &bcoins&c, &bcredits&c.");
                break;
        }
    }
    
    private void updateCurrencyInDatabase(String targetName, String currencyFieldName, int amount) {
        MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Players");
        Document index = new Document("username", targetName);
        DatabaseConnection.updateFields(collection, index, Pair.<String, Object>of(currencyFieldName, amount));
    }

}
