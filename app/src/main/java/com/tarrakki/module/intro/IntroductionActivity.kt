package com.tarrakki.module.intro

import android.content.Intent
import com.tarrakki.IS_FROM_INTRO
import com.tarrakki.R
import com.tarrakki.databinding.ActivityInroductionBinding
import com.tarrakki.databinding.LayoutIntroducationItemBinding
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_inroduction.*
import org.supportcompact.CoreActivity
import org.supportcompact.adapters.setPageAdapter
import org.supportcompact.ktx.setFirsttimeInstalled
import org.supportcompact.ktx.startActivity

class IntroductionActivity : CoreActivity<IntroducationVM, ActivityInroductionBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_inroduction
    }

    override fun createViewModel(): Class<out IntroducationVM> {
        return IntroducationVM::class.java
    }

    override fun setVM(binding: ActivityInroductionBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setAdapter()

        btn_existing_user?.setOnClickListener {
            setFirsttimeInstalled(false)
            startActivity<LoginActivity>()
            finish()
        }
        btn_new_user?.setOnClickListener {
            setFirsttimeInstalled(false)
            val i = Intent(this, RegisterActivity::class.java)
            i.putExtra(IS_FROM_INTRO, true)
            startActivity(i)
            finish()
        }
    }

    private fun setAdapter() {
        pager_intro?.setPageAdapter(R.layout.layout_introducation_item,
                getViewModel().getIntroductionList()) { binder: LayoutIntroducationItemBinding,
                                                        item: IntroducationVM.Introduction ->
            binder.vm = item
            binder.executePendingBindings()
        }
        pageIndicator?.setViewPager(pager_intro)
        pager_intro?.interval = 4000
        pager_intro?.startAutoScroll()
    }

}
