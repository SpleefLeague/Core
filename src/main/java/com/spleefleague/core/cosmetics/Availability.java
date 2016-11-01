package com.spleefleague.core.cosmetics;

import com.google.common.collect.Lists;
import com.spleefleague.core.utils.UtilChat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public enum Availability {
    
    BUYABLE(Collections.emptyList()),
    UNAVAILABLE(Lists.newArrayList(
            "&cThis item is currently",
            "&cunable to obtain."
    )),
    DISABLED(Collections.emptyList());
    
    private final List<String> description;
    
    Availability(List<String> desctiption) {
        this.description = desctiption.stream().map(UtilChat::c).collect(Collectors.toList());
    }
    
    public List<String> getDescription() {
        return this.description;
    }

}
