package com.example.healthymind.ui.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.healthymind.R

class MyContactListAdapter(var mCtx:Context , var resource:Int,var items:List<ContactModel>)
    :ArrayAdapter<ContactModel>( mCtx , resource , items ){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(mCtx)

        val view : View = layoutInflater.inflate(resource , null )
        val imageView :ImageView = view.findViewById(R.id.icontcIv)
        var textView : TextView = view.findViewById(R.id.nameTv)
        var textView1 : TextView = view.findViewById(R.id.numberTv)


        var person : ContactModel = items[position]

        imageView.setImageDrawable(mCtx.resources.getDrawable(person.photo))
        textView.text = person.title
        textView1.text = person.desc


        return view
    }

}