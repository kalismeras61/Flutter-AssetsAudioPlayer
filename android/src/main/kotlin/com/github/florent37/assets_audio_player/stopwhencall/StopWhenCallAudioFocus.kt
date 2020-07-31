package com.github.florent37.assets_audio_player.stopwhencall

import android.content.Context
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat

class StopWhenCallAudioFocus(private val context: Context) : StopWhenCall() {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val focusLock = Any()

    private var request: AudioFocusRequestCompat? = null
    private fun generateListener() : ((Int) -> Unit) = { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN ->
                synchronized(focusLock) {
                    pingListeners(AudioState.AUTHORIZED_TO_PLAY)
                }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                synchronized(focusLock) {
                    pingListeners(AudioState.REDUCE_VOLUME)
                }
            AudioManager.AUDIOFOCUS_LOSS -> {
                synchronized(focusLock) {
                    pingListeners(AudioState.FORBIDDEN)
                }
            }
            else-> {
                synchronized(focusLock) {
                    pingListeners(AudioState.FORBIDDEN)
                }
            }
        }
    }

<<<<<<< HEAD
    override fun requestAudioFocus(): AudioState {
=======
    override fun requestAudioFocus(audioFocusStrategy: AudioFocusStrategy) : AudioState {
        if(audioFocusStrategy is AudioFocusStrategy.None)
            return AudioState.FORBIDDEN
        
        val strategy = audioFocusStrategy as AudioFocusStrategy.Request
        
>>>>>>> upstream/master
        request?.let {
            AudioManagerCompat.abandonAudioFocusRequest(audioManager, it)
        }
        
        val requestType = if(strategy.resumeOthersPlayersAfterDone){
            AudioManagerCompat.AUDIOFOCUS_GAIN_TRANSIENT
        } else {
            AudioManagerCompat.AUDIOFOCUS_GAIN
        }
        
        val listener = generateListener()
        this.request = AudioFocusRequestCompat.Builder(requestType).also {
            it.setAudioAttributes(AudioAttributesCompat.Builder().run {
                setUsage(AudioAttributesCompat.USAGE_MEDIA)
                setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                build()
            })
            it.setOnAudioFocusChangeListener(listener)
        }.build()
        val result: Int = AudioManagerCompat.requestAudioFocus(audioManager, request!!)
        synchronized(focusLock) {
            listener(result)
        }
<<<<<<< HEAD
        return when (result) {
            AudioManager.AUDIOFOCUS_GAIN -> AudioState.AUTHORIZED_TO_PLAY
=======
        return when(result){
            AudioManager.AUDIOFOCUS_GAIN, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> AudioState.AUTHORIZED_TO_PLAY
>>>>>>> upstream/master
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> AudioState.REDUCE_VOLUME
            AudioManager.AUDIOFOCUS_LOSS -> AudioState.FORBIDDEN
            else -> AudioState.FORBIDDEN
        }
    }

    override fun stop() {
        this.request?.let {
            AudioManagerCompat.abandonAudioFocusRequest(audioManager, it)
        }
    }
}