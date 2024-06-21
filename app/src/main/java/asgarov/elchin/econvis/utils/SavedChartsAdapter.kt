package asgarov.elchin.econvis.utils

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import asgarov.elchin.econvis.data.model.SavedChart
import asgarov.elchin.econvis.databinding.ItemSavedChartBinding
import java.io.File

class SavedChartsAdapter(
    private val savedCharts: List<SavedChart>,
    private val onDelete: (SavedChart) -> Unit
) : RecyclerView.Adapter<SavedChartsAdapter.SavedChartViewHolder>() {

    inner class SavedChartViewHolder(val binding: ItemSavedChartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedChartViewHolder {
        val binding = ItemSavedChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedChartViewHolder, position: Int) {
        val savedChart = savedCharts[position]
        val bitmap = BitmapFactory.decodeFile(File(savedChart.filePath).absolutePath)
        holder.binding.chartImageView.setImageBitmap(bitmap)

        holder.binding.deleteButton.setOnClickListener {
            onDelete(savedChart)
        }
    }

    override fun getItemCount(): Int = savedCharts.size
}