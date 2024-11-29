package com.example.calculationofscaffolding.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculationofscaffolding.models.Element
import com.example.myapplication.R

class ElementsSettingsAdapter(
    private var elements: List<Element>,
    private val onEditClick: (Element) -> Unit,

) : RecyclerView.Adapter<ElementsSettingsAdapter.ElementViewHolder>() {

    inner class ElementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tVName: TextView = itemView.findViewById(R.id.element)
        val tVPrice: TextView = itemView.findViewById(R.id.price)
        val tvWeight: TextView = itemView.findViewById(R.id.weight)
        val btEdit: Button = itemView.findViewById(R.id.edit)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.element_item_settings, parent, false)
        return ElementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ElementViewHolder, position: Int) {
        val currentElement = elements[position]

        holder.tVName.text = currentElement.name
        holder.tVPrice.text = currentElement.price.toString()
        holder.tvWeight.text = currentElement.weight.toString()
        holder.btEdit.setOnClickListener {
            onEditClick(currentElement)
        }

    }

    override fun getItemCount(): Int {
        return elements.size
    }

    fun updateData(newElements: List<Element>) {
        elements = newElements
        notifyDataSetChanged()
    }


}
