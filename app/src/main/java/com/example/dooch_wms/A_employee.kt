package com.example.dooch_wms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dooch_wms.databinding.ActivityEmployeeBinding

class A_employee (val context: Context, val EmpList: ArrayList<D_employee>) : BaseAdapter()
{
    private lateinit var employeeBinding: ActivityEmployeeBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_employee, null)

        val id = view.findViewById<TextView>(R.id.txt_lay_emp_id)
        val name = view.findViewById<TextView>(R.id.txt_lay_emp_name)

        val employee = EmpList[position]
        id.setText(employee.id)
        name.setText(employee.name)

        return view
    }

    override fun getCount(): Int {
        return EmpList.size
    }

    override fun getItem(position: Int): Any {
        return EmpList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}
