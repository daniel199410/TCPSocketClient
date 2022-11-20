package com.example.socket.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers

class MessageViewModel: ViewModel() {
    private lateinit var connection: Socket
    private lateinit var selectorManager: SelectorManager
    private lateinit var output: ByteWriteChannel
    private lateinit var input: ByteReadChannel
    val receivedMessage = MutableLiveData<String?>()

     suspend fun connect(address: String, port: Int) {
        selectorManager = SelectorManager(Dispatchers.IO)
        connection = aSocket(selectorManager).tcp().connect(address, port)
        input = connection.openReadChannel()
        output = connection.openWriteChannel(autoFlush = true)
        while (true) {
            val message = input.readUTF8Line(128)
            if(message != null) {
                receivedMessage.postValue(message)
            } else {
                close()
            }
        }
    }

     suspend fun send(message: String) {
        output.writeStringUtf8("${message}\n")
    }

     fun close() {
        connection.close()
        selectorManager.close()
    }
}