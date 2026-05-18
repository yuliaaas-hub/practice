package ci.nsu.mobile.main.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calc: DepositCalculation)

    @Query("SELECT * FROM deposit_calculations WHERE userId = :userId ORDER BY calculationDate DESC")
    fun getByUser(userId: Long): Flow<List<DepositCalculation>>

    @Delete
    suspend fun delete(calc: DepositCalculation)
}