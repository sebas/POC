package com.example.poc

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MemberViewModel : ViewModel() {
    private val _members = MutableSharedFlow<List<String>>(replay = 1)
    val members: Flow<List<String>> get() = _members.asSharedFlow()
    private val dniList = mutableListOf<String>()

    private val dniMap = mapOf(
        "12345" to "Sebas",
        "1234" to "Giordan",
        "123" to "Juan"
    )

    init {
        viewModelScope.launch {
            _members.emit(emptyList())
        }
    }


    fun addMember(dni: String, context: Context) {
        viewModelScope.launch {
            val currentList = _members.replayCache.firstOrNull() ?: emptyList()
            if (!isDniDuplicated(dni,dniList)) {
                if (isDniValid(dni)) {
                    val updatedList = currentList.toMutableList().apply {
                        add(dniMap[dni]!!)
                        dniList.add(dni)
                    }
                    _members.emit(updatedList)
                    Toast.makeText(context, "Miembro agregado: $dni", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Este nombre ya esta incluido en la lista", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deleteMember (name:String, context: Context) {
        viewModelScope.launch {
            val currentList = _members.replayCache.firstOrNull() ?: emptyList()
            val updatedList = currentList.toMutableList().apply {
                remove(name)
            }
            _members.emit(updatedList)
            Toast.makeText(context, "Miembro removido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDniDuplicated(dni: String, list: MutableList<String>?): Boolean {
        return list?.contains(dni) ?: false
    }

    fun isDniValid(dni: String): Boolean {
        return dniMap.containsKey(dni)
    }
}


