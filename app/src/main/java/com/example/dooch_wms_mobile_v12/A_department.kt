package com.example.dooch_wms_mobile_v12

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class A_department (val context: Context, val DeptList: ArrayList<D_department>) : BaseAdapter()
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_department, null)

        val dept_id = view.findViewById<TextView>(R.id.txt_lay_dept_id)
        val dept_name = view.findViewById<TextView>(R.id.txt_lay_dept_name)

        val employee = DeptList[position]
        dept_id.text = employee.dept_id
        dept_name.text = employee.dept_name

        return view
    }

    override fun getCount(): Int {
        return DeptList.size
    }

    override fun getItem(position: Int): Any {
        return DeptList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}