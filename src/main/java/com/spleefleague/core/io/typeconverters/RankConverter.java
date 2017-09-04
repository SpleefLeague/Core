package com.spleefleague.core.io.typeconverters;

import com.spleefleague.core.player.Rank;
import com.spleefleague.entitybuilder.TypeConverter;

/**
 *
 * @author balsfull
 */
public class RankConverter extends TypeConverter<String, Rank> {

    @Override
    public String convertSave(Rank t) {
        return t.getName();
    }

    @Override
    public Rank convertLoad(String v) {
        return Rank.valueOf(v);
    }
}
