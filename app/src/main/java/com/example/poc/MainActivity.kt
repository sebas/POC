package com.example.poc

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val memberViewModel: MemberViewModel by viewModels()
    private lateinit var memberAdapter: MemberAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        memberAdapter = MemberAdapter(emptyList(), emptyList()) { dni ->
            memberViewModel.deleteMember(dni, this)  // Pass DNI instead of name
        }
        recyclerView.adapter = memberAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            memberViewModel.members.collect { members ->
                memberAdapter = MemberAdapter(members, memberViewModel.dniList) { dni ->
                    memberViewModel.deleteMember(dni, this@MainActivity)  // Pass DNI instead of name
                }
                recyclerView.adapter = memberAdapter
            }
        }
// arregle el button a las 2:56 miercoles (esto puede generar un conflicto)
        //se acepto el pr de otra persona a las 2:53 martes

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddMemberDialog()
        }

        val countMember: TextView = findViewById(R.id.memberCount)
        lifecycleScope.launch {
            memberViewModel.members.collect { members ->
                memberAdapter = MemberAdapter(members, memberViewModel.dniList) { dni ->
                    memberViewModel.deleteMember(dni, this@MainActivity)  // Pass DNI instead of name
                }
                recyclerView.adapter = memberAdapter
                countMember.text = "Members: ${members.size}"
            }
        }
    }

    private fun showAddMemberDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Miembro")

        val editText = EditText(this)
        builder.setView(editText)

        builder.setPositiveButton("OK", null)
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss() // Close the dialog on cancel
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val dni = editText.text.toString()
            if (memberViewModel.isDniValid(dni)) {
                memberViewModel.addMember(dni, this)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "No existe cliente con ese DNI", Toast.LENGTH_SHORT).show()
                // Do not close the dialog to keep it visible
            }

            // Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
}