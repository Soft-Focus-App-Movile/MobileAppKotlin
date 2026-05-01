package com.softfocus.features.therapy.data.remote

import android.util.Log
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.softfocus.core.data.local.UserSession
import com.softfocus.features.therapy.data.models.response.TherapyChatResponseDto
import com.softfocus.features.therapy.domain.models.ChatMessage
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit

class SignalRService(
    private val userSession: UserSession
) {

    private var hubConnection: HubConnection? = null
    private val gson = Gson()
    private var isManuallyStopping = false

    fun initConnection() {
        val token = userSession.getUser()?.token ?: run {
            Log.e("SignalRService", "No se pudo iniciar SignalR: Token es nulo.")
            return
        }

        val hubUrl = "http://32.194.77.233:5000/chatHub"

        try {
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .withAccessTokenProvider(Single.defer {
                    val cleanToken = token.removePrefix("Bearer ")
                    Single.just(cleanToken)
                })
                .build()

            Log.d("SignalRService", "HubConnection inicializado para $hubUrl")

            hubConnection?.onClosed { exception ->
                if (isManuallyStopping) {
                    Log.d("SignalRService", "Conexión cerrada manualmente.")
                    isManuallyStopping = false
                    return@onClosed
                }

                if (exception != null) {
                    Log.w("SignalRService", "Conexión perdida: ${exception.message}. Reconectando en 5s...")
                    try {
                        Thread.sleep(5000)
                        startConnection()
                    } catch (e: InterruptedException) {
                        Log.e("SignalRService", "Espera de reconexión interrumpida", e)
                    }
                } else {
                    Log.d("SignalRService", "Conexión cerrada limpiamente.")
                }
            }

        } catch (e: Exception) {
            Log.e("SignalRService", "Error al construir HubConnection: ${e.message}", e)
        }
    }

    fun startConnection(onConnected: () -> Unit = {}) {
        if (hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection?.start()?.blockingAwait(5, TimeUnit.SECONDS)
                Log.d("SignalRService", "SignalR Conectado. ID: ${hubConnection?.connectionId}")
                onConnected()
            } catch (e: Exception) {
                Log.e("SignalRService", "Error al iniciar conexión SignalR: ${e.message}", e)
            }
        } else {
            Log.d("SignalRService", "SignalR ya conectado o conectando.")
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                onConnected()
            }
        }
    }

    fun stopConnection() {
        isManuallyStopping = true
        hubConnection?.stop()
        Log.d("SignalRService", "SignalR Desconectado.")
    }

    fun registerMessageHandler(onMessageReceived: (ChatMessage) -> Unit) {
        if (hubConnection == null) {
            Log.e("SignalRService", "HubConnection no inicializado. Llama a initConnection() primero.")
            return
        }

        hubConnection?.on(
            "ReceiveMessage",
            { messageMap: Map<*, *> ->
                try {
                    Log.d("SignalRService", "Mensaje objeto recibido: $messageMap")

                    // Convertimos el Map de vuelta a JSON string para que Gson pueda
                    // deserializarlo correctamente al DTO tipado.
                    val messageJson = gson.toJson(messageMap)
                    Log.d("SignalRService", "Mensaje JSON reconvertido: $messageJson")

                    val dto = gson.fromJson(messageJson, TherapyChatResponseDto::class.java)
                    val chatMessage = dto.toDomain(userSession.getUser()?.id ?: "")
                    onMessageReceived(chatMessage)

                } catch (e: Exception) {
                    Log.e("SignalRService", "Error al deserializar mensaje SignalR: ${e.message}", e)
                }
            },
            // FIX: Map::class.java en lugar de String::class.java
            Map::class.java
        )

        Log.d("SignalRService", "Listener 'ReceiveMessage' registrado (tipo Map).")
    }

    private fun TherapyChatResponseDto.toDomain(currentUserId: String): ChatMessage {
        return ChatMessage(
            id = this.id,
            relationshipId = this.relationshipId,
            senderId = this.senderId,
            receiverId = this.receiverId,
            content = this.content.value,
            timestamp = this.timestamp,
            isFromMe = this.senderId == currentUserId,
            messageType = this.messageType
        )
    }
}