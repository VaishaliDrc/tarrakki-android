package com.tarrakki

class TarrakkiSingleton {
    companion object {
        var tarrakkiSingleton: TarrakkiSingleton? = null
        fun getInstance() : TarrakkiSingleton {
            if (tarrakkiSingleton == null)
                tarrakkiSingleton = TarrakkiSingleton()
            return tarrakkiSingleton!!
        }
    }

    var debitCardAddress = ""
    var debitCardAmount = ""

    fun clearSingleton() {
        debitCardAddress = ""
        debitCardAmount = ""
    }
}