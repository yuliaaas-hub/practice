package ci.nsu.mobile.main.ui.calc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.databinding.ItemCalculationBinding
import java.text.SimpleDateFormat
import java.util.*

class CalculationsAdapter(
    private val onDeleteClick: (DepositCalculation) -> Unit
) : ListAdapter<DepositCalculation, CalculationsAdapter.CalcViewHolder>(CalcDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalcViewHolder {
        val binding = ItemCalculationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalcViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CalcViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalcViewHolder(
        private val binding: ItemCalculationBinding,
        private val onDeleteClick: (DepositCalculation) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

        fun bind(calc: DepositCalculation) {
            binding.tvInitialAmount.text = "Сумма: ${"%.2f".format(calc.initialAmount)} ₽"
            binding.tvPeriod.text = "Срок: ${calc.periodMonths} мес."
            binding.tvRate.text = "Ставка: ${calc.interestRate}%"
            binding.tvFinalAmount.text = "Итог: ${"%.2f".format(calc.finalAmount)} ₽"
            binding.tvInterest.text = "Прибыль: ${"%.2f".format(calc.interestEarned)} ₽"
            binding.tvDate.text = dateFormat.format(Date(calc.calculationDate))

            binding.btnDelete.setOnClickListener { onDeleteClick(calc) }
        }
    }

    class CalcDiffCallback : DiffUtil.ItemCallback<DepositCalculation>() {
        override fun areItemsTheSame(oldItem: DepositCalculation, newItem: DepositCalculation) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DepositCalculation, newItem: DepositCalculation) =
            oldItem == newItem
    }
}