package com.mozhimen.imagek.matisse.uis.fragments

import android.os.Bundle
import android.util.Log
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mozhimen.kotlin.utilk.wrapper.UtilKScreen
import com.mozhimen.imagek.matisse.R
import com.mozhimen.imagek.matisse.bases.BaseBottomSheetDialogFragment
import com.mozhimen.imagek.matisse.commons.IAlbumBottomSheetListener
import com.mozhimen.imagek.matisse.cons.CImageKMatisse
import com.mozhimen.imagek.matisse.uis.adapters.AlbumSelectionAdapter

class AlbumSelectionBottomSheetDialogFragment : BaseBottomSheetDialogFragment() {
    companion object {
        fun newInstance(currentPos: Int): AlbumSelectionBottomSheetDialogFragment {
            val albumSelectionBottomSheetDialogFragment = AlbumSelectionBottomSheetDialogFragment()
            albumSelectionBottomSheetDialogFragment.arguments = Bundle().apply {
                putInt(CImageKMatisse.FOLDER_CHECK_POSITION, currentPos)
            }
            return albumSelectionBottomSheetDialogFragment
        }
    }

    ///////////////////////////////////////////////////////////////////

    private var _containerView: View? = null
    private var _currentPosition = 0
    private lateinit var _recyclerView: RecyclerView

    var albumSelectionAdapter: AlbumSelectionAdapter? = null
    var folderBottomSheetListener: IAlbumBottomSheetListener? = null

    ///////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _currentPosition = arguments?.getInt(CImageKMatisse.FOLDER_CHECK_POSITION, 0) ?: 0
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup): View {
        if (_containerView == null) {
            _containerView = inflater.inflate(R.layout.fragment_dialog_bottom_sheet_album, container, false)
            setDefaultHeight(UtilKScreen.getHeight_ofDisplayMetrics_ofDef() / 2)
            initView()
        } else {
            if (_containerView?.parent != null) {
                val parent = _containerView?.parent as ViewGroup
                parent.removeView(view)
            }
        }
        return _containerView!!
    }

    ///////////////////////////////////////////////////////////////////

    private fun initView() {
        _recyclerView = _containerView?.findViewById(R.id.recyclerview)!!
        _recyclerView.layoutManager = LinearLayoutManager(context)
        _recyclerView.setHasFixedSize(true)
        _recyclerView.layoutParams.height = UtilKScreen.getHeight_ofDisplayMetrics_ofSys() / 2
        albumSelectionAdapter = AlbumSelectionAdapter(requireContext(), _currentPosition).apply {
            _recyclerView.adapter = this
            folderBottomSheetListener?.onInitData(this)

            itemClickListener = object : AlbumSelectionAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    dismiss()
                    if (albumList.isNotEmpty()){
                        UtilKLogWrapper.d(TAG, "onItemClick: albumList size ${albumList.size}")
                    }
                    folderBottomSheetListener?.onItemClick(albumList[position], position)
                }
            }
        }
    }
}