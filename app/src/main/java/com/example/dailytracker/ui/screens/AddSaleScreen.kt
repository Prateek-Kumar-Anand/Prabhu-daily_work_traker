package com.example.dailytracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytracker.util.DateUtils
import com.example.dailytracker.viewmodel.SaleViewModel
import com.example.dailytracker.viewmodel.ViewModelFactory

@Composable
fun AddSaleScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: SaleViewModel = viewModel(factory = factory)

    var itemName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var customer by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var itemError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Text(text = "Record a Sale", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = DateUtils.formatFullDate(DateUtils.now()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp, top = 4.dp)
        )

        OutlinedTextField(
            value = itemName,
            onValueChange = {
                itemName = it
                itemError = false
            },
            label = { Text("Item / Service sold") },
            isError = itemError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = {
                amountText = it
                amountError = false
            },
            label = { Text("Sale amount") },
            isError = amountError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it.filter { c -> c.isDigit() } },
            label = { Text("Quantity") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = customer,
            onValueChange = { customer = it },
            label = { Text("Customer (optional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull()
                var hasError = false
                if (itemName.isBlank()) {
                    itemError = true
                    hasError = true
                }
                if (amount == null || amount <= 0.0) {
                    amountError = true
                    hasError = true
                }
                if (!hasError) {
                    val quantity = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    viewModel.addSale(itemName, amount!!, quantity, customer, note, DateUtils.now()) { onBack() }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Text("Save Sale")
        }
    }
}
