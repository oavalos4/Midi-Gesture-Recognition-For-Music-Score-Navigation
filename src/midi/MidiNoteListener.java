package midi;

public interface MidiNoteListener {

    void onNoteOn(int note, int velocity);

    void onNoteOff(int note);
    
    void onControlChange(int controller, int value);
}