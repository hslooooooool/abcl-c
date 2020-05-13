package vip.qsos.demo.netservice.ui.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.main_fragment.*
import qsos.lib.base.base.fragment.BaseFragment
import vip.qsos.demo.netservice.R

class MainFragment : BaseFragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun initData(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun initView(view: View) {
        viewModel.userInfo.observe(this, Observer {
            message.text = it.toString()
        })
    }

    override fun getData(loadMore: Boolean) {
        viewModel.loadUserInfo()
    }

}
