package app.camp.gladiator.ui.base

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import app.camp.gladiator.R

abstract class BaseFragment : Fragment() {

    fun showMessage(message: String) {
        context?.let {
            val dialog: AlertDialog = AlertDialog.Builder(it)
                .setMessage(message)
                .setPositiveButton(R.string.ok, { dialog, _ -> { dialog.dismiss() } })
                .show()
        }
    }

}
