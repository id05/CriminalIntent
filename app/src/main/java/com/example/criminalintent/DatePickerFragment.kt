package com.example.criminalintent

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle

import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

import com.example.criminalintent.databinding.PickerDateBinding
import java.util.*

const val ARG_DATE = "argDate"

class DatePickerFragment : DialogFragment() {
    private var _binding: PickerDateBinding? = null
    private val binding: PickerDateBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = PickerDateBinding.inflate(layoutInflater)

        val calendar = Calendar.getInstance()
        calendar.time = requireArguments()[ARG_DATE] as Date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.dialogDatePicker.init(year, month, day, null)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.date_picker_title)
            .setPositiveButton(R.string.ok) { _, _ ->
                run {
                    val year = binding.dialogDatePicker.year
                    val month = binding.dialogDatePicker.month
                    val day = binding.dialogDatePicker.dayOfMonth
                    sendResult(Activity.RESULT_OK, GregorianCalendar(year, month, day).time)
                }
            }
            .setView(binding.root)
            .create()
    }

    companion object {
        const val EXTRA_DATE = "com.example.criminalIntent.date"
        fun newInstance(date: Date): DatePickerFragment {
            val fragment = DatePickerFragment()
            fragment.arguments = bundleOf(
                ARG_DATE to date
            )
            return fragment
        }
    }

    private fun sendResult(resultCode: Int, date: Date) {
        if (targetFragment == null) {
            return
        }
        targetFragment!!.onActivityResult(targetRequestCode, resultCode, Intent().apply {
            this.putExtra(
                EXTRA_DATE, date
            )
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}