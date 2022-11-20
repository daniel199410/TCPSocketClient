package com.example.socket.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socket.R
import com.example.socket.databinding.FragmentFirstBinding
import com.example.socket.presentation.MessageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {
    private var active = false
    private var _binding: FragmentFirstBinding? = null
    private val messageViewModel: MessageViewModel by viewModels()

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
                    messageViewModel.connect(
                        binding!!.editTextIp.text.toString(),
                        binding!!.editTextPort.text.toString().toInt()
                    )
                }
            } else {
                active = false
                binding!!.buttonFirst.text = getString(R.string.connect)
                messageViewModel.close()
            }
        }
        binding!!.buttonSend.setOnClickListener {
            val message = binding!!.editTextTextMultiLine.text.toString()
            binding!!.textviewFirst.append("Cliente: $message\n")
            binding!!.editTextTextMultiLine.setText("")
            CoroutineScope(Dispatchers.IO).launch {
                messageViewModel.send(message)
            }
        }
        messageViewModel.receivedMessage.observe(viewLifecycleOwner) { message ->
            binding!!.textviewFirst.append("Server: $message\n")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}