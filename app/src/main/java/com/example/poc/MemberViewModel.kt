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
    val dniList = mutableListOf<String>()

    private val dniMap = mapOf(
        "12345" to "Sebas",
        "1234" to "Giordan",
        "123" to "Juan",
        "12" to "Juan"
    )
// agregando un cambio el martes a la 1:37
    // arregle  el bug a la 1:41
// agregando un cambio el martes a la 1:39 (esto ya estaba en main y lo puse abajo)
    init {
        viewModelScope.launch {
            _members.emit(emptyList())
        }
    }
// PR el martes se merge con la main
    fun addMember(dni: String, context: Context) {
        viewModelScope.launch {
            val currentList = _members.replayCache.firstOrNull() ?: emptyList()
            if (!isDniDuplicated(dni, dniList)) {
                if (isDniValid(dni)) {
                    val updatedList = currentList.toMutableList().apply {
                        add(dniMap[dni]!!)
                        dniList.add(dni)  // Add DNI to the list
                    }
                    _members.emit(updatedList)
                    Toast.makeText(context, "Miembro agregado: $dni", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Este nombre ya está incluido en la lista", Toast.LENGTH_LONG).show()
            }
        }
    }
// arreglando mi bug el viernes
    fun deleteMember(dni: String, context: Context) {  // Change to delete using DNI
        viewModelScope.launch {
            if (dniList.contains(dni)) {
                val currentList = _members.replayCache.firstOrNull() ?: emptyList()
                val nameToRemove = dniMap[dni]
                val updatedList = currentList.toMutableList().apply {
                    remove(nameToRemove)  // Remove by name obtained from DNI map
                }
                dniList.remove(dni)  // Remove DNI from the list
                _members.emit(updatedList)
                Toast.makeText(context, "Miembro removido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No se encontró miembro con ese DNI", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // agrege un nuevo feature a las 2:17 Miercoles
    private fun isDniDuplicated(dni: String, list: MutableList<String>?): Boolean {
        return list?.contains(dni) ?: false
    }

    fun isDniValid(dni: String): Boolean {
        return dniMap.containsKey(dni)
    }
}

// Otro feature creado a las 2:19 PR aceptado el Martes