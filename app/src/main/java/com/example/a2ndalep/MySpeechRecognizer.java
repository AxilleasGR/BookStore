package com.example.a2ndalep;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class MySpeechRecognizer {


    private TextToSpeech tts;
    private TextToSpeech.OnInitListener initListener=
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                }
            };

    public MySpeechRecognizer(Context context) {
        tts = new TextToSpeech(context,initListener);
    }

    public void speak(String message){
        tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);
    }
}
