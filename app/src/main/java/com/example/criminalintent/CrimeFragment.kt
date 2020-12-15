package com.example.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import com.example.criminalintent.databinding.FragmentCrimeBinding
import java.util.*

const val ARG_CRIME_ID = "crime_id"

class CrimeFragment : Fragment() {
    private var _binding: FragmentCrimeBinding? = null
    private val binding: FragmentCrimeBinding get() = _binding!!
    private val DIALOG_DATE = "DialogDate"
    private val REQUEST_DATE = 0
    private val DIALOG_DELETE = "DialogDelete"
    private val REQUEST_DELETE = 1
    private val REQUEST_CONTACT = 2
    private lateinit var crime: Crime
    private lateinit var crimeLab: CrimeLab

    companion object {
        fun newInstance(id: UUID): CrimeFragment = CrimeFragment().apply {
            this.arguments = bundleOf(
                ARG_CRIME_ID to id
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crimeLab = CrimeLab.getInstance(requireActivity().applicationContext)
        crime = crimeLab[requireArguments()[ARG_CRIME_ID] as UUID]!!
    }

    override fun onPause() {
        super.onPause()
        crimeLab.updateCrime(crime)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeBinding.inflate(inflater, container, false)
        binding.crimeTitle.setText(crime.title)
        binding.crimeTitle.doAfterTextChanged { text -> crime.title = text.toString() }
        binding.crimeDate.text = crime.date.toString()
        binding.crimeDate.setOnClickListener {
            DatePickerFragment.newInstance(crime.date)
                .also { it.setTargetFragment(this, REQUEST_DATE) }
                .show(parentFragmentManager, DIALOG_DATE)
        }
        binding.crimeSolved.isChecked = crime.solved
        binding.crimeSolved.setOnCheckedChangeListener { _, b -> crime.solved = b }
        binding.deleteButton.setOnClickListener {
            DeleteDialogFragment.newInstance(crime.id)
                .also { it.setTargetFragment(this, REQUEST_DELETE) }
                .show(parentFragmentManager, DIALOG_DELETE)
        }
        binding.sendButton.setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(
                            Intent.EXTRA_SUBJECT,
                            getString(R.string.crime_report_subject)
                        )
                        .putExtra(Intent.EXTRA_TEXT, getCrimeReport()),
                    getString(R.string.send_report)
                )
            )
        }
        val pickContactIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.Contacts.CONTENT_URI
        )
        binding.suspectButton.setOnClickListener {
            startActivityForResult(pickContactIntent, REQUEST_CONTACT)
        }
        if (crime.suspect != null) {
            binding.suspectButton.text = crime.suspect
        }
        if (requireActivity().packageManager.resolveActivity(
                pickContactIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            ) == null
        ) {
            binding.suspectButton.isEnabled = false
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null || resultCode != Activity.RESULT_OK)
            return
        when (requestCode) {
            REQUEST_DATE -> {
                crime.date = data.extras?.get(DatePickerFragment.EXTRA_DATE) as Date
                binding.crimeDate.text = crime.date.toString()
            }
            REQUEST_DELETE -> {
                crimeLab.deleteCrime(crime.id)
                requireActivity().finish()
            }
            REQUEST_CONTACT -> {
                val contactUri = data.data!!
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = requireActivity().contentResolver.query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null
                )
                cursor.use {
                    if (it == null || it.count == 0) return
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    binding.suspectButton.text = suspect
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCrimeReport(): String {
        val solvedString =
            if (crime.solved) {
                getString(R.string.crime_report_solved)
            } else {
                getString(R.string.crime_report_unsolved)
            }
        val dateString = DateFormat.format("EEE, MMM dd", crime.date).toString()
        val suspect = if (crime.suspect == null) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

}