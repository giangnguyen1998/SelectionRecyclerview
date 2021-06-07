package edu.nuce.apps.newflowtypes

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.nuce.apps.newflowtypes.databinding.ItemCheckBinding
import kotlinx.android.parcel.Parcelize
import kotlin.properties.Delegates

class CheckedAdapter : ListAdapter<Checked, CheckedVH>(DiffCallback) {

    init {
        setHasStableIds(true)
    }

    var tracker: SelectionTracker<Long>? = null

    var selectedItem: Int by Delegates.observable(RecyclerView.NO_POSITION) { _, old, new ->
        notifyItemChanged(old)
        notifyItemChanged(new)
    }

    var selectedMode: Mode by Delegates.vetoable(Mode.SINGLE) { _, old, new ->
        selectedItem = RecyclerView.NO_POSITION
        tracker?.clearSelection()
        old != new
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckedVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckBinding.inflate(layoutInflater, parent, false)
        return CheckedVH(binding) { position ->
            if (selectedMode == Mode.SINGLE) {
                selectedItem = position
            } else {
                selectedItem = RecyclerView.NO_POSITION
            }
        }
    }

    override fun onBindViewHolder(holder: CheckedVH, position: Int) {
        getItem(position).let {
            holder.bind(
                it,
                if (selectedMode == Mode.SINGLE) position == selectedItem else tracker?.isSelected(
                    position.toLong()
                ) ?: false
            )
        }
    }
}

enum class Mode {
    SINGLE,
    MULTIPLE
}

class CheckedVH(
    private val binding: ItemCheckBinding,
    private val onClick: (position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Checked, checked: Boolean = false) {
        binding.run {
            text.text = item.data
            check.isSelected = checked
            root.setOnClickListener { onClick.invoke(adapterPosition) }
        }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long? = itemId
        }
}

@Parcelize
data class Checked(
    val data: String
) : Parcelable

object DiffCallback : DiffUtil.ItemCallback<Checked>() {
    override fun areItemsTheSame(oldItem: Checked, newItem: Checked): Boolean {
        return oldItem.data == newItem.data
    }

    override fun areContentsTheSame(oldItem: Checked, newItem: Checked): Boolean {
        return oldItem == newItem
    }
}

class CheckedItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as CheckedVH).getItemDetails()
        }
        return null
    }
}

class CheckedKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        return recyclerView.findViewHolderForItemId(key)?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}