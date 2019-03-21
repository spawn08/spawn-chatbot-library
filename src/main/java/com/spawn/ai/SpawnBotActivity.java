package com.spawn.ai;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spawn.ai.adapters.SpawnChatbotAdapter;
import com.spawn.ai.databinding.ActivitySpawnBotBinding;
import com.spawn.ai.interfaces.IBotObserver;
import com.spawn.ai.model.BotResponse;
import com.spawn.ai.model.ChatMessageType;
import com.spawn.ai.network.WebServiceUtils;
import com.spawn.ai.utils.DateTimeUtils;
import com.spawn.ai.utils.JsonFileReader;

import java.util.ArrayList;
import java.util.Locale;

import constants.ChatViewTypes;

public class SpawnBotActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener, IBotObserver, TextToSpeech.OnInitListener {

    private static final String TAG = SpawnBotActivity.class.getCanonicalName();
    public Context context;
    private ActivitySpawnBotBinding activitySpawnBotBinding;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntentDispatcher;
    private Locale locale;
    private boolean isSpeechEnd = false;
    private CountDownTimer countDownTimer;
    private boolean isSpeechEnabled = false;
    private ArrayList<ChatMessageType> botResponses;
    private SpawnChatbotAdapter chatbotAdapter;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activitySpawnBotBinding = DataBindingUtil.setContentView(this, R.layout.activity_spawn_bot);
        activitySpawnBotBinding.setListener(this);
        locale = new Locale("en");
        requestPermission();
        activitySpawnBotBinding.mic.setOnClickListener(this);
        activitySpawnBotBinding.micImage.setOnClickListener(this);
        botResponses = new ArrayList<ChatMessageType>();
        chatbotAdapter = new SpawnChatbotAdapter(this, botResponses);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        activitySpawnBotBinding.chatRecycler.setLayoutManager(linearLayoutManager);
        activitySpawnBotBinding.chatRecycler.setAdapter(chatbotAdapter);
        textToSpeech = new TextToSpeech(this, this);

        initSpeech();

        activitySpawnBotBinding.mic.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isSpeechEnd) {
                    activitySpawnBotBinding.mic.invalidate();
                    activitySpawnBotBinding.mic.cancelAnimation();
                    activitySpawnBotBinding.micImage.setVisibility(View.VISIBLE);
                    activitySpawnBotBinding.mic.setVisibility(View.GONE);
                } else {
                    activitySpawnBotBinding.mic.playAnimation();
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SpawnBotActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET},
                1);
    }

    private void initSpeech() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechIntentDispatcher = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("en"));
        speechIntentDispatcher.putExtra("android.speech.extra.DICTATION_MODE", true);
        speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            speechIntentDispatcher.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);
        }

        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isSpeechEnabled = true;
                } else {
                    isSpeechEnabled = false;
                    Toast.makeText(this, "Permission for speech input is disabled", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onReadyForSpeech");
        isSpeechEnd = false;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
        isSpeechEnd = true;
        activitySpawnBotBinding.mic.invalidate();
        activitySpawnBotBinding.mic.cancelAnimation();
        activitySpawnBotBinding.micImage.setVisibility(View.VISIBLE);
        activitySpawnBotBinding.mic.setVisibility(View.GONE);

    }

    @Override
    public void onError(int i) {
        Log.d(TAG, "ERROR " + i);
        switch (i) {
            case SpeechRecognizer.ERROR_NETWORK:
                onEndOfSpeech();
                Toast.makeText(this, "No Network", Toast.LENGTH_LONG);
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                onEndOfSpeech();
                Toast.makeText(this, "No permission to perform the action", Toast.LENGTH_LONG);
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                onEndOfSpeech();
                break;
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        if (bundle != null && bundle.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            activitySpawnBotBinding.mic.cancelAnimation();
            activitySpawnBotBinding.mic.invalidate();
            ArrayList<String> returnSpeech = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String speechString = returnSpeech.get(0);
            onEndOfSpeech();
            callWitService(speechString);
            Log.d(getClass().getCanonicalName(), "Speech :" + speechString);

        }
    }

    private void callWitService(String speechString) {
        WebServiceUtils.getInstance(this).setUpObserver(this);
        WebServiceUtils.getInstance(this).getRetrofitClient();
        WebServiceUtils.getInstance(this).getBotResponse(speechString);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        if (countDownTimer != null)
            countDownTimer.cancel();
        Log.d(TAG, "onPartialResults");
        activitySpawnBotBinding.mic.playAnimation();

        if (bundle != null
                && bundle.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)
                && bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0) {
            ArrayList<String> partialResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String partialString = partialResults.get(0);
            chatViews(partialString, 0, null);
            Log.d(TAG, "partialString :" + partialString);
        }

        countDownTimer = new CountDownTimer(1000, 3000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (speechRecognizer != null)
                    speechRecognizer.stopListening();

                onEndOfSpeech();
                Log.d(TAG, "onEndOfSpeech from Countdowntimer");

            }
        }.start();
    }

    private void chatViews(String chatMessage, int type, BotResponse botResponse) {

        switch (type) {
            case ChatViewTypes.CHAT_VIEW_USER:
                ChatMessageType chatMessageType = new ChatMessageType();
                chatMessageType.setMessage(chatMessage);
                chatMessageType.setViewType(0);
                chatMessageType.setDate(new DateTimeUtils().getDate());
                chatMessageType.setBotResponse(null);
                if (botResponses.size() == 0)
                    botResponses.add(chatMessageType);
                else {
                    botResponses.remove(botResponses.size() - 1);
                    botResponses.add(chatMessageType);
                }
                chatbotAdapter.setAdapter(botResponses);

                break;

            case ChatViewTypes.CHAT_VIEW_BOT:
                if (botResponse != null) {
                    ChatMessageType chatMessageType1 = new ChatMessageType();
                    if (botResponse.getEntities().getBotIntents() != null &&
                            botResponse.getEntities().getBotIntents().size() > 0)
                        chatMessageType1.setMessage(JsonFileReader.getInstance().getJsonFromKey(botResponse.getEntities().getBotIntents().get(0).getValue().toString(), ChatViewTypes.CHAT_VIEW_BOT));
                    else
                        chatMessageType1.setMessage(JsonFileReader.getInstance().getDefaultAnswer());
                    chatMessageType1.setDate(new DateTimeUtils().getDate());
                    chatMessageType1.setViewType(1);
                    chatMessageType1.setBotResponse(null);
                    botResponses.remove(botResponses.size() - 1);
                    botResponses.add(chatMessageType1);
                    chatbotAdapter.setAdapter(botResponses);
                    chatbotAdapter.notifyDataSetChanged();

                }

                break;

            case ChatViewTypes.CHAT_VIEW_LOADING:
                ChatMessageType chatMessageLoading = new ChatMessageType();
                chatMessageLoading.setViewType(2);
                botResponses.add(chatMessageLoading);
                chatbotAdapter.setAdapter(botResponses);
                break;
        }

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.mic) {
            initSpeech();
            speechRecognizer.startListening(speechIntentDispatcher);
            activitySpawnBotBinding.micImage.setVisibility(View.GONE);
            activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
            activitySpawnBotBinding.mic.playAnimation();


        } else if (i == R.id.mic_image) {
            botResponses.clear();
            chatbotAdapter.setAdapter(botResponses);
            if (SpeechRecognizer.isRecognitionAvailable(this) && isSpeechEnabled) {
                initSpeech();
                speechRecognizer.startListening(speechIntentDispatcher);
                activitySpawnBotBinding.micImage.setVisibility(View.GONE);
                activitySpawnBotBinding.mic.setVisibility(View.VISIBLE);
                activitySpawnBotBinding.mic.playAnimation();
            } else {
                if (speechRecognizer != null) {
                    speechRecognizer.cancel();
                    speechRecognizer.destroy();
                }
                Toast.makeText(this, "Permission for speech input is disabled", Toast.LENGTH_LONG).show();
                requestPermission();

            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (speechRecognizer != null) {
                speechRecognizer.cancel();
                speechRecognizer.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notifyBotResponse(BotResponse botResponse) {
        chatViews(null, 1, botResponse);
    }

    @Override
    public void notifyBotError() {

    }

    @Override
    public void loading() {
        chatViews(null, 2, null);
    }

    @Override
    public void speakBot(String message) {
        if (Build.VERSION.SDK_INT < 21) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);

        } else {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "10000");
        }
    }


    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(this.getClass().getName(), "This Language is not supported");
            } else {
                Log.d(this.getClass().getName(), "Initilization Success!");
            }
        } else {
            Log.e(this.getClass().getName(), "Initilization Failed!");
        }
    }
}
