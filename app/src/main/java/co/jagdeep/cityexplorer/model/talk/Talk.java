package net.as93.text2speech;

import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.speech.tts.TextToSpeech;

/**
 * Created by Alicia on 10/05/14.
 */
public class Talk {

    public final TextToSpeech tts;
    public final Block place;

    public Talk(TextToSpeech tts, Block place){
        this.tts = tts;
        this.place = place;
    }


    public void sayTitleAndShortDesciption(){
        speak(place.getTitle());
    }

    public void sayShortDescription(){
        speak(place.getShortDescription());
    }

    public void sayDistance(Location currentLocation){
        int distance = calculateDistance(place.getPosition(), currentLocation);
        speak(distance+" meters away ");
    }

    public void sayLongDescription(){
        speak(place.getLongDescription());
    }

    public void stopTalking(){
        tts.stop();
    }

    public void playSound(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 400);
        toneG.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 300);
    }

    private void speak(String say){
        tts.speak(say, TextToSpeech.QUEUE_FLUSH, null);
    }

    private int calculateDistance(Location firstLocation, Location secondLocation){
        float distance = firstLocation.distanceTo(secondLocation);
        return (int)Math.round(distance);
    }
}
