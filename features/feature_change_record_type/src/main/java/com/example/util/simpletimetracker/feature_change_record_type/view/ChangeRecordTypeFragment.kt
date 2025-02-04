package com.example.util.simpletimetracker.feature_change_record_type.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.util.simpletimetracker.core.base.BaseFragment
import com.example.util.simpletimetracker.core.delegates.iconSelection.adapter.createIconSelectionAdapterDelegate
import com.example.util.simpletimetracker.core.delegates.iconSelection.adapter.createIconSelectionCategoryAdapterDelegate
import com.example.util.simpletimetracker.core.delegates.iconSelection.adapter.createIconSelectionCategoryInfoAdapterDelegate
import com.example.util.simpletimetracker.core.delegates.iconSelection.viewData.IconSelectionScrollViewData
import com.example.util.simpletimetracker.core.delegates.iconSelection.viewData.IconSelectionSelectorStateViewData
import com.example.util.simpletimetracker.core.delegates.iconSelection.viewData.IconSelectionStateViewData
import com.example.util.simpletimetracker.core.delegates.iconSelection.viewDelegate.IconSelectionViewDelegate
import com.example.util.simpletimetracker.core.dialog.ColorSelectionDialogListener
import com.example.util.simpletimetracker.core.dialog.DurationDialogListener
import com.example.util.simpletimetracker.core.dialog.EmojiSelectionDialogListener
import com.example.util.simpletimetracker.core.extension.addOnBackPressedListener
import com.example.util.simpletimetracker.core.extension.hideKeyboard
import com.example.util.simpletimetracker.core.extension.observeOnce
import com.example.util.simpletimetracker.core.extension.setSharedTransitions
import com.example.util.simpletimetracker.core.extension.showKeyboard
import com.example.util.simpletimetracker.core.extension.toViewData
import com.example.util.simpletimetracker.core.repo.DeviceRepo
import com.example.util.simpletimetracker.core.utils.fragmentArgumentDelegate
import com.example.util.simpletimetracker.core.view.UpdateViewChooserState
import com.example.util.simpletimetracker.domain.extension.orFalse
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_base_adapter.category.createCategoryAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.category.createCategoryAddAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.color.createColorAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.color.createColorPaletteAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.dayOfWeek.createDayOfWeekAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.divider.createDividerAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.emoji.createEmojiAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.empty.createEmptyAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.hint.createHintAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.hintBig.createHintBigAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.info.createInfoAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.recordType.RecordTypeViewData
import com.example.util.simpletimetracker.feature_change_record_type.goals.GoalsViewDelegate
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeCategoriesViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Category
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Closed
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Color
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.GoalTime
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Icon
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeGoalsViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewModel.ChangeRecordTypeViewModel
import com.example.util.simpletimetracker.feature_views.extension.dpToPx
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.example.util.simpletimetracker.feature_views.extension.visible
import com.example.util.simpletimetracker.navigation.params.screen.ChangeRecordTypeParams
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.util.simpletimetracker.feature_change_record_type.databinding.ChangeRecordTypeFragmentBinding as Binding

@AndroidEntryPoint
class ChangeRecordTypeFragment :
    BaseFragment<Binding>(),
    DurationDialogListener,
    EmojiSelectionDialogListener,
    ColorSelectionDialogListener {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    @Inject
    lateinit var deviceRepo: DeviceRepo

    private val viewModel: ChangeRecordTypeViewModel by viewModels()

    private val colorsAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createColorAdapterDelegate(viewModel::onColorClick),
            createColorPaletteAdapterDelegate(viewModel::onColorPaletteClick),
        )
    }
    private val iconsAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createLoaderAdapterDelegate(),
            createIconSelectionAdapterDelegate(viewModel::onIconClick),
            createEmojiAdapterDelegate(viewModel::onEmojiClick),
            createIconSelectionCategoryInfoAdapterDelegate(),
        )
    }
    private val iconCategoriesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createIconSelectionCategoryAdapterDelegate {
                viewModel.onIconCategoryClick(it)
                binding.containerChangeRecordTypeIcon.rvIconSelection.stopScroll()
            },
        )
    }
    private val categoriesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createCategoryAdapterDelegate(
                onClick = viewModel::onCategoryClick,
                onLongClickWithTransition = viewModel::onCategoryLongClick,
            ),
            createCategoryAddAdapterDelegate { throttle(viewModel::onAddCategoryClick).invoke() },
            createDividerAdapterDelegate(),
            createInfoAdapterDelegate(),
            createHintAdapterDelegate(),
            createHintBigAdapterDelegate(),
            createEmptyAdapterDelegate(),
        )
    }
    private val dailyGoalDayOfWeekAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createDayOfWeekAdapterDelegate(viewModel::onDayOfWeekClick),
        )
    }
    private var iconsLayoutManager: GridLayoutManager? = null
    private val params: ChangeRecordTypeParams by fragmentArgumentDelegate(
        key = ARGS_PARAMS,
        default = ChangeRecordTypeParams.New(ChangeRecordTypeParams.SizePreview()),
    )

    override fun initUi(): Unit = with(binding) {
        postponeEnterTransition()

        setPreview()

        setSharedTransitions(
            additionalCondition = { params !is ChangeRecordTypeParams.New },
            transitionName = (params as? ChangeRecordTypeParams.Change)?.transitionName.orEmpty(),
            sharedView = previewChangeRecordType,
        )

        rvChangeRecordTypeColor.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = colorsAdapter
        }

        iconsLayoutManager = IconSelectionViewDelegate.initUi(
            context = requireContext(),
            resources = resources,
            deviceRepo = deviceRepo,
            layout = containerChangeRecordTypeIcon,
            iconsAdapter = iconsAdapter,
            iconCategoriesAdapter = iconCategoriesAdapter,
        )

        rvChangeRecordTypeCategories.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = categoriesAdapter
        }

        GoalsViewDelegate.initGoalUi(
            layout = binding.layoutChangeRecordTypeGoals,
            dayOfWeekAdapter = dailyGoalDayOfWeekAdapter,
        )

        setOnPreDrawListener {
            startPostponedEnterTransition()
        }
    }

    override fun initUx() = with(binding) {
        etChangeRecordTypeName.doAfterTextChanged { viewModel.onNameChange(it.toString()) }
        fieldChangeRecordTypeColor.setOnClick(viewModel::onColorChooserClick)
        fieldChangeRecordTypeIcon.setOnClick(viewModel::onIconChooserClick)
        fieldChangeRecordTypeCategory.setOnClick(viewModel::onCategoryChooserClick)
        fieldChangeRecordTypeGoalTime.setOnClick(viewModel::onGoalTimeChooserClick)
        btnChangeRecordTypeSave.setOnClick(viewModel::onSaveClick)
        btnChangeRecordTypeDelete.setOnClick(viewModel::onDeleteClick)
        btnChangeRecordTypeStatistics.setOnClick(viewModel::onStatisticsClick)
        IconSelectionViewDelegate.initUx(
            viewModel = viewModel,
            layout = containerChangeRecordTypeIcon,
            iconsLayoutManager = iconsLayoutManager,
        )
        GoalsViewDelegate.initGoalUx(
            viewModel = viewModel,
            layout = layoutChangeRecordTypeGoals,
        )
        addOnBackPressedListener(action = viewModel::onBackPressed)
    }

    override fun initViewModel(): Unit = with(binding) {
        with(viewModel) {
            extra = params
            deleteIconVisibility.observeOnce(viewLifecycleOwner, btnChangeRecordTypeDelete::isVisible::set)
            statsIconVisibility.observeOnce(viewLifecycleOwner, btnChangeRecordTypeStatistics::isVisible::set)
            saveButtonEnabled.observe(btnChangeRecordTypeSave::setEnabled)
            deleteButtonEnabled.observe(btnChangeRecordTypeDelete::setEnabled)
            recordType.observeOnce(viewLifecycleOwner, ::updateUi)
            recordType.observe(::updatePreview)
            colors.observe(colorsAdapter::replace)
            icons.observe(::updateIconsState)
            iconCategories.observe(::updateIconCategories)
            iconsTypeViewData.observe(::updateIconsTypeViewData)
            iconSelectorViewData.observe(::updateIconSelectorViewData)
            expandIconTypeSwitch.observe { updateBarExpanded() }
            categories.observe(::updateCategories)
            goalsViewData.observe(::updateGoalsState)
            nameErrorMessage.observe(::updateNameErrorMessage)
            notificationsHintVisible.observe(
                layoutChangeRecordTypeGoals.containerChangeRecordTypeGoalNotificationsHint::visible::set,
            )
            chooserState.observe(::updateChooserState)
            keyboardVisibility.observe { visible ->
                if (visible) showKeyboard(etChangeRecordTypeName) else hideKeyboard()
            }
            iconsScrollPosition.observe {
                if (it is IconSelectionScrollViewData.ScrollTo) {
                    iconsLayoutManager?.scrollToPositionWithOffset(it.position, 0)
                    onScrolled()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onVisible()
        GoalsViewDelegate.onResume(binding.layoutChangeRecordTypeGoals)
    }

    override fun onDurationSet(duration: Long, tag: String?) {
        viewModel.onDurationSet(
            tag = tag,
            duration = duration,
            anchor = binding.btnChangeRecordTypeSave,
        )
    }

    override fun onDisable(tag: String?) {
        viewModel.onDurationDisabled(tag)
    }

    override fun onEmojiSelected(emojiText: String) {
        viewModel.onEmojiSelected(emojiText)
    }

    override fun onColorSelected(colorInt: Int) {
        viewModel.onCustomColorSelected(colorInt)
    }

    private fun updateUi(item: RecordTypeViewData) = with(binding) {
        etChangeRecordTypeName.setText(item.name)
        etChangeRecordTypeName.setSelection(item.name.length)
        IconSelectionViewDelegate.updateUi(
            icon = item.iconId,
            viewModel = viewModel,
            layout = containerChangeRecordTypeIcon,
        )
    }

    private fun updatePreview(item: RecordTypeViewData) {
        with(binding.previewChangeRecordType) {
            itemName = item.name
            itemIcon = item.iconId
            itemColor = item.color
        }
        with(binding) {
            viewChangeRecordTypePreviewBackground.backgroundTintList =
                ColorStateList.valueOf(item.color)
            layoutChangeRecordTypeColorPreview.setCardBackgroundColor(item.color)
            layoutChangeRecordTypeIconPreview.setCardBackgroundColor(item.color)
            iconChangeRecordTypeIconPreview.itemIcon = item.iconId
            layoutChangeRecordTypeCategoriesPreview.setCardBackgroundColor(item.color)
            layoutChangeRecordTypeGoalPreview.setCardBackgroundColor(item.color)
        }
    }

    private fun setPreview() {
        with(binding.previewChangeRecordType) {
            itemIsRow = params.sizePreview.asRow
            layoutParams = layoutParams.also { layoutParams ->
                params.sizePreview.width?.dpToPx()?.let { layoutParams.width = it }
                params.sizePreview.height?.dpToPx()?.let { layoutParams.height = it }
            }

            (params as? ChangeRecordTypeParams.Change)?.preview?.let {
                itemName = it.name
                itemIcon = it.iconId.toViewData()
                itemColor = it.color

                binding.viewChangeRecordTypePreviewBackground.backgroundTintList =
                    ColorStateList.valueOf(it.color)
                binding.layoutChangeRecordTypeColorPreview.setCardBackgroundColor(it.color)
                binding.layoutChangeRecordTypeIconPreview.setCardBackgroundColor(it.color)
                binding.iconChangeRecordTypeIconPreview.itemIcon = it.iconId.toViewData()
                binding.layoutChangeRecordTypeCategoriesPreview.setCardBackgroundColor(it.color)
                binding.layoutChangeRecordTypeGoalPreview.setCardBackgroundColor(it.color)
            }
        }
    }

    private fun updateChooserState(state: ChangeRecordTypeChooserState) = with(binding) {
        updateChooser<Color>(
            state = state,
            chooserData = rvChangeRecordTypeColor,
            chooserView = fieldChangeRecordTypeColor,
            chooserArrow = arrowChangeRecordTypeColor,
        )
        updateChooser<Icon>(
            state = state,
            chooserData = containerChangeRecordTypeIcon.root,
            chooserView = fieldChangeRecordTypeIcon,
            chooserArrow = arrowChangeRecordTypeIcon,
        )
        updateChooser<Category>(
            state = state,
            chooserData = rvChangeRecordTypeCategories,
            chooserView = fieldChangeRecordTypeCategory,
            chooserArrow = arrowChangeRecordTypeCategory,
        )
        updateChooser<GoalTime>(
            state = state,
            chooserData = containerChangeRecordTypeGoalTime,
            chooserView = fieldChangeRecordTypeGoalTime,
            chooserArrow = arrowChangeRecordTypeGoalTime,
        )

        val isClosed = state.current is Closed
        inputChangeRecordTypeName.isVisible = isClosed
        btnChangeRecordTypeStatistics.isVisible =
            viewModel.statsIconVisibility.value.orFalse() && isClosed
        btnChangeRecordTypeDelete.isVisible =
            viewModel.deleteIconVisibility.value.orFalse() && isClosed
        dividerChangeRecordTypeBottom.isVisible = !isClosed

        // Chooser fields
        fieldChangeRecordTypeColor.isVisible = isClosed || state.current is Color
        fieldChangeRecordTypeIcon.isVisible = isClosed || state.current is Icon
        fieldChangeRecordTypeCategory.isVisible = isClosed || state.current is Category
        fieldChangeRecordTypeGoalTime.isVisible = isClosed || state.current is GoalTime
    }

    private fun updateGoalsState(state: ChangeRecordTypeGoalsViewData) = with(binding) {
        GoalsViewDelegate.updateGoalsState(
            state = state,
            layout = layoutChangeRecordTypeGoals,
        )
        layoutChangeRecordTypeGoalPreview.isVisible = state.selectedCount > 0
        tvChangeRecordTypeGoalPreview.text = state.selectedCount.toString()
    }

    private fun updateCategories(
        data: ChangeRecordTypeCategoriesViewData,
    ) = with(binding) {
        categoriesAdapter.replace(data.viewData)
        layoutChangeRecordTypeCategoriesPreview.isVisible = data.selectedCount > 0
        tvChangeRecordTypeCategoryPreview.text = data.selectedCount.toString()
    }

    private fun updateBarExpanded() {
        IconSelectionViewDelegate.updateBarExpanded(
            layout = binding.containerChangeRecordTypeIcon,
        )
    }

    private fun updateIconsState(state: IconSelectionStateViewData) {
        IconSelectionViewDelegate.updateIconsState(
            state = state,
            layout = binding.containerChangeRecordTypeIcon,
            iconsAdapter = iconsAdapter,
        )
    }

    private fun updateIconCategories(data: List<ViewHolderType>) {
        IconSelectionViewDelegate.updateIconCategories(
            data = data,
            iconCategoriesAdapter = iconCategoriesAdapter,
        )
    }

    private fun updateIconSelectorViewData(
        data: IconSelectionSelectorStateViewData,
    ) {
        IconSelectionViewDelegate.updateIconSelectorViewData(
            data = data,
            layout = binding.containerChangeRecordTypeIcon,
        )
    }

    private fun updateIconsTypeViewData(data: List<ViewHolderType>) {
        IconSelectionViewDelegate.updateIconsTypeViewData(
            data = data,
            layout = binding.containerChangeRecordTypeIcon,
        )
    }

    private fun updateNameErrorMessage(error: String) = with(binding) {
        inputChangeRecordTypeName.error = error
        inputChangeRecordTypeName.isErrorEnabled = error.isNotEmpty()
    }

    private inline fun <reified T : State> updateChooser(
        state: ChangeRecordTypeChooserState,
        chooserData: View,
        chooserView: CardView,
        chooserArrow: View,
    ) {
        UpdateViewChooserState.updateChooser<State, T, Closed>(
            stateCurrent = state.current,
            statePrevious = state.previous,
            chooserData = chooserData,
            chooserView = chooserView,
            chooserArrow = chooserArrow,
        )
    }

    companion object {
        private const val ARGS_PARAMS = "args_params"

        fun createBundle(data: ChangeRecordTypeParams): Bundle = Bundle().apply {
            putParcelable(ARGS_PARAMS, data)
        }
    }
}