package com.example.pennydrop4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pennydrop4.R

class RankingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceStates: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_rankings, container, false)

        return view
    }
}