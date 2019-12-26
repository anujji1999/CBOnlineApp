package com.codingblocks.cbonlineapp.mycourse.player.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.OnItemClickListener
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.library.LibraryNotesListAdapter
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.fragment_notes.view.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoNotesFragment : Fragment(), AnkoLogger {

    private val viewModel by sharedViewModel<VideoPlayerViewModel>()
    private val notesListAdapter = LibraryNotesListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notes, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchNotes()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerNotesRv.setRv(requireContext(), notesListAdapter, true, "thick")

        viewModel.notes.observer(viewLifecycleOwner) {
            notesListAdapter.submitList(it)
        }
    }

}