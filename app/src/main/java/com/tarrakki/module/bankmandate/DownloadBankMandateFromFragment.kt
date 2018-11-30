package com.tarrakki.module.bankmandate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tarrakki.R

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadBankMandateFromFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DownloadBankMandateFromFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download_bank_mandate_from, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment DownloadBankMandateFromFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = DownloadBankMandateFromFragment().apply { arguments = basket }
    }
}
