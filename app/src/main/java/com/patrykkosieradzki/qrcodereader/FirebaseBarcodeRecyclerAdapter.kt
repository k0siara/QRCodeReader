package com.patrykkosieradzki.qrcodereader

import android.content.Context
import android.content.Intent
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.ui.details.DetailsActivity
import com.patrykkosieradzki.qrcodereader.animator.FlipAnimator

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

        holder.setTypeIcon(model.type)
        holder.mTitle.text = model.text
        holder.mDescription.text = model.type
        holder.mText.text = model.text

        holder.itemView.isActivated = selectedItems.get(position, false)


        // TODO: remove in future
        holder.mMessageContainer.setOnClickListener { view ->
            val intent = Intent(view.context, DetailsActivity::class.java)
            intent.putExtra("text", model.text)
            intent.putExtra("type", model.type)
            view.context.startActivity(intent)
        }

        applyClickEvents(holder, model, position)


        applyIconAnimation(holder, position)


    }

    private fun applyClickEvents(holder: ViewHolder, model: QRCode, position: Int) {
        holder.iconContainer.setOnClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onIconClicked(model, position)
        })

        //holder.iconImp.setOnClickListener(View.OnClickListener { listener.onIconImportantClicked(position) })

        holder.mMessageContainer.setOnClickListener({
            mFirebaseBarcodeRecyclerAdapterListener.onMessageRowClicked(model, position)
        })

        holder.mMessageContainer.setOnLongClickListener({
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
    public fun isSelection(): Boolean = selectedItems.size() > 0

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1f
            if (currentSelectedIndex === position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.visibility = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex.get(position, false) || currentSelectedIndex === position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false)
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
