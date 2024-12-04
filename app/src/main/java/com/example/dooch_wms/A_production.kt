package com.example.dooch_wms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dooch_wms.databinding.LayoutProdOrderBinding

class A_production (val context: Context, val ProdList: ArrayList<D_production>) : BaseAdapter()
{
    // findviewbyid를 사용하지 않고 viewbinding을 사용하여 어댑터 코드 작성
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding : LayoutProdOrderBinding
        val view : View

        if(convertView == null){
            binding = LayoutProdOrderBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as LayoutProdOrderBinding
            view = convertView
        }

        val product = ProdList[position]
        binding.txtLayOrderId.text = product.order_id
        binding.txtLayProdId.text = product.item_id
        binding.txtLayProdName.text = product.item_name
        binding.txtLayProdQty.text = product.item_qty

        return view

//        viewbinding을 사요하지 않고 findviewbyid를 사용하여 view를 만들고 반환 하는 코드
//        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_prod_order, null)
//
//        val order_id = view.findViewById<TextView>(R.id.txt_lay_order_id)
//        val item_id = view.findViewById<TextView>(R.id.txt_lay_prod_id)
//        val item_name = view.findViewById<TextView>(R.id.txt_lay_prod_name)
//        val item_qty = view.findViewById<TextView>(R.id.txt_lay_prod_qty)
//
//
//        val product = ProdList[position]
//        order_id.setText(product.order_id)
//        item_id.setText(product.item_id)
//        item_name.setText(product.item_name)
//        item_qty.setText(product.item_qty)
//
//        return view
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