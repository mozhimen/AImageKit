package com.mozhimen.imagek.coil2.test

import android.os.Bundle
import com.mozhimen.mvvmk.bases.activity.viewbinding.BaseActivityVB
import com.mozhimen.imagek.coil2.loadImage_ofCoil
import com.mozhimen.imagek.coil2.test.databinding.ActivityImagekCoilBinding

class ImageKCoilActivity : BaseActivityVB<ActivityImagekCoilBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        vb.imagekCoilImg1.loadImage_ofCoil(R.drawable.imagek_img_test)
    }
}