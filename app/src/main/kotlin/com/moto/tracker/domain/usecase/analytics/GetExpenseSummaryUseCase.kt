package com.moto.tracker.domain.usecase.analytics

import com.moto.tracker.domain.model.CategoryExpense
import com.moto.tracker.domain.model.ExpenseSummary
import com.moto.tracker.domain.repository.ExpenseRepository
import java.util.Calendar
import javax.inject.Inject

class GetExpenseSummaryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        vehicleId: Long?,
        year: Int,
        month: Int? = null  // null = full year
    ): ExpenseSummary {
        val (from, to) = if (month != null) {
            monthRange(year, month)
        } else {
            yearRange(year)
        }

        val categoryTotals = repository.getCategoryTotals(vehicleId, from, to)
        val totalAmount = categoryTotals.sumOf { it.amount }

        val categoryBreakdown = categoryTotals.map { ct ->
            CategoryExpense(
                category = ct.category,
                amount = ct.amount,
                percentage = if (totalAmount > 0) (ct.amount / totalAmount * 100).toFloat() else 0f
            )
        }

        val monthlyTrend = repository.getMonthlyTrend(vehicleId, year)

        return ExpenseSummary(
            totalAmount = totalAmount,
            categoryBreakdown = categoryBreakdown,
            monthlyTrend = monthlyTrend
        )
    }

    private fun monthRange(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val from = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return Pair(from, cal.timeInMillis)
    }

    private fun yearRange(year: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, 0, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val from = cal.timeInMillis
        cal.set(year, 11, 31, 23, 59, 59)
        return Pair(from, cal.timeInMillis)
    }
}
