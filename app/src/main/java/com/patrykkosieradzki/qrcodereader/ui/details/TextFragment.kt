package com.patrykkosieradzki.qrcodereader.ui.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.patrykkosieradzki.qrcodereader.R

class TextFragment : Fragment() {

    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            text = arguments!!.getString(param)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_text, container, false)


        return rootView
    }

    companion object {
        private const val param = "text"

        fun newInstance(text: String): TextFragment {
            val fragment = TextFragment()
            val args = Bundle()
            args.putString(param, text)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
