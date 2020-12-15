package com.example.criminalintent

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemCrimeBinding
import java.util.*


class CrimeListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var crimeLab:CrimeLab
    private lateinit var adapter: CrimeAdapter
    private var subtitleVisible = false
    private val SAVED_SUBTITLE_VISIBLE = "subtitle"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.crime_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.crime_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        crimeLab = CrimeLab.getInstance(requireActivity().applicationContext)
        adapter = CrimeAdapter(crimeLab.crimes)
        recyclerView.adapter = adapter
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subtitleVisible = savedInstanceState?.getBoolean(SAVED_SUBTITLE_VISIBLE) ?: false
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible)
    }

    override fun onResume() {
        super.onResume()
        updateSubtitle()

        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
        menu.findItem(R.id.subtitle_button)
            .setTitle(if (subtitleVisible) R.string.hide_subtitle else R.string.show_subtitle )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.new_crime -> {
            val crime = Crime()
            crimeLab + crime
            startActivity(crimeIntent(requireActivity(), crime.id))
            true
        }
        R.id.subtitle_button -> {
            subtitleVisible = !subtitleVisible
            requireActivity().invalidateOptionsMenu()
            updateSubtitle()
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateSubtitle(): Boolean {
        val a = activity as AppCompatActivity
        a.supportActionBar!!.subtitle = if (subtitleVisible) getString(
            R.string.subtitle_format,
            crimeLab.crimes.count()
        ) else null
        return true
    }

    inner class CrimeHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_crime, parent, false)),
        View.OnClickListener {
        private val binding =
            ListItemCrimeBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            startActivity(crimeIntent(activity!!, crime.id))
        }

        private var _crime: Crime? = null
        private val crime: Crime get() = _crime!!

        fun bind(crime: Crime) {
            _crime = crime
            binding.crimeDate.text = crime.date.toString()
            binding.crimeTitle.text = crime.title
            binding.imageView.isVisible = crime.solved
        }

    }

    inner class CrimeAdapter(crimeList: MutableList<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        private var list = crimeList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            return CrimeHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

}