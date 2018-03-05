package com.patrykkosieradzki.qrcodereader

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var mMessageContainer: LinearLayout = view.findViewById(R.id.message_container)
    var iconContainer: RelativeLayout = view.findViewById(R.id.icon_container)

    var mIcon: ImageView = view.findViewById(R.id.icon_profile)
    var mTitle: TextView = view.findViewById(R.id.from)
    var mDescription: TextView = view.findViewById(R.id.txt_primary)
    var mText: TextView = view.findViewById(R.id.txt_secondary)

    var iconBack: RelativeLayout = view.findViewById(R.id.icon_back)
    var iconFront: RelativeLayout = view.findViewById(R.id.icon_front)


    fun setTypeIcon(type: String) {
        when (type) {
            "ADDRESSBOOK" -> mIcon.setImageResource(R.drawable.ic_person_black_24dp)
            "EMAIL_ADDRESS" -> mIcon.setImageResource(R.drawable.ic_email_black_24dp)
            "PRODUCT" -> mIcon.setImageResource(R.drawable.ic_attach_money_black_24dp)
            "URI" -> mIcon.setImageResource(R.drawable.ic_website_black_24dp)
            "TEXT" -> mIcon.setImageResource(R.drawable.ic_text_fields_black_24dp)
            "GEO" -> mIcon.setImageResource(R.drawable.ic_location_on_black_24dp)
            "TEL" -> mIcon.setImageResource(R.drawable.ic_phone_black_24dp)
            "SMS" -> mIcon.setImageResource(R.drawable.ic_sms_black_24dp)
            "CALENDAR" -> mIcon.setImageResource(R.drawable.ic_date_range_black_24dp)
            "WIFI" -> mIcon.setImageResource(R.drawable.ic_network_wifi_black_24dp)
            "ISBN" -> mIcon.setImageResource(R.drawable.ic_library_books_black_24dp)
            "VIN" -> mIcon.setImageResource(R.drawable.ic_directions_car_black_24dp)
        }


    }

}