package com.example.util.simpletimetracker.feature_statistics_detail.view

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.transition.TransitionInflater
import com.example.util.simpletimetracker.core.base.BaseFragment
import com.example.util.simpletimetracker.core.di.BaseViewModelFactory
import com.example.util.simpletimetracker.core.dialog.StandardDialogListener
import com.example.util.simpletimetracker.core.extension.setOnClick
import com.example.util.simpletimetracker.core.utils.BuildVersions
import com.example.util.simpletimetracker.core.view.TransitionNames
import com.example.util.simpletimetracker.domain.extension.orZero
import com.example.util.simpletimetracker.feature_statistics_detail.R
import com.example.util.simpletimetracker.feature_statistics_detail.di.StatisticsDetailComponentProvider
import com.example.util.simpletimetracker.feature_statistics_detail.extra.StatisticsDetailExtra
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailChartViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewModel.StatisticsDetailViewModel
import com.example.util.simpletimetracker.navigation.params.StatisticsDetailParams
import kotlinx.android.synthetic.main.statistics_detail_fragment.*
import javax.inject.Inject

class StatisticsDetailFragment : BaseFragment(R.layout.statistics_detail_fragment),
    StandardDialogListener {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<StatisticsDetailViewModel>

    private val viewModel: StatisticsDetailViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )
    private val typeId: Long by lazy { arguments?.getLong(ARGS_TYPE_ID).orZero() }

    override fun initDi() {
        (activity?.application as StatisticsDetailComponentProvider)
            .statisticsDetailComponent
            ?.inject(this)
    }

    override fun initUi() {
        if (BuildVersions.isLollipopOrHigher()) {
            sharedElementEnterTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
        }

        ViewCompat.setTransitionName(
            layoutStatisticsDetailItem,
            TransitionNames.STATISTICS_DETAIL + typeId
        )
    }

    override fun initUx() {
        btnStatisticsDetailChartDaily.setOnClick(viewModel::onChartDailyClick)
        btnStatisticsDetailChartWeekly.setOnClick(viewModel::onChartWeeklyClick)
        btnStatisticsDetailChartMonthly.setOnClick(viewModel::onChartMonthlyClick)
    }

    override fun initViewModel(): Unit = with(viewModel) {
        extra = StatisticsDetailExtra(
            typeId = typeId
        )
        viewData.observe(viewLifecycleOwner, ::updateViewData)
        chartViewData.observe(viewLifecycleOwner, ::updateChartViewData)
    }

    private fun updateViewData(viewData: StatisticsDetailViewData) {
        tvStatisticsDetailItemName.text = viewData.name
        layoutStatisticsDetailItem.setCardBackgroundColor(viewData.color)
        ivStatisticsDetailItemIcon.setBackgroundResource(viewData.iconId)

        tvStatisticsDetailTotalDuration.text = viewData.totalDuration
        tvStatisticsDetailTimesTracked.text = viewData.timesTracked
        chartStatisticsDetail.setBarColor(viewData.color)
    }

    private fun updateChartViewData(viewData: StatisticsDetailChartViewData) {
        chartStatisticsDetail.setBars(viewData.data)
        btnStatisticsDetailChartDaily.backgroundTintList =
            ColorStateList.valueOf(viewData.dailyButtonColor)
        btnStatisticsDetailChartWeekly.backgroundTintList =
            ColorStateList.valueOf(viewData.weeklyButtonColor)
        btnStatisticsDetailChartMonthly.backgroundTintList =
            ColorStateList.valueOf(viewData.monthlyButtonColor)
    }

    companion object {
        private const val ARGS_TYPE_ID = "args_type_id"

        fun createBundle(data: Any?): Bundle = Bundle().apply {
            when (data) {
                is StatisticsDetailParams -> putLong(ARGS_TYPE_ID, data.typeId)
            }
        }
    }
}
