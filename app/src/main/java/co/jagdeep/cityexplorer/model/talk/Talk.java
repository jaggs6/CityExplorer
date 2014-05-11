package co.jagdeep.cityexplorer.model.talk;

import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

/**
 * Created by Alicia on 10/05/14.
 */
public class Talk {

	public final TextToSpeech tts;
	public final Place place;
	private Handler handler;
	public Boolean hasSpoken = false;

	public Talk(TextToSpeech tts, Place place, Handler handler) {
		this.tts = tts;
		this.place = place;
		this.handler = handler;
	}

	public void sayTitle() {
		speak(place.getTitle());
	}

	public void sayTitleAndDistance(Location currentLocation) {
		speak(place.getTitle() + "." + findDistance(currentLocation));
	}

	public void sayShortDescription() {
		speak(place.getShortDescription());
	}

	public void sayDistance(Location currentLocation) {

		String distanceStr = findDistance(currentLocation);
		speak(distanceStr);
	}

	public String findDistance(Location currentLocation) {
		String result = "";
		int distanceMeters = calculateDistance(place.getPosition(), currentLocation);
		if (distanceMeters <= 30) {
			result += distanceMeters + " meters";
		} else if (distanceMeters > 30 && distanceMeters < 100) {
			result += (Math.round(distanceMeters / 10d) * 10) + " meters";
		} else if (distanceMeters >= 100 && distanceMeters < 1000) {
			result += (Math.round(distanceMeters / 10d)) / 10 + "00 meters";
		} else if (distanceMeters >= 1000) {
			result += distanceMeters / 100 + " Kilometers";
		} else {
			result += distanceMeters + " meters";
		}
		return result + " away";
	}

	public void sayLongDescription() {
		speak(place.getLongDescription());
	}

	public void stopTalking() {
		tts.stop();
	}

	public void playSound() {
		ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 400);
		toneG.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 300);
	}

	private void speak(String say) {
		if (!hasSpoken) {
			tts.speak(say, TextToSpeech.QUEUE_ADD, null);
			hasSpoken = true;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					hasSpoken = false;
				}
			}, 10000);
		}
	}

	public static int calculateDistance(Location firstLocation, Location secondLocation) {
		float distance = firstLocation.distanceTo(secondLocation);
		return (int) Math.round(distance);
	}
}
