package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceSpellCommand {
    object ShieldOrange : VoiceSpellCommand()
    object ShieldTime : VoiceSpellCommand()
    object ShieldMirror : VoiceSpellCommand()
    object ShieldCrimson : VoiceSpellCommand()
    object OpenPortal : VoiceSpellCommand()
    object MysticWhips : VoiceSpellCommand()
    object SwitchCamera : VoiceSpellCommand()
    object ToggleTorch : VoiceSpellCommand()
    object ClearSpells : VoiceSpellCommand()
}

class VoiceIncantationManager(
    private val context: Context,
    private val onCommandDetected: (VoiceSpellCommand) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _lastHeardText = MutableStateFlow("")
    val lastHeardText: StateFlow<String> = _lastHeardText.asStateFlow()

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    fun initialize() {
        val available = SpeechRecognizer.isRecognitionAvailable(context)
        _isAvailable.value = available
        if (!available) return

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        } catch (e: Exception) {
            Log.e("VoiceIncantation", "Failed to initialize SpeechRecognizer", e)
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initialize()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e("VoiceIncantation", "Failed to start listening", e)
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _isListening.value = false
        } catch (e: Exception) {
            Log.e("VoiceIncantation", "Failed to stop listening", e)
        }
    }

    fun toggleListening() {
        if (_isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                // Automatically restart listening if still in active session
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spoken = matches[0].lowercase(Locale.ROOT).trim()
                    _lastHeardText.value = spoken
                    parseCommand(spoken)
                }
                _isListening.value = false
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spoken = matches[0].lowercase(Locale.ROOT).trim()
                    _lastHeardText.value = spoken
                    parseCommand(spoken)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun parseCommand(text: String) {
        when {
            text.contains("time") || text.contains("green") || text.contains("agamotto") -> {
                onCommandDetected(VoiceSpellCommand.ShieldTime)
            }
            text.contains("orange") || text.contains("tao") || text.contains("mandala") || text.contains("shield") -> {
                onCommandDetected(VoiceSpellCommand.ShieldOrange)
            }
            text.contains("mirror") || text.contains("dimension") || text.contains("crystal") || text.contains("glass") -> {
                onCommandDetected(VoiceSpellCommand.ShieldMirror)
            }
            text.contains("crimson") || text.contains("band") || text.contains("cyttorak") || text.contains("red") -> {
                onCommandDetected(VoiceSpellCommand.ShieldCrimson)
            }
            text.contains("portal") || text.contains("sling") || text.contains("ring") || text.contains("multiverse") || text.contains("space") -> {
                onCommandDetected(VoiceSpellCommand.OpenPortal)
            }
            text.contains("whip") || text.contains("tether") || text.contains("string") || text.contains("five") -> {
                onCommandDetected(VoiceSpellCommand.MysticWhips)
            }
            text.contains("switch") || text.contains("flip") || text.contains("camera") || text.contains("back") || text.contains("front") -> {
                onCommandDetected(VoiceSpellCommand.SwitchCamera)
            }
            text.contains("torch") || text.contains("light") || text.contains("flash") -> {
                onCommandDetected(VoiceSpellCommand.ToggleTorch)
            }
            text.contains("clear") || text.contains("stop") || text.contains("vanish") || text.contains("close") -> {
                onCommandDetected(VoiceSpellCommand.ClearSpells)
            }
        }
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
    }
}
