package com.gxatek.cockpit.shortcut.allMenu

import android.view.IRemoteAnimationFinishedCallback
import android.view.IRemoteAnimationRunner
import android.view.RemoteAnimationAdapter
import android.view.RemoteAnimationDefinition
import android.view.RemoteAnimationTarget
import android.view.WindowManager
import com.exa.baselib.base.BaseViewBindingActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.SystemBarUtil
import com.exa.companyclient.databinding.TestLayoutBinding

/**
 * @author lsh
 * @date 2024/9/13 11:28
 * @description
 */
class AllMenuPanelActivity : BaseViewBindingActivity<TestLayoutBinding>() {

    override fun initView() {
        SystemBarUtil.setInvasionSystemBars(this)
    }

    class TaskFragmentAnimationRunner: IRemoteAnimationRunner.Stub(){
        override fun onAnimationStart(
            p0: Int,
            p1: Array<out RemoteAnimationTarget>?,
            p2: Array<out RemoteAnimationTarget>?,
            p3: Array<out RemoteAnimationTarget>?,
            p4: IRemoteAnimationFinishedCallback?
        ) {
            L.dd()
        }

        override fun onAnimationCancelled() {
            L.dd()
        }
    }

    override fun initData() {
        val mDefinition = RemoteAnimationDefinition()
        val mRemoteRunner = TaskFragmentAnimationRunner()
        val animationAdapter =
            RemoteAnimationAdapter(mRemoteRunner, 0, 0, true /* changeNeedsSnapshot */)
        mDefinition.addRemoteAnimation(6, animationAdapter)
        mDefinition.addRemoteAnimation(28, animationAdapter)
        mDefinition.addRemoteAnimation(8, animationAdapter)
        mDefinition.addRemoteAnimation(7, animationAdapter)
        mDefinition.addRemoteAnimation(29, animationAdapter)
        mDefinition.addRemoteAnimation(9, animationAdapter)
        mDefinition.addRemoteAnimation(30, animationAdapter)
//        registerRemoteAnimations(mDefinition)
    }

    override fun getViewBinding(): TestLayoutBinding {
        window.addFlags(WindowManager.LayoutParams.FLAG_DITHER)
        return TestLayoutBinding.inflate(layoutInflater)
    }
}