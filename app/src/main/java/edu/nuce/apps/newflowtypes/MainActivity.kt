package edu.nuce.apps.newflowtypes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var checkedAdapter: CheckedAdapter
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        checkedAdapter = CheckedAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val switchCompat = findViewById<SwitchCompat>(R.id.swichCompat)

        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            checkedAdapter.selectedMode = if (isChecked) Mode.MULTIPLE else Mode.SINGLE
        }

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = checkedAdapter
        }

        checkedAdapter.submitList(
            listOf(
                Checked("mot"),
                Checked("hai"),
                Checked("ba"),
                Checked("bon")
            )
        )

        tracker = SelectionTracker.Builder(
            "mySelection",
            recyclerView,
            CheckedKeyProvider(recyclerView),
            CheckedItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        checkedAdapter.tracker = tracker

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker?.selection!!.size()
                }
            })
    }
}