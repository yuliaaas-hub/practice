package ci.nsu.mobile.main.repository

import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.data.local.DepositDao
import kotlinx.coroutines.flow.Flow

class DepositRepository(private val dao: DepositDao) {
    fun getCalculations(userId: Long): Flow<List<DepositCalculation>> =
        dao.getByUser(userId)

    suspend fun save(calc: DepositCalculation) = dao.insert(calc)
    suspend fun delete(calc: DepositCalculation) = dao.delete(calc)
}