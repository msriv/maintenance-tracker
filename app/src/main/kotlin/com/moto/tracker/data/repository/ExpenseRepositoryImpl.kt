package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.ExpenseViewDao
import com.moto.tracker.domain.model.CategoryExpense
import com.moto.tracker.domain.model.MonthlyExpense
import com.moto.tracker.domain.repository.ExpenseRepository
import java.util.Calendar
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseViewDao
) : ExpenseRepository {

    override suspend fun getCategoryTotals(
        vehicleId: Long?,
        from: Long,
        to: Long
    ): List<CategoryExpense> {
        val totals = if (vehicleId != null) {
            dao.getCategoryTotals(vehicleId, from, to)
        } else {
            dao.getAllVehiclesCategoryTotals(from, to)
        }
        val totalAmount = totals.sumOf { it.total }
        return totals.map { ct ->
            CategoryExpense(
                category = ct.category,
                amount = ct.total,
                percentage = if (totalAmount > 0) (ct.total / totalAmount * 100).toFloat() else 0f
            )
        }
    }

    override suspend fun getMonthlyTrend(vehicleId: Long?, year: Int): List<MonthlyExpense> {
        val monthlyExpenses = mutableListOf<MonthlyExpense>()

        for (month in 1..12) {
            val (from, to) = monthRange(year, month)
            val expenses = if (vehicleId != null) {
                dao.getByVehicleAndRange(vehicleId, from, to)
            } else {
                dao.getAllVehiclesCategoryTotals(from, to).map {
                    com.moto.tracker.data.local.entity.ExpenseView(0, it.category, from, it.total)
                }
            }

            val breakdown = expenses.groupBy { it.category }
                .mapValues { (_, items) -> items.sumOf { it.amount } }

            monthlyExpenses.add(
                MonthlyExpense(
                    month = month,
                    year = year,
                    amount = expenses.sumOf { it.amount },
                    breakdown = breakdown
                )
            )
        }
        return monthlyExpenses
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
}
