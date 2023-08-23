package com.example.pennydrop4.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pennydrop4.R
import com.example.pennydrop4.databinding.FragmentPickPlayersBinding
import com.example.pennydrop4.viewmodels.PickPlayersViewModel
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass.
 * Use the [PickPlayersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PickPlayersFragment : Fragment() {
    private val pickPlayersViewModel by activityViewModels<PickPlayersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPickPlayersBinding
            .inflate(inflater, container, false)
            .apply {
                this.vm = pickPlayersViewModel
            }

        return binding.root
    }
}