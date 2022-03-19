package ru.etu.battleships.extUI

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import ru.etu.battleships.databinding.DialogInfoSetupBinding

class InfoSetupDialog(context: Context) {
    private var alertDialog: AlertDialog
    private var binding = DialogInfoSetupBinding.inflate(LayoutInflater.from(context))

    init {
        alertDialog = AlertDialog.Builder(context)
            .setCancelable(true)
            .setView(binding.root)
            .create()

        binding.ok.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun show(): InfoSetupDialog {
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        return this
    }
}
