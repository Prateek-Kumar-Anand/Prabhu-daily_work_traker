package com.example.dailytracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytracker.data.model.expenseCategories
import com.example.dailytracker.util.DateUtils
import com.example.dailytracker.viewmodel.ExpenseViewModel
import com.example.dailytracker.viewmodel.ViewModelFactory
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: ExpenseViewModel = viewModel(factory = factory)

    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(expenseCategories.first()) }
    var note by remember { mutableStateOf("") }
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
        Text(text = "Record Spending", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = DateUtils.formatFullDate(DateUtils.now()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp, top = 4.dp)
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = {
                amountText = it
                amountError = false
            },
            label = { Text("Amount") },
            singleLine = true,
            isError = amountError,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        if (amountError) {
            Text(
                text = "Enter a valid amount",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        CategoryChipRow(
            options = expenseCategories,
            selected = category,
            onSelect = { category = it }
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull()
                if (amount == null || amount <= 0.0) {
                    amountError = true
                } else {
                    viewModel.addExpense(amount, category, note, DateUtils.now()) { onBack() }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Text("Save Spending")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChipRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option) },
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}
