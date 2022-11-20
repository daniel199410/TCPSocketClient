package com.example.socket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socket.databinding.FragmentFirstBinding
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket

class FirstFragment : Fragment() {
    private var active = false
    private var _binding: FragmentFirstBinding? = null
    private lateinit var connection: Socket
    private lateinit var selectorManager: SelectorManager
    private lateinit var output: ByteWriteChannel
    private lateinit var input: ByteReadChannel

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonFirst.setOnClickListener {
            if (binding!!.buttonFirst.text == getString(R.string.connect)) {
                binding!!.buttonFirst.text = getString(R.string.disconnect)
                active = true
                CoroutineScope(Dispatchers.IO).launch {
                    connect()
                }
            } else {
                active = false
                binding!!.buttonFirst.text = getString(R.string.connect)
                close()
            }
        }
        binding!!.buttonSend.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                send()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun send() {
        if(!binding?.editTextTextMultiLine?.text?.trim().isNullOrEmpty()) {
            output.writeStringUtf8("${binding!!.editTextTextMultiLine.text.trim()}\n")
            binding!!.editTextTextMultiLine.setText("")
        }
    }

    private suspend fun connect() {
        selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect(
            binding!!.editTextIp.text.toString(),
            binding!!.editTextPort.text.toString().toInt()
        )
        input = socket.openReadChannel()
        output = socket.openWriteChannel(autoFlush = true)
        while (true) {
            val message = input.readUTF8Line(128)
            if(message != null) {
                binding?.textviewFirst?.append(message)
            } else {
                close()
            }
        }
    }

    private fun close() {
        connection.close()
        selectorManager.close()
    }
}