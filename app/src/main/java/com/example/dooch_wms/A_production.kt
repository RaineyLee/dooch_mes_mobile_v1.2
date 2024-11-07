package com.example.dooch_wms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dooch_wms.databinding.ActivityProductionBinding
import com.example.dooch_wms.databinding.LayoutProdOrderBinding

class A_production (val context: Context, val ProdList: ArrayList<D_production>) : BaseAdapter()
{
    private lateinit var LayoutProdOrderBinding: ActivityProductionBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_prod_order, null)

        val order_id = view.findViewById<TextView>(R.id.txt_lay_order_id)
        val item_id = view.findViewById<TextView>(R.id.txt_lay_prod_id)
        val item_name = view.findViewById<TextView>(R.id.txt_lay_prod_name)
        val item_qty = view.findViewById<TextView>(R.id.txt_lay_prod_qty)

        val product = ProdList[position]
        order_id.setText(product.order_id)
        item_id.setText(product.item_id)
        item_name.setText(product.item_name)
        item_qty.setText(product.item_qty)

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