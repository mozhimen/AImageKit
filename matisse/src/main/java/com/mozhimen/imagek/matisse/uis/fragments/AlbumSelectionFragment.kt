package com.mozhimen.imagek.matisse.uis.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.mozhimen.kotlin.elemk.androidx.fragment.bases.BaseFragment
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.databinding.FragmentMediaSelectionBinding
import com.mozhimen.imagek.matisse.mos.Album
import com.mozhimen.imagek.matisse.cons.CImageKMatisse
import com.mozhimen.imagek.matisse.mos.Media
import com.mozhimen.imagek.matisse.mos.Selection
import com.mozhimen.imagek.matisse.commons.IAlbumLoadListener
import com.mozhimen.imagek.matisse.commons.IMediaCheckSelectSateListener
import com.mozhimen.imagek.matisse.commons.IMediaClickListener
import com.mozhimen.imagek.matisse.commons.IMediaSelectionProxyProvider
import com.mozhimen.imagek.matisse.helpers.loader.AlbumSelectionCursorLoaderCallbacks
import com.mozhimen.imagek.matisse.uis.adapters.MediaSelectionAdapter
import com.mozhimen.imagek.matisse.utils.MAX_SPAN_COUNT
import com.mozhimen.imagek.matisse.utils.spanCount
import com.mozhimen.imagek.matisse.widgets.MediaGridInset
import kotlin.math.max
import kotlin.math.min

class AlbumSelectionFragment : BaseFragment(), IAlbumLoadListener, IMediaCheckSelectSateListener, IMediaClickListener {

    companion object {
        fun newInstance(album: Album): AlbumSelectionFragment {
            val fragment = AlbumSelectionFragment()
            fragment.arguments = Bundle().apply { putParcelable(CImageKMatisse.EXTRA_ALBUM, album) }
            return fragment
        }
    }

    //////////////////////////////////////////////////////////////////////

    private val _albumSelectionCursorLoaderCallbacks = AlbumSelectionCursorLoaderCallbacks()
    private lateinit var _album: Album
    private lateinit var _mediaSelectionAdapter: MediaSelectionAdapter
    private lateinit var _mediaSelectionProvider: IMediaSelectionProxyProvider
    private lateinit var _onMediaCheckSelectSateListener: IMediaCheckSelectSateListener
    private lateinit var _onMediaClickListener: IMediaClickListener
    private var _vb: FragmentMediaSelectionBinding? = null

    //////////////////////////////////////////////////////////////////////

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IMediaSelectionProxyProvider) {
            _mediaSelectionProvider = context
        } else {
            throw IllegalStateException("Context must implement SelectionProvider.")
        }

        if (context is IMediaCheckSelectSateListener)
            _onMediaCheckSelectSateListener = context

        if (context is IMediaClickListener)
            _onMediaClickListener = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentMediaSelectionBinding.inflate(inflater, container, false)
        _vb = binding
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerview = _vb?.recyclerview
        recyclerview ?: return
        _album = arguments?.getParcelable(CImageKMatisse.EXTRA_ALBUM)!!
        UtilKLogWrapper.d(TAG, "onActivityCreated: _album $_album")

        _mediaSelectionAdapter = MediaSelectionAdapter(requireContext(), _mediaSelectionProvider.provideMediaSelectionProxy(), recyclerview)
        _mediaSelectionAdapter.onMediaCheckSelectSateListener = this
        _mediaSelectionAdapter.onMediaClickListener = this
        recyclerview.setHasFixedSize(true)

        val selection = Selection.getInstance()
        val spanCount = if (selection.gridExpectedSize > 0) {
            spanCount(requireContext(), selection.gridExpectedSize)
        } else {
            max(min(selection.gridSpanCount, MAX_SPAN_COUNT), 1)
        }

        recyclerview.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_media_grid)
        recyclerview.addItemDecoration(MediaGridInset(spanCount, spacing, false))
        recyclerview.itemAnimator?.changeDuration = 0
        recyclerview.adapter = _mediaSelectionAdapter
        _albumSelectionCursorLoaderCallbacks.onCreate(requireActivity(), this)
        _albumSelectionCursorLoaderCallbacks.loadAlbum(_album, selection.mediaCaptureEnable)
    }

    //////////////////////////////////////////////////////////////////////

    override fun onMediaClick(album: Album?, item: Media, adapterPosition: Int) {
        _onMediaClickListener.onMediaClick(_album, item, adapterPosition)
    }

    override fun onMediaSelectUpdate() {
        _onMediaCheckSelectSateListener.onMediaSelectUpdate()
    }

    override fun onAlbumStart() {
        // do nothing
    }

    override fun onAlbumLoad(cursor: Cursor) {
        _mediaSelectionAdapter.swapCursor(cursor)
    }

    override fun onAlbumReset() {
        _mediaSelectionAdapter.swapCursor(null)
    }

    override fun onDestroyView() {
        _albumSelectionCursorLoaderCallbacks.onDestroy()
        super.onDestroyView()
    }

    //////////////////////////////////////////////////////////////////////

    @SuppressLint("NotifyDataSetChanged")
    fun refreshSelectionAdapter() {
        _mediaSelectionAdapter.notifyDataSetChanged()
    }
}