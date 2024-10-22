package com.example.dooch_wms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dooch_wms.databinding.ActivityProductionBinding

class A_product (val context: Context, val ProdList: ArrayList<D_production>) : BaseAdapter()
{
    private lateinit var productionBinding: ActivityProductionBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_prod_order, null)

        val prod_id = view.findViewById<TextView>(R.id.txt_lay_prod_id)
        val prod_name = view.findViewById<TextView>(R.id.txt_lay_prod_name)
        val prod_qty = view.findViewById<TextView>(R.id.txt_lay_prod_qty)

        val product = ProdList[position]
        prod_id.setText(product.id)
        prod_name.setText(product.name)
        prod_qty.setText(product.qty)

        return view
    }

    override fun getCount(): Int {
        return ProdList.size
    }

    override fun getItem(position: Int): Any {
        return ProdList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}