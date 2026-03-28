package com.moto.tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.moto.tracker.data.local.entity.CategoryTotal
import com.moto.tracker.data.local.entity.ExpenseView
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseViewDao {
    @Query("SELECT * FROM expense_view WHERE vehicleId = :vehicleId AND date BETWEEN :from AND :to ORDER BY date DESC")
    fun observeByVehicleAndRange(vehicleId: Long, from: Long, to: Long): Flow<List<ExpenseView>>

    @Query("SELECT * FROM expense_view WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun observeAllInRange(from: Long, to: Long): Flow<List<ExpenseView>>

    @Query("SELECT category, SUM(amount) as total FROM expense_view WHERE vehicleId = :vehicleId AND date BETWEEN :from AND :to GROUP BY category")
    suspend fun getCategoryTotals(vehicleId: Long, from: Long, to: Long): List<CategoryTotal>

    @Query("SELECT category, SUM(amount) as total FROM expense_view WHERE date BETWEEN :from AND :to GROUP BY category")
    suspend fun getAllVehiclesCategoryTotals(from: Long, to: Long): List<CategoryTotal>

    @Query("SELECT * FROM expense_view WHERE vehicleId = :vehicleId AND date BETWEEN :from AND :to ORDER BY date ASC")
    suspend fun getByVehicleAndRange(vehicleId: Long, from: Long, to: Long): List<ExpenseView>
}
