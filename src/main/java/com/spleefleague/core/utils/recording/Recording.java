package com.spleefleague.core.utils.recording;

import com.spleefleague.core.io.typeconverters.LocationConverter;
import com.spleefleague.entitybuilder.*;
import org.bukkit.Location;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Recording extends DBEntity implements DBSaveable, DBLoadable {


    @DBLoad(fieldName = "uuid", typeConverter = TypeConverter.UUIDStringConverter.class)
    @DBSave(fieldName = "uuid", typeConverter = TypeConverter.UUIDStringConverter.class)
    private final UUID playerId;
    @DBSave(fieldName = "timestamp")
    @DBLoad(fieldName = "timestamp")
    private final Date timestamp;
    @DBSave(fieldName = "start", typeConverter = LocationConverter.class)
    @DBLoad(fieldName = "start", typeConverter = LocationConverter.class)
    private Location start;
    private RecordingData recordingData;

    public Recording(UUID playerId, Location start) {
        this.playerId = playerId;
        this.start = start;
        this.timestamp = new Date();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public RecordingData getRecordingData() {
        return recordingData;
    }

    public Location getStart() {
        return start;
    }

    protected void setRecordingData(RecordingData recordingData) {
        this.recordingData = recordingData;
    }

    @DBSave(fieldName = "stateChanges")
    private List<StateChange> getStateChanges() {
        return this.recordingData.getStateChanges();
    }

    @DBLoad(fieldName = "stateChanges", typeConverter = ActiveRecordingData.StateChangeConverter.class)
    private void setStateChanges(List<StateChange> stateChanges) {
        recordingData = new FinishedRecordingData(stateChanges);
    }
}
