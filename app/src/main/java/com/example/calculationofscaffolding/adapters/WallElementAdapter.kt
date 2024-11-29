package com.example.calculationofscaffolding.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculationofscaffolding.models.Element
import com.example.myapplication.R
class WallElementAdapter(private var elements: List<Element>) : RecyclerView.Adapter<WallElementAdapter.WallElementViewHolder>() {



    // ViewHolder для базового элемента (для отображения цены)
    class WallElementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val elementName: TextView = view.findViewById(R.id.tvElementName)
        val elementQuantity: TextView = view.findViewById(R.id.tvElementQuantity)

    }

    // ViewHolder для расширенного элемента (для отображения веса)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallElementViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wall_element1, parent, false)
        return WallElementViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallElementViewHolder, position: Int) {
        val element = elements[position]
        holder.elementName.text = element.name
        holder.elementQuantity.text = "Кол-во: ${element.quantity}"

    }



    override fun getItemCount(): Int = elements.size

    fun submitList(newElements: List<Element>) {
        elements = newElements
        notifyDataSetChanged()
    }
}
