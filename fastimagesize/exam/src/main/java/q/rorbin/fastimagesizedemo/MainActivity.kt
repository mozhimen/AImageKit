package q.rorbin.fastimagesizedemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.imagek.fastimagesize.ImageKFastImageSize
import q.rorbin.fastimagesizedemo.databinding.ActivityMainBinding

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/12/28 9:58
 * @Version 1.0
 */
class MainActivity : BaseActivityVB<ActivityMainBinding>() {
    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        vb.btnMeasure.applySuspendDebounceClickListener(lifecycleScope) {
            val res = ImageKFastImageSize.getImageWidthAndHeight(
                "https://lele-res.lelejoy.com/yx_20230921155041/js_20231019174402/Asphalt8_1702049704188/0_1702049715915.png?q-sign-algorithm=sha1&q-ak=AKIDe83k6vOsmi486oxtmNGbBTwvY_Pzo4CEJp1OaXJGwHd5LZ8VtQKM20a10ItQ5XYV&q-sign-time=1703745413;1706337413&q-key-time=1703745412;1703752612&q-header-list=&q-url-param-list=&q-signature=d57b487b9e161b7cceb24471842f62f5c8eaf2a0&x-cos-security-token=92Mhf9rtyfcMQr1VMdICv6xgwsEj8Oja1774dd46bd52ff8db443ce629025213bYHhHBxetblB3arn5670YFz4URL70ZG2D79fn3yczR6C5OJE8bNoudofgvGfGxGa6_VZnW79wPoXsiw8c6_oRcNCqJqLkJBK1w5feJGuthWXD_LIjNwUY1AuZFYghg7MdkoogO0IuImZpdNgbkpGKpSdik1G6EQ9Ny33tKfO6VpsTeLXHRjh8n1KguifdhskoVM4TtLV4eSQEAZ9a7TYO0dyG5m3jBa1Q9e8_MorCqvSUz_l7zVLCOISxY9iSg3c2iPrRGF8G7HQU5SJ_YikKOKIm87mT_tN40UJY3A1cd6Sp_Ews_XAGw9OXSzbkCusEAA0dxqf3GBQYTBneK7uw7LlIe_-ocoxKsWhqio5utyN_3iEA8BUbvB3bGtniGIGijv3yWX6Y7d_-CAaxFtF1xIXmXIzx_xZmxMJJftbpRVeEPf4WAZHNfrcBqiT4ATDUS8UNQ0pvfU1iiXZaYJEbtAYCUSU6G5xNojWCCHl6IlWG0qrSilvY8d4GpV6V1k4CMLWSs12kURZ515JgYjZrOWID8WU02oSteIQmw3ZHhZpppp9EHscuNwY4pmxRpvwi1N0wN_6vbsyBIIiZO2xEESFh9cFvnVYfK5f-sVHIjfUr3_pRssKwsApavvkuFGT3gcy7AFjye00-P-SkcmH8dJfKT8eSzmXKwTXEm7VnGVFl4bVX2pJ8Hl22xOqXGCePCpoC9kXasLIekMrwfBuAATjgzNRekwMtoobyMDuQrw8GPNB8UYPwwRtXxPDYkKJVaZR0QnhbFrCrO9a1LdIoGQ"
               // "http://image.hnol.net/c/2013-10/14/00/201310140015197351-4228429.jpg"
                  //"https://cdn.pixabay.com/photo/2023/12/12/10/32/christmas-8444992_640.png"
//                "https://p.kindpng.com/picc/s/191-1919587_flower-cut-flower-sweet-flower-vase-png-transparent.png"
            )
            vb.tvResult.text = "image type ${res.third} width ${res.first} height ${res.second}"
        }
    }
}

