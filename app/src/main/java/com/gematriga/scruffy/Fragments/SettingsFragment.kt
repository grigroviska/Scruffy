package com.gematriga.scruffy.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.gematriga.scruffy.R
import com.gematriga.scruffy.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var category: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        category = binding.languages

        val languagesList: MutableList<String> = mutableListOf("Turkish","English","German")

        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,languagesList)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

        category.adapter = adapter


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}