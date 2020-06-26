package com.spleefleague.core.utils.recording;

import com.spleefleague.entitybuilder.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class FinishedRecordingData implements RecordingData {

    private final List<StateChange> stateChanges;

    public FinishedRecordingData(List<StateChange> stateChanges) {
        this.stateChanges = stateChanges;
    }

    public List<StateChange> getStateChanges() {
        return stateChanges;
    }
}
