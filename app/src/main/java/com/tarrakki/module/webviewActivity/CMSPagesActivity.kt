package com.tarrakki.module.webviewActivity

import android.os.Bundle
import android.view.View
import com.tarrakki.BaseActivity
import com.tarrakki.R
import com.tarrakki.module.webview.WebViewFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.startFragment

class CMSPagesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createReference() {
        super.createReference()
        getViewModel().footerVisibility.set(View.GONE)
        startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
      //  postSticky(Event.TERMS_AND_CONDITIONS_PAGE)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
