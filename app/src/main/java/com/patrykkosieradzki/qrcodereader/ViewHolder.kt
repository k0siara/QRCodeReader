package com.patrykkosieradzki.qrcodereader

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.patrykkosieradzki.qrcodereader.model.QRCode
import kotlinx.android.synthetic.main.row_item.view.*

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(qrCode: QRCode) {
        with(itemView) {
            title.text = qrCode.text
            description.text = qrCode.text
            secondary_description.text = qrCode.type
        }

        setTypeIcon(qrCode.type)
    }

    private fun setTypeIcon(type: String) {
        when (type) {
            "ADDRESSBOOK" -> setImage(R.drawable.ic_person_black_24dp)
            "EMAIL_ADDRESS" -> setImage(R.drawable.ic_email_black_24dp)
            "PRODUCT" -> setImage(R.drawable.ic_attach_money_black_24dp)
            "URI" -> setImage(R.drawable.ic_website_black_24dp)
            "TEXT" -> setImage(R.drawable.ic_text_fields_black_24dp)
            "GEO" -> setImage(R.drawable.ic_location_on_black_24dp)
            "TEL" -> setImage(R.drawable.ic_phone_black_24dp)
            "SMS" -> setImage(R.drawable.ic_sms_black_24dp)
            "CALENDAR" -> setImage(R.drawable.ic_date_range_black_24dp)
            "WIFI" -> setImage(R.drawable.ic_network_wifi_black_24dp)
            "ISBN" -> setImage(R.drawable.ic_library_books_black_24dp)
            "VIN" -> setImage(R.drawable.ic_directions_car_black_24dp)
        }
    }

    private fun setImage(drawable: Int, imageView: ImageView = itemView.icon_image) {
        imageView.setImageResource(drawable)
    }

}