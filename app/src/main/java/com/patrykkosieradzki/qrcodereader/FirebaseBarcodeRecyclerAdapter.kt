package com.patrykkosieradzki.qrcodereader

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.animator.FlipAnimator
import kotlinx.android.synthetic.main.row_item.view.*

class FirebaseBarcodeRecyclerAdapter(options: FirebaseRecyclerOptions<QRCode>, mContext: Context)
    : FirebaseRecyclerAdapter<QRCode, ViewHolder>(options) {

    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()

    private var reverseAllAnimations: Boolean = false
    private var currentSelectedIndex: Int = -1

    private var mContext: Context = mContext
    private lateinit var mFirebaseBarcodeRecyclerAdapterListener: FirebaseBarcodeRecyclerAdapterListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QRCode) {
        holder.bind(model)


        holder.itemView.isActivated = selectedItems.get(position, false)


        applyClickEvents(holder, model, position)
        applyIconAnimation(holder, position)


    }

    private fun applyClickEvents(holder: ViewHolder, model: QRCode, position: Int) {
        holder.itemView.icon_container.setOnClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onIconClicked(model, position)
        })

        //holder.iconImp.setOnClickListener(View.OnClickListener { listener.onIconImportantClicked(position) })

        holder.itemView.content_container.setOnClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onMessageRowClicked(model, position)
        })

        holder.itemView.content_container.setOnLongClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onRowLongClicked(model, position)
            true
        })
    }


    fun toggleSelection(position: Int) {
        currentSelectedIndex = position
        if (isItemSelected(position)) {
            selectedItems.delete(position)
            animationItemsIndex.delete(position)
        } else {
            selectedItems.put(position, true)
            animationItemsIndex.put(position, true)
        }
        notifyItemChanged(position)
    }

    private fun isItemSelected(position: Int): Boolean = selectedItems.get(position, false)
    fun isSelection(): Boolean = selectedItems.size() > 0

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
        val front: RelativeLayout = holder.itemView.icon_front
        val back: RelativeLayout = holder.itemView.icon_back

        if (selectedItems.get(position, false)) {
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

    fun setOnClickListener(listener: FirebaseBarcodeRecyclerAdapterListener) {
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
