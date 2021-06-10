package edu.nuce.apps.newflowtypes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        checkedAdapter = CheckedAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)

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
            Checked::class.java.simpleName,
            recyclerView,
            CheckedKeyProvider(recyclerView),
            CheckedItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectSingleAnything()
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
                    super.onItemStateChanged(key, selected)
                    if (key.toInt() == 0 && selected || tracker.selection.isEmpty && key.toInt() != 0) {
                        tracker.setItemsSelected(tracker.selection.filter { it.toInt() != 0 }, !selected)
                    }
                    if (tracker.selection.isEmpty) {
                        tracker.select(0)
                    }
                    if (key.toInt() != 0 && selected) {
                        tracker.deselect(0)
                    }
                }
            })
    }
}