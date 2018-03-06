package com.patrykkosieradzki.qrcodereader.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.patrykkosieradzki.qrcodereader.R
import com.patrykkosieradzki.qrcodereader.ViewHolder
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.animator.FlipAnimator
import com.patrykkosieradzki.qrcodereader.extensions.contains
import com.patrykkosieradzki.qrcodereader.extensions.inflate
import kotlinx.android.synthetic.main.row_item.view.*

class BarcodeListAdapter(options: FirebaseRecyclerOptions<QRCode>, private val mContext: Context)
    : FirebaseRecyclerAdapter<QRCode, ViewHolder>(options) {

    interface OnClickListener {
        fun onIconClick(model: QRCode, position: Int)
        fun onContentClick(model: QRCode, position: Int)
        fun onContentLongClick(model: QRCode, position: Int)
    }

    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()

    private var reverseAllAnimations: Boolean = false
    private var currentSelectedIndex: Int = -1

    private lateinit var mFirebaseBarcodeRecyclerAdapterListener: OnClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.row_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QRCode) {
        holder.bind(model)

        applyClickEvents(holder, model, position)
        applyIconAnimation(holder, position)
    }

    private fun applyClickEvents(holder: ViewHolder, model: QRCode, position: Int) {
        holder.itemView.icon_container.setOnClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onIconClick(model, position)
        })

        holder.itemView.content_container.setOnClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onContentClick(model, position)
        })

        holder.itemView.content_container.setOnLongClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onContentLongClick(model, position)
            true
        })
    }


    fun toggleSelection(position: Int) {
        currentSelectedIndex = position
        if (selectedItems.contains(position)) {
            selectedItems.delete(position)
            animationItemsIndex.delete(position)
        } else {
            selectedItems.put(position, true)
            animationItemsIndex.put(position, true)
        }
        notifyItemChanged(position)
    }

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
        val front: RelativeLayout = holder.itemView.icon_front
        val back: RelativeLayout = holder.itemView.icon_back

        if (selectedItems.contains(position)) {
            front.visibility = View.GONE
            resetIconYAxis(back)
            back.visibility = View.VISIBLE
            back.alpha = 1f
            if (currentSelectedIndex === position) {
                FlipAnimator.flipView(mContext, back, front, true)
                resetCurrentIndex()
            }
        } else {
            back.visibility = View.GONE
            resetIconYAxis(front)
            front.visibility = View.VISIBLE
            front.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex.get(position, false) || currentSelectedIndex === position) {
                FlipAnimator.flipView(mContext, back, front, false)
                resetCurrentIndex()
            }
        }
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    fun setOnClickListener(listener: OnClickListener) {
        mFirebaseBarcodeRecyclerAdapterListener = listener
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }


}
