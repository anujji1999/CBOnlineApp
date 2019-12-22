package com.codingblocks.cbonlineapp.dashboard.mycourses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.SheetAdapter
import com.codingblocks.cbonlineapp.commons.SheetItem
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.bottom_sheet_mycourses.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_my_courses.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardMyCoursesFragment : Fragment() {

    private val dialog by lazy { BottomSheetDialog(requireContext()) }
    private val imgs by lazy { resources.obtainTypedArray(R.array.course_type_img) }
    private val coursesType by lazy { resources.getStringArray(R.array.course_type) }
    private val viewModel by viewModel<DashboardMyCoursesViewModel>()
    private val type = MutableLiveData<Int>()
    private val courseListAdapter = MyCourseListAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard_my_courses, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dashboardCourseShimmer.startShimmer()
        viewModel.fetchMyCourses()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomSheet()

        courseTypeTv.setOnClickListener {
            dialog.show()
        }
        type.observer(viewLifecycleOwner) {
            courseTypeTv.apply {
                //                TransitionManager.beginDelayedTransition(dashboardCourseRoot, ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN))
                text = coursesType[it]
                viewModel.courseFilter.postValue(coursesType[it])
                setCompoundDrawablesRelativeWithIntrinsicBounds(requireContext().getDrawable(imgs.getResourceId(it, 0)), null, requireContext().getDrawable(R.drawable.ic_dropdown), null)
            }
        }

        dashboardCoursesRv.setRv(requireContext(), courseListAdapter, true)


        viewModel.courses.observer(viewLifecycleOwner) {
            courseListAdapter.submitList(it)
            changeViewState(dashboardCoursesRv, internetll, emptyLl, dashboardCourseShimmer, it.isEmpty())

        }

        viewModel.errorLiveData.observer(viewLifecycleOwner) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
//                    showEmptyView(internetll, emptyLl, dashboardDoubtShimmer)
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(requireContext(), UNAUTHORIZED) {
                        requireActivity().finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {
                    dashboardCourseRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, dashboardBottomNav) {
                        viewModel.fetchMyCourses()
                    }
                }
            }
        }
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_mycourses, null)
        val list = arrayListOf<SheetItem>()
        repeat(5) {
            if (type.value == it)
                list.add(SheetItem(coursesType[it], imgs.getResourceId(it, 0), true))
            else
                list.add(SheetItem(coursesType[it], imgs.getResourceId(it, 0)))
        }
        sheetDialog.run {
            sheetLv.adapter = SheetAdapter(list)
            sheetLv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                type.postValue(position)
                dialog.dismiss()
            }
        }
        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}



