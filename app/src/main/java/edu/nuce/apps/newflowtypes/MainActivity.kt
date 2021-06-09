package edu.nuce.apps.newflowtypes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.nuce.apps.newflowtypes.data.ApiService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var checkedAdapter: CheckedAdapter
    private lateinit var tracker: SelectionTracker<Long>

    @Inject
    lateinit var apiService: ApiService

    private var isSelectedDefault = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        checkedAdapter = CheckedAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val switchCompat = findViewById<SwitchCompat>(R.id.swichCompat)

        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            checkedAdapter.selectedMode = if (isChecked) Mode.MULTIPLE else Mode.SINGLE
        }
        checkedAdapter.selectedMode = Mode.MULTIPLE

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
        tracker.select(0)

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker.selection.map { checkedAdapter.currentList[it.toInt()] }
                    Log.e(MainActivity::class.java.simpleName, items.toString())
                }

                override fun onItemStateChanged(key: Long, selected: Boolean) {
                    if (key.toInt() == 0 && selected && !isSelectedDefault) {
                        isSelectedDefault = true
                        tracker.clearSelection()
                        tracker.select(0)
                    }
                    if (key.toInt() != 0) {
                        tracker.deselect(0)
                        isSelectedDefault = false
                    }
                    super.onItemStateChanged(key, selected)
                }
            })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        checkedAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::checkedAdapter.isInitialized) {
            checkedAdapter.onSaveStateInstance(outState)
        }
    }
}