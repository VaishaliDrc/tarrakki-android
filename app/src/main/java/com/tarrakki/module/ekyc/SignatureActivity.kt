package com.tarrakki.module.ekyc

import android.graphics.Bitmap
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.ActivitySignatureBinding
import com.williamww.silkysignature.views.SignaturePad
import kotlinx.android.synthetic.main.activity_signature.*
import org.supportcompact.CoreActivity
import org.supportcompact.ktx.dismissProgress
import org.supportcompact.ktx.showProgress
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


class SignatureActivity : CoreActivity<SignatureVM, ActivitySignatureBinding>() {

    override fun getLayout(): Int {
        return R.layout.activity_signature
    }

    override fun createViewModel(): Class<out SignatureVM> {
        return SignatureVM::class.java
    }

    override fun setVM(binding: ActivitySignatureBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        signPad?.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onClear() {
                getViewModel().isEdit.set(false)
            }

            override fun onSigned() {
                getViewModel().isEdit.set(true)
            }
        })
        btnClear?.setOnClickListener { signPad?.clear() }
        ivClose?.setOnClickListener { finish() }
        btnContinue?.setOnClickListener {
            val file = signPad?.transparentSignatureBitmap?.let { it1 -> saveAsFile(it1) }
            App.INSTANCE.signatureFile.value = file
            finish()
        }
    }

    private fun saveAsFile(myBitmap: Bitmap): File {
        getViewModel().showProgress()
        val tempFile = File(cacheDir, "signedImg")
        thread {
            val outStream = FileOutputStream(tempFile)
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.close()
        }
        getViewModel().dismissProgress()
        return tempFile
    }
}