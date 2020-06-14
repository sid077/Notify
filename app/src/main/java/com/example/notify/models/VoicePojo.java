package com.example.notify.models;

import android.speech.tts.Voice;

public class VoicePojo {
    public VoicePojo() {
        this.isLocked =false;
    }

    Voice voice;

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    Boolean isLocked;
}
