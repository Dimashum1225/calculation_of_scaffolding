package com.example.calculationofscaffolding.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calculationofscaffolding.models.Wall
import com.example.myapplication.R

class WallAdapter(
    private var walls: MutableList<Wall>, // Теперь список можно изменять
    private val onEdit: (Wall,Int) -> Unit,
    private val onDelete: (Wall) -> Unit
) : RecyclerView.Adapter<WallAdapter.WallViewHolder>() {

    class WallViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wallNumberTv: TextView = view.findViewById(R.id.wallNumberTv)
        val wallInfo: TextView = view.findViewById(R.id.tv_wall_info)
        val totalSquare: TextView = view.findViewById(R.id.totalSquare)
        val elementsRecyclerView: RecyclerView = view.findViewById(R.id.item_wall_recycler)
        val totalPrice:TextView = view.findViewById(R.id.TotalPrice)
        val btnEdit: Button = view.findViewById(R.id.edit)
        val btnDelete: Button = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wall, parent, false)
        return WallViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallViewHolder, position: Int) {
        val wall = walls[position]
        val totalPrice = wall.elements
            .filter { element ->
                !(wall.isJaskSelected && element.name == "пятки")&&
                        !(wall.isHeelSelected && element.name == "домкрат")
            }
            .sumOf { element ->
                element.price * element.quantity
            }

        // Отображаем информацию о стене
        holder.wallNumberTv.text = "Фасад № ${position+1}"
        holder.wallInfo.text = "Ширина: ${wall.width}, Высота: ${wall.height}, Ярусы: ${wall.tiers},"
        holder.totalSquare.text = "Площадь стены: ${wall.width * wall.height}"
        holder.totalPrice.text = "Итого : ${totalPrice}p"
        // Настраиваем вложенный RecyclerView для отображения элементов стены
        holder.elementsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        val elementAdapter = WallElementAdapter(wall.elements)
        holder.elementsRecyclerView.adapter = elementAdapter

        // Устанавливаем обработчики кнопок
        holder.btnEdit.setOnClickListener {
            onEdit(wall,position)
        }
        holder.btnDelete.setOnClickListener {
            onDelete(wall)
        }
    }

    override fun getItemCount(): Int = walls.size

    // Метод для обновления данных в адаптере
    fun updateWalls(newWalls: List<Wall>) {

        walls = newWalls.toMutableList()
        notifyDataSetChanged()
    }
}
