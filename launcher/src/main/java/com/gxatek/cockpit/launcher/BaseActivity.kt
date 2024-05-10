package com.gxatek.cockpit.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.exa.baselib.utils.L
import com.exa.baselib.utils.SystemBarUtil

/**
 * @Author lsh
 * @Date 2023/6/27 10:44
 * @Description
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    private lateinit var _binding: T
    protected val binding get() = _binding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.dd()
        _binding = getViewBinding()
        setContentView(_binding.root)
        initView()
        initData()
    }

    protected abstract fun getViewBinding(): T

    protected abstract fun initView()

    protected abstract fun initData()
}
