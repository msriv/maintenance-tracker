package com.moto.tracker.data.local.entity

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "expense_view",
    value = """
        SELECT vehicleId, 'fuel' as category, fillDate as date, totalCost as amount
        FROM fuel_logs
        UNION ALL
        SELECT vehicleId, 'service' as category, performedDate as date, cost as amount
        FROM maintenance_logs WHERE cost IS NOT NULL
        UNION ALL
        SELECT vehicleId, 'insurance' as category, issueDate as date, amount as amount
        FROM documents WHERE type = 'insurance' AND amount IS NOT NULL AND issueDate IS NOT NULL
        UNION ALL
        SELECT vehicleId, 'other' as category, createdAt as date, amount as amount
        FROM documents WHERE type != 'insurance' AND amount IS NOT NULL
    """
)
data class ExpenseView(
    val vehicleId: Long,
    val category: String,
    val date: Long,
    val amount: Double
)

data class CategoryTotal(
    val category: String,
    val total: Double
)
