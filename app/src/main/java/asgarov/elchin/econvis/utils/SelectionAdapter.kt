package asgarov.elchin.econvis.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import asgarov.elchin.econvis.R

class SelectionAdapter(
    private val items: List<String>,
    private val selectedItems: MutableList<String>
) : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val textView: TextView = view.findViewById(R.id.textView)
    }

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.xml.item_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item
        holder.checkBox.isChecked = selectedItems.contains(item)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item)
                }
            } else {
                selectedItems.remove(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}