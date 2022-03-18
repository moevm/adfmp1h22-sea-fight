package ru.etu.battleships.extUI

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import ru.etu.battleships.databinding.DialogQuestionBinding

class QuestionDialog(context: Context) {
    private var acceptListener: (() -> Unit)? = null
    private var alertDialog: AlertDialog
    private var binding: DialogQuestionBinding = DialogQuestionBinding.inflate(LayoutInflater.from(context))

    init {
        alertDialog = AlertDialog.Builder(context)
            .setCancelable(true)
            .setView(binding.root)
            .create()

        binding.decline.setOnClickListener {
            alertDialog.dismiss()
        }

        binding.accept.setOnClickListener {
            alertDialog.dismiss()
            acceptListener?.invoke()
        }
    }

    fun setMessage(text: String): QuestionDialog {
        binding.message.text = text
        binding.root.invalidate()
        return this
    }

    fun setOnAcceptListener(callback: () -> Unit): QuestionDialog {
        acceptListener = callback
        return this
    }

    fun show(): QuestionDialog {
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        return this
    }
}