package midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

public class MidiInputHandler {

    private MidiDevice device;
    private Transmitter transmitter;
    private MidiNoteListener listener;

    public void setNoteListener(MidiNoteListener listener) {
        this.listener = listener;
    }

    public boolean startListening() {
        try {
            MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();

            for (MidiDevice.Info info : devices) {
                MidiDevice currentDevice = MidiSystem.getMidiDevice(info);

                if (info.getName().contains("Roland Digital Piano")
                        && currentDevice.getMaxTransmitters() != 0) {

                    device = currentDevice;
                    device.open();

                    transmitter = device.getTransmitter();

                    transmitter.setReceiver(new Receiver() {

                        @Override
                        public void send(MidiMessage message, long timeStamp) {

                            if (message instanceof ShortMessage) {
                                ShortMessage sm = (ShortMessage) message;

                                int command = sm.getCommand();
                                int data1 = sm.getData1();
                                int data2 = sm.getData2();

                                if (command == ShortMessage.NOTE_ON && data2 > 0) {
                                    if (listener != null) {
                                        listener.onNoteOn(data1, data2);
                                    }
                                }

                                if (command == ShortMessage.NOTE_OFF ||
                                        (command == ShortMessage.NOTE_ON && data2 == 0)) {
                                    if (listener != null) {
                                        listener.onNoteOff(data1);
                                    }
                                }

                                if (command == ShortMessage.CONTROL_CHANGE) {
                                    if (listener != null) {
                                        listener.onControlChange(data1, data2);
                                    }
                                }
                            }
                        }

                        @Override
                        public void close() {
                        }
                    });

                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void stopListening() {
        if (transmitter != null) {
            transmitter.close();
        }

        if (device != null && device.isOpen()) {
            device.close();
        }
    }
}