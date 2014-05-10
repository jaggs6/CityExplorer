package net.as93.text2speech;

import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    private TextToSpeech tts; // The TextToSpeech object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Set up the text to speech object */
        tts =new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            tts.setLanguage(Locale.UK);
                        }
                    }
                });

        /* Pass block object to Talk class */
        testMethod();

    }


    private void testMethod(){
        final Block testData = makeTestData();
        final Talk talkObj = new Talk(tts, testData);

        Button cmdTalk = (Button)findViewById(R.id.cmdTalk);
        cmdTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkObj.sayTitleAndShortDesciption();

            }
        });

        Button cmdDistance = (Button)findViewById(R.id.cmdDistance);
        cmdDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location testLocation = new Location("");
                testLocation.setLatitude(51.505187d);
                testLocation.setLongitude(-0.020992d);
                talkObj.sayDistance(testLocation);

            }
        });

        Button cmdSound = (Button)findViewById(R.id.cmdSound);
        cmdSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               talkObj.playSound();

            }
        });

        Button cmdTalkShortDescription = (Button)findViewById(R.id.cmdTalkShortDescription);
        cmdTalkShortDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkObj.sayShortDescription();

            }
        });

        Button cmdTalkDescription = (Button)findViewById(R.id.cmdTalkDescription);
        cmdTalkDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkObj.sayLongDescription();

            }
        });

        Button cmdStopTalking = (Button)findViewById(R.id.cmdStopTalking);
        cmdStopTalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkObj.stopTalking();

            }
        });
    }

    private Block makeTestData(){
        Block samplePlace = new Block();
        samplePlace.setTitle("Sydney Opera House");
        Location testLocation = new Location("");
        testLocation.setLatitude(51.504927d);
        testLocation.setLongitude(-0.019652d);
        samplePlace.setPosition(testLocation);
        samplePlace.setShortDescription("The Sydney Opera House is a multi-venue performing arts centre in Sydney");
        samplePlace.setLongDescription("The Opera House’s magnificent harbourside location, stunning architecture and excellent programme of events make it Sydney’s number one destination. The modern masterpiece reflects the genius of its architect, Jørn Utzon. In 1999, Utzon agreed to prepare a guide of design principles for future changes to the building. This was welcome news for all who marvel at his masterpiece and for the four million visitors to the site each year.");
        return samplePlace;
    }

}
