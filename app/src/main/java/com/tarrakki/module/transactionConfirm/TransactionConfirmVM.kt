package com.tarrakki.module.transactionConfirm

import org.supportcompact.FragmentViewModel
import java.util.ArrayList

class TransactionConfirmVM : FragmentViewModel() {

    val list = ArrayList<TransactionConfirm>()

    init {
        val statuslist = arrayListOf<TranscationStatus>()
        statuslist.add(TranscationStatus("Mutual Fund Payment","via Net Banking",1))
        statuslist.add(TranscationStatus("Order Placed with AMC","",2))
        statuslist.add(TranscationStatus("Investment Confirmation","",3))
        statuslist.add(TranscationStatus("Units Alloted","",3))

        list.add(TransactionConfirm("HDFC GOLD Fund Direct Growth","Lumpsump",true,statuslist))
        list.add(TransactionConfirm("HDFC GOLD Fund Direct Growth","SIP",false,statuslist))
    }

    data class TransactionConfirm(val name : String,val type : String,
                                  val isSuccess : Boolean,val status : ArrayList<TranscationStatus>)

    data class TranscationStatus(val name: String,val description : String,val status : Int)
}