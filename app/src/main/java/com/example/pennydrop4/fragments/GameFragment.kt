package com.example.pennydrop4.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pennydrop4.R
import com.example.pennydrop4.databinding.FragmentGameBinding
import com.example.pennydrop4.viewmodels.GameViewModel
import androidx.fragment.app.activityViewModels

class GameFragment : Fragment() {
    private val gameViewModel by activityViewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGameBinding
            .inflate(inflater, container, false)
            .apply {
                vm = gameViewModel

                textCurrentTurnInfo.movementMethod = ScrollingMovementMethod()

                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }
}