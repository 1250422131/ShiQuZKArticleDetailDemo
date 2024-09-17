package com.imcys.shiquzkarticledetaildemo.ui.article.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imcys.shiquzkarticledetaildemo.R
import com.imcys.shiquzkarticledetaildemo.base.BaseActivity
import com.imcys.shiquzkarticledetaildemo.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : BaseActivity<ActivityArticleDetailBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun getViewBinding(): ActivityArticleDetailBinding =
        ActivityArticleDetailBinding.inflate(layoutInflater)
}