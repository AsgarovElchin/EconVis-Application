package asgarov.elchin.econvis.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.TextView
import asgarov.elchin.econvis.R
import asgarov.elchin.econvis.data.model.Country

class RegionExpandableListAdapter(
    private val context: Context,
    private val regionList: List<String>,
    private val countryMap: Map<String, List<Country>>,
    private val selectedCountryIds: MutableList<Long>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = regionList.size

    override fun getChildrenCount(groupPosition: Int): Int =
        countryMap[regionList[groupPosition]]?.size ?: 0

    override fun getGroup(groupPosition: Int): Any = regionList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        countryMap[regionList[groupPosition]]?.get(childPosition) ?: Country(0, "Unknown", regionList[groupPosition])

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val region = getGroup(groupPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = region
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val country = getChild(groupPosition, childPosition) as Country
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_country, parent, false)
        val textView = view.findViewById<TextView>(R.id.countryName)
        val checkBox = view.findViewById<CheckBox>(R.id.countryCheckBox)

        textView.text = country.name
        checkBox.isChecked = selectedCountryIds.contains(country.id)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedCountryIds.contains(country.id)) {
                    selectedCountryIds.add(country.id)
                }
            } else {
                selectedCountryIds.remove(country.id)
            }
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}