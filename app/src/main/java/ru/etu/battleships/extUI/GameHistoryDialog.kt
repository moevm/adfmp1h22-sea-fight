package ru.etu.battleships.extUI

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.etu.battleships.R
import ru.etu.battleships.databinding.DialogGameHistoryBinding
import ru.etu.battleships.databinding.GameHistoryItemBinding
import ru.etu.battleships.model.CellState
import ru.etu.battleships.model.PlayerStep

class GameHistoryDialog(playersSteps: MutableList<PlayerStep>, context: Context) {
    class GameHistoryAdapter(private var playersSteps: MutableList<PlayerStep>): RecyclerView.Adapter<GameHistoryAdapter.Holder>() {
        class Holder(item: View): RecyclerView.ViewHolder(item) {
            private var binding = GameHistoryItemBinding.bind(item)
            fun bind(playerStep: PlayerStep) = with(binding) {
                who.text = playerStep.player
                whereLetter.text = "ABCDEFGHIK"[playerStep.point.y - 1].toString()
                whereNumber.text = playerStep.point.x.toString()

                status.text = when (playerStep.cellState) {
                    CellState.MISS -> {
                        status.setTextColor(ResourcesCompat.getColor(root.resources, R.color.blue, null))
                        root.resources.getString(R.string.miss)
                    }
                    CellState.HIT -> {
                        status.setTextColor(ResourcesCompat.getColor(root.resources, R.color.orange, null))
                        root.resources.getString(R.string.hit)
                    }
                    else -> {
                        status.setTextColor(ResourcesCompat.getColor(root.resources, R.color.black, null))
                        "[unknown]"
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.game_history_item, parent, false)
        )

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(playersSteps[position])
        }

        override fun getItemCount() = playersSteps.size

        fun addPlayerStep(playerStep: PlayerStep) {
            this.playersSteps.add(playerStep)
            this.notifyItemInserted(this.playersSteps.size - 1)
        }

    }

    private val alertDialog: AlertDialog
    private val binding = DialogGameHistoryBinding.inflate(LayoutInflater.from(context))
    private val adapter = GameHistoryAdapter(playersSteps)

    init {
        alertDialog = AlertDialog.Builder(context)
            .setCancelable(true)
            .setView(binding.root)
            .create()

        binding.btClose.setOnClickListener {
            alertDialog.cancel()
        }

        binding.rvGameHistory.layoutManager = LinearLayoutManager(binding.root.context)
        binding.rvGameHistory.adapter = adapter
    }

    fun addStep(playerStep: PlayerStep) {
        adapter.addPlayerStep(playerStep)
    }

    fun show(): GameHistoryDialog {
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        return this
    }
}