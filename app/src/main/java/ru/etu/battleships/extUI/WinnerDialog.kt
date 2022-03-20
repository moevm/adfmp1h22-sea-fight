package ru.etu.battleships.extUI

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import ru.etu.battleships.databinding.DialogWinnerBinding
import ru.etu.battleships.model.UserScore

class WinnerDialog(context: Context) {
    private var onBackListener: (() -> Unit)? = null
    private var onExitListener: (() -> Unit)? = null

    private var alertDialog: AlertDialog
    private var binding: DialogWinnerBinding = DialogWinnerBinding.inflate(LayoutInflater.from(context))

    init {
        alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        alertDialog.setOnCancelListener {
            onBackListener?.invoke()
        }

        binding.btBack.setOnClickListener {
            onBackListener?.invoke()
        }

        binding.btExit.setOnClickListener {
            onExitListener?.invoke()
        }
    }

    fun setScore(player1Score: UserScore, player2Score: UserScore): WinnerDialog {
        binding.player1Name.text = player1Score.username
        binding.victoriesPlayer1.text = player1Score.victories.toString()

        binding.player2Name.text = player2Score.username
        binding.victoriesPlayer2.text = player2Score.victories.toString()
        return this
    }

    fun setWinner(playerName: String): WinnerDialog {
        binding.winnerName.text = playerName
        return this
    }

    fun setOnBackListener(callback: () -> Unit): WinnerDialog {
        onBackListener = callback
        return this
    }

    fun setOnExitListener(callback: () -> Unit): WinnerDialog {
        onExitListener = callback
        return this
    }

    fun dismiss(): WinnerDialog {
        if (alertDialog.isShowing) {
            alertDialog.dismiss()
        }
        return this
    }

    fun show(): WinnerDialog {
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        return this
    }
}
