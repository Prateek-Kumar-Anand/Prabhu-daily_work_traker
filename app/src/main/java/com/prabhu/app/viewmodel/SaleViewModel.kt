package com.prabhu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prabhu.app.data.model.SaleEntity
import com.prabhu.app.data.repository.SaleRepository
import kotlinx.coroutines.launch

class SaleViewModel(private val repository: SaleRepository) : ViewModel() {

    fun addSale(
        itemName: String,
        amount: Double,
        quantity: Int,
        customer: String,
        note: String,
        dateMillis: Long,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.add(
                SaleEntity(
                    itemName = itemName,
                    amount = amount,
                    quantity = quantity,
                    customer = customer,
                    note = note,
                    dateMillis = dateMillis
                )
            )
            onDone()
        }
    }
}
