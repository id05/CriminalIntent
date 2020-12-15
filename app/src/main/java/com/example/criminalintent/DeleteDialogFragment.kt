package com.example.criminalintent

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.*


class DeleteDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(id: UUID): DeleteDialogFragment {
            return DeleteDialogFragment().also {
                it.arguments = bundleOf(
                    ARG_CRIME_ID to id
                )
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete_crime) { _, _ ->
                CrimeLab.getInstance(requireActivity().applicationContext)
                    targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent())
            }
            .create()
    }
}