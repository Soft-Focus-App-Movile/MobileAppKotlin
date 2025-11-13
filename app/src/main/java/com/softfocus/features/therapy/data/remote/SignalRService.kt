package com.softfocus.features.therapy.data.remote

import android.util.Log
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.softfocus.core.data.local.UserSession
import com.softfocus.core.networking.ApiConstants
import com.softfocus.features.therapy.data.models.response.TherapyChatResponseDto
import com.softfocus.features.therapy.domain.models.ChatMessage
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SignalRService(
    private val userSession: UserSession
) {

    private var hubConnection: HubConnection? = null
    private val gson = Gson()
    private var isManuallyStopping = false // Para evitar reconexión si cerramos a propósito

    // Función para construir y configurar la conexión
    fun initConnection() {
        // Obtenemos el token de la sesión
        val token = userSession.getUser()?.token ?: run {
            Log.e("SignalRService", "No se pudo iniciar SignalR: Token es nulo.")
            return
        }

        // Construimos la URL del Hub. Asegúrate que "chathub" coincide con tu backend
        val hubUrl = ApiConstants.BASE_URL + "chathub"

        try {
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .withAccessTokenProvider(Single.defer {
                    // SignalR necesita el token LIMPIO, sin el prefijo "Bearer ".
                    val cleanToken = token.removePrefix("Bearer ")
                    Single.just(cleanToken)
                })
                .build()

            Log.d("SignalRService", "HubConnection inicializado para $hubUrl")

            hubConnection?.onClosed { exception ->
                if (isManuallyStopping) {
                    Log.d("SignalRService", "Conexión cerrada manualmente.")
                    isManuallyStopping = false // Resetea el flag
                    return@onClosed
                }

                if (exception != null) {
                    Log.w("SignalRService", "Conexión perdida por error: ${exception.message}. Intentando reconectar en 5s...")
                    try {
                        // Espera 5 segundos antes de reintentar
                        Thread.sleep(5000)
                        startConnection()
                    } catch (e: InterruptedException) {
                        Log.e("SignalRService", "Espera de reconexión interrumpida", e)
                    }
                } else {
                    Log.d("SignalRService", "Conexión cerrada limpiamente (sin error).")
                }
            }

        } catch (e: Exception) {
            Log.e("SignalRService", "Error al construir HubConnection: ${e.message}", e)
        }
    }

    // Función para iniciar la conexión
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
            Log.d("SignalRService", "SignalR ya estaba conectado o conectando.")
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                onConnected()
            }
        }
    }

    // Función para detener la conexión
    fun stopConnection() {
        isManuallyStopping = true // <- Avisa que la detención es intencional
        hubConnection?.stop()
        Log.d("SignalRService", "SignalR Desconectado.")
    }

    /**
     * Registra el listener para el evento "ReceiveMessage".
     */
    fun registerMessageHandler(onMessageReceived: (ChatMessage) -> Unit) {
        if (hubConnection == null) {
            Log.e("SignalRService", "HubConnection no inicializado. Llama a initConnection() primero.")
            return
        }

        hubConnection?.on(
            "ReceiveMessage",
            { messageJson: String ->
                try {
                    Log.d("SignalRService", "Mensaje JSON recibido: $messageJson")

                    val dto = gson.fromJson(messageJson, TherapyChatResponseDto::class.java)

                    val chatMessage = dto.toDomain(userSession.getUser()?.id ?: "")

                    onMessageReceived(chatMessage)

                } catch (e: Exception) {
                    Log.e("SignalRService", "Error al deserializar mensaje SignalR: ${e.message}", e)
                }
            },
            String::class.java
        )

        Log.d("SignalRService", "Listener 'ReceiveMessage' registrado.")
    }

    // Mapeador simple de DTO a Dominio
    private fun TherapyChatResponseDto.toDomain(currentUserId: String): ChatMessage {
        return ChatMessage(
            id = this.id,
            relationshipId = this.relationshipId,
            senderId = this.senderId,
            receiverId = this.receiverId,
            content = this.content.value, // Mapeo clave
            timestamp = this.timestamp,
            isFromMe = this.senderId == currentUserId,
            messageType = this.messageType
        )
    }
}