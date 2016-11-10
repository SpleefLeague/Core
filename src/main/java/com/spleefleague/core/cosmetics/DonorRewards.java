package com.spleefleague.core.cosmetics;

import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.UtilChat;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class DonorRewards {

    private final static int DONOR_REWARD = 2;
    private final static int DONOR_PLUS_REWARD = 3;
    private final static int DONOR_PLUS_PLUS_REWARD = 8;
    
    public static void checkWhetherShouldGetCredits(SLPlayer slp) {
        if(slp.isDonorPlusPlus())
            check(slp, DONOR_PLUS_PLUS_REWARD);
        else if(slp.isDonorPlus())
            check(slp, DONOR_PLUS_REWARD);
        else if(slp.isDonor())
            check(slp, DONOR_REWARD);
    }
    
    private static void check(SLPlayer slp, int reward) {
        int got = slp.getPremiumCreditsGotThatMonth();
        long last = slp.getPremiumCreditsLastReceptionTime();
        long current = System.currentTimeMillis();
        if(current - last > 1000l * 60 * 60 * 24 * 30) {
            slp.changePremiumCredits(reward);
            slp.setPremiumCreditsGotThatMonth(reward);
            slp.setPremiumCreditsLastReceptionTime(current);
            UtilChat.s(Theme.INFO, slp, "You have just received your monthly portion of &b%d premium credits&e!", reward);
        }else if(got < reward) {
            int delta = reward - got;
            slp.changePremiumCredits(delta);
            slp.setPremiumCreditsGotThatMonth(got);
            UtilChat.s(Theme.INFO, slp, "You have just received additional &b%d premium credits &efor buying new donor group!", delta);
        }
    }
    
}
