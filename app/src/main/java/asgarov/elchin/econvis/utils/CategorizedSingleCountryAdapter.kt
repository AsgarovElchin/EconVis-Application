package asgarov.elchin.econvis.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.RadioButton
import android.widget.TextView
import asgarov.elchin.econvis.R
import asgarov.elchin.econvis.data.model.Country

class CategorizedSingleCountryAdapter(
    private val context: Context,
    private val regions: List<String>,
    private val countryMap: Map<String, List<Country>>,
    private var _selectedCountryId: Long? // Changed to a single Long? value
) : BaseExpandableListAdapter() {

    var selectedCountryId: Long?
        get() = _selectedCountryId
        set(value) {
            _selectedCountryId = value
            notifyDataSetChanged() // Refresh view to reflect selection
        }

    override fun getGroupCount(): Int {
        return regions.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return countryMap[regions[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return regions[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return countryMap[regions[groupPosition]]?.get(childPosition) ?: Country(0, "", "")
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return countryMap[regions[groupPosition]]?.get(childPosition)?.id ?: 0L
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupTitle = getGroup(groupPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group, parent, false)
        val groupTextView = view.findViewById<TextView>(R.id.listTitle)
        groupTextView.text = groupTitle
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val country = getChild(groupPosition, childPosition) as Country
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_single_choice, parent, false)
        val countryTextView = view.findViewById<TextView>(R.id.listItem)
        val radioButton = view.findViewById<RadioButton>(R.id.radioButton)

        countryTextView.text = country.name
        radioButton.isChecked = selectedCountryId == country.id // Compare with a single value

        // Handle clicks on the entire item view
        view.setOnClickListener {
            selectedCountryId = country.id // Set selected country ID
            notifyDataSetChanged() // Refresh view to reflect selection
        }

        // Handle clicks on the RadioButton itself
        radioButton.setOnClickListener {
            selectedCountryId = country.id // Set selected country ID
            notifyDataSetChanged() // Refresh view to reflect selection
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
