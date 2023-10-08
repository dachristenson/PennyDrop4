package com.example.pennydrop4.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.pennydrop4.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        view.findViewById<TextView>(R.id.about_credits)?.apply {
            // Make the links clickable
            this.movementMethod = LinkMovementMethod.getInstance()
        }

        view.findViewById<TextView>(R.id.about_icon_credits)?.apply {
            val spannableString = SpannableString(getString(R.string.penny_drop_icons))

            spannableString.setSpan(
                URLSpan("https://pictogrammers.com/library/mdi/icon/bitcoin/"),
                4,
                8,
                0
            )

            spannableString.setSpan(
                URLSpan("https://pictogrammers.com/library/mdi/icon/dice-6/"),
                13,
                26,
                0
            )

            spannableString.setSpan(
                URLSpan("https://pictogrammers.com/library/mdi/"),
                46,
                67,
                0
            )

            this.text = spannableString

            // Make links clickable
            this.movementMethod = LinkMovementMethod.getInstance()
        }

        return view
    }
}