package com.tarrakki.module.cart

import com.tarrakki.module.invest.Fund
import org.supportcompact.FragmentViewModel

class CartVM : FragmentViewModel() {

    val funds = arrayListOf<Fund>()

    init {
        funds.add(Fund(
                "SBI Banking and Financial Services Growth Direct Plan",
                "Sectoral/Thematic",
                0.93f,
                18.2f,
                19.4f,
                13.7f,
                5.2f,
                5.2f)
        )

        funds.add(Fund(
                "DSP Blackrock Natural Resources and New Energy Growth Direct Plan",
                "Sectoral/Thematic",
                0.26f,
                18.5f,
                4.6f,
                14.8f,
                25.7f,
                6.5f)
        )
    }

}