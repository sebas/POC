package com.example.poc

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        memberAdapter = MemberAdapter(emptyList()) { member ->
            memberViewModel.deleteMember(member, this)
        }
        recyclerView.adapter = memberAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            memberViewModel.members.collect { members ->
                memberAdapter = MemberAdapter(members) { member ->
                    memberViewModel.deleteMember(member, this@MainActivity)
                }
                recyclerView.adapter = memberAdapter
            }
        }

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddMemberDialog()
        }
    }

    private fun showAddMemberDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Miembro")

        val editText = EditText(this)
        builder.setView(editText)

        builder.setPositiveButton("OK", null)
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss() // Cierra el diálogo al cancelar
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
                // No cerramos el diálogo para mantenerlo visible
            }

            // Ocultar el teclado
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
}