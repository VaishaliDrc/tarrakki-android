package com.tarrakki.module.bankmandate

import com.tarrakki.R
import com.tarrakki.module.bankaccount.SingleButton
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class BankMandateFormVM : FragmentViewModel() {

    val bankMandateWays = arrayListOf<WidgetsViewModel>()

    init {
        bankMandateWays.add(BankMandateWay(
                R.string.download_mandate_form,
                R.string.download_bank_form_description,
                R.drawable.ic_download,
                true))

        bankMandateWays.add(BankMandateWay(
                R.string.upload_scanned_form,
                R.string.upload_scanned_form_description,
                R.drawable.ic_upload))

        bankMandateWays.add(SingleButton(R.string.txtcontinue))
    }
}