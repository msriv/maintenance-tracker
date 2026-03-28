package com.moto.tracker.domain.model

data class ExpenseSummary(
    val totalAmount: Double,
    val categoryBreakdown: List<CategoryExpense>,
    val monthlyTrend: List<MonthlyExpense>
)

data class CategoryExpense(
    val category: String,
    val amount: Double,
    val percentage: Float
)

data class MonthlyExpense(
    val month: Int,    // 1-12
    val year: Int,
    val amount: Double,
    val breakdown: Map<String, Double> = emptyMap()
)
