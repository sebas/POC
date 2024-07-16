package com.example.poc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(
    private val members: List<String>,
    private val dniList: List<String>,  // Add DNI list
    private val onDeleteClick: (String) -> Unit  // Change parameter type to String for DNI
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberName: TextView = itemView.findViewById(R.id.memberName)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_delete_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        val dni = dniList[position]  // Get corresponding DNI
        holder.memberName.text = member
        holder.deleteButton.setOnClickListener {
            onDeleteClick(dni)  // Pass the DNI to the onDeleteClick function
        }
    }

    override fun getItemCount(): Int = members.size
}