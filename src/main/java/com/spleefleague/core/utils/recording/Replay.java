package com.spleefleague.core.utils.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Replay {

    private static final int SUBTICK_THRESHHOLD = 10;
    private final Recording recording;
    private final Player player;
    private final int entityId;
    private int currentTick = 0;
    private int currentStateId = 0;
    private boolean cancelled = false;
    private int subtickCount = 0;
    private int speed = 10;
    private boolean paused = false;
    private final boolean ghost;

    public Replay(Recording recording, Player player, int entityId, boolean ghost) {
        this.recording = recording;
        this.player = player;
        this.entityId = entityId;
        this.ghost = ghost;
    }

    public void cancel() {
        cancelled = true;
    }

    protected void cleanup() {
        PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        container.getIntegerArrays().write(0, new int[]{entityId});
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Recording getRecording() {
        return recording;
    }

    public Player getPlayer() {
        return player;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void doTick() {
        if(paused) return;
        if(speed > 0) {
            doTickForward();
        }
        else if(speed < 0) {
            doTickBackward();
        }
    }

    public void adjustTicks(int ticks) {
        subtickCount += ticks * SUBTICK_THRESHHOLD;
        if(subtickCount > 0) {
            doTickForward();
            subtickCount -= speed;
        }
        else if(subtickCount < 0) {
            doTickBackward();
            subtickCount += speed;
        }
    }

    public void doTickForward() {
        List<StateChange> stateChanges = recording.getRecordingData().getStateChanges();
        while(subtickCount >= SUBTICK_THRESHHOLD) {
            while(currentStateId < stateChanges.size()) {
                StateChange change = stateChanges.get(currentStateId);
                if(change.getTick() == currentTick) {
                    change.applyTo(entityId, true, ghost, player);
                    currentStateId++;
                }
                else {
                    break;
                }
            }
            currentTick++;
            subtickCount -= SUBTICK_THRESHHOLD;
            if(currentStateId >= stateChanges.size()) {
                break;
            }
        }
        subtickCount += speed;
    }

    public void doTickBackward() {
        List<StateChange> stateChanges = recording.getRecordingData().getStateChanges();
        while(subtickCount <= -SUBTICK_THRESHHOLD) {
            while(currentStateId >= 0) {
                StateChange change = stateChanges.get(currentStateId);
                if(change.getTick() == currentTick) {
                    change.applyTo(entityId, false, ghost, player);
                    currentStateId--;
                }
                else {
                    break;
                }
            }
            currentTick--;
            subtickCount += SUBTICK_THRESHHOLD;
            if(currentStateId < 0) {
                break;
            }
        }
        subtickCount -= speed;
    }

    public boolean isDone() {
        return cancelled || recording.getRecordingData().getStateChanges().size() <= currentStateId;
    }
}
