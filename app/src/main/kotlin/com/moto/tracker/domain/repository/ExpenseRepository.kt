package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.CategoryExpense
import com.moto.tracker.domain.model.MonthlyExpense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun getCategoryTotals(vehicleId: Long?, from: Long, to: Long): List<CategoryExpense>
    suspend fun getMonthlyTrend(vehicleId: Long?, year: Int): List<MonthlyExpense>
}
