package com.dicoding.agrovision.ui.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.dicoding.AgroVision.R


class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumButton: Button = view.findViewById(R.id.albumButton)
        val cameraButton: Button = view.findViewById(R.id.cameraButton)
        val checkButton: Button = view.findViewById(R.id.checkButton)

        // Setup listeners for buttons if needed
        albumButton.setOnClickListener {
            // Action for album button
        }

        cameraButton.setOnClickListener {
            // Action for camera button
        }

        checkButton.setOnClickListener {
            // Action for check button
        }
    }
}
