package com.github.satoshun.example.sample

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.github.satoshun.example.sample.databinding.MainActBinding

class MainActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<MainActBinding>(this, R.layout.main_act)

    if (savedInstanceState == null) {
      supportFragmentManager.commit {
        add(R.id.fragment, MainFragment())
      }
    }
  }
}
