package com.solarnet.demo.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import com.solarnet.demo.MainActivity
import com.solarnet.demo.R
import com.solarnet.demo.adapter.PaymentGridViewAdapter
import com.solarnet.demo.adapter.TrxListAdapter
import com.solarnet.demo.data.GridItem
import com.solarnet.demo.data.trx.Trx
import com.solarnet.demo.data.trx.TrxViewModel
import com.solarnet.demo.data.trx.TrxViewModelFactory
import com.solarnet.demo.design.NoScrollLinearLayoutManager
import java.util.*


class PaymentFragment : Fragment() {
    private val NUM_COLUMNS = 4
    private val LIMIT_TRX = 5
    private lateinit var recyclerView : RecyclerView
    private var mOnScrollListener : MainActivity.OnScrollListener? = null
    private var mTrxViewModel : TrxViewModel? = null
    private lateinit var mTrxListAdapter : TrxListAdapter

    // Store instance variables based on arguments passed
    // newInstance constructor for creating fragment with arguments
    companion object {
        fun newInstance(): PaymentFragment {
            return PaymentFragment()
        }
    }

    fun setOnScrollListener(onScrollListener: MainActivity.OnScrollListener) {
        mOnScrollListener = onScrollListener
    }

    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

//        recyclerView = view.findViewById<RecyclerView>(R.id.recylerView)
//        recyclerView.layoutManager = GridLayoutManager(context, NUM_COLUMNS)
//        recyclerView.isNestedScrollingEnabled = false
//        recyclerView.setHasFixedSize(true)
        var data = ArrayList<GridItem>()
        data.add(GridItem(R.drawable.ic_send_money, context!!.resources.getString(R.string.send_money)))
        data.add(GridItem(R.drawable.ic_cellular, context!!.resources.getString(R.string.cellular)))
        data.add(GridItem(R.drawable.ic_pln, context!!.resources.getString(R.string.pln)))
        data.add(GridItem(R.drawable.ic_invoice, context!!.resources.getString(R.string.invoice)))
        data.add(GridItem(R.drawable.ic_bank, context!!.resources.getString(R.string.transfer_to_bank)))
        data.add(GridItem(R.drawable.ic_qr, context!!.resources.getString(R.string.scan_qr)))
        data.add(GridItem(R.drawable.ic_product, context!!.resources.getString(R.string.create_product)))
        data.add(GridItem(R.drawable.ic_agent, context!!.resources.getString(R.string.new_agent)))

//        recyclerView.adapter = PaymentGridAdapter(data)
        var adapter = PaymentGridViewAdapter(context!!, data)
        val gridView = view.findViewById<GridView>(R.id.gridView)
        gridView.numColumns = NUM_COLUMNS
        gridView.adapter = adapter

        setGridViewHeightBasedOnChildren(gridView, NUM_COLUMNS)

        var scrollView = view as NestedScrollView
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
            _, _, scrollY, _, oldScrollY ->
            mOnScrollListener?.onScrollChange(scrollY, scrollY - oldScrollY)
        })

        var trx = ArrayList<Trx>()
        mTrxListAdapter = TrxListAdapter(context!!, trx)
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val lm = NoScrollLinearLayoutManager(context!!,
                LinearLayoutManager.VERTICAL, false)
//        val lm = LinearLayoutManager(context!!)
//        lm.isAutoMeasureEnabled = true
        recyclerView.layoutManager = lm
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = mTrxListAdapter

        mTrxViewModel =  ViewModelProviders.of(this,
                TrxViewModelFactory(activity!!.application, LIMIT_TRX))
                .get(TrxViewModel::class.java)
        mTrxViewModel?.getAllTransactions()?.observe(this, Observer<List<Trx>> {allTrx ->
            if (allTrx != null) {
                mTrxListAdapter.setItems(allTrx)
            } else {
                mTrxListAdapter.setItems(ArrayList<Trx>())
            }
            mTrxListAdapter.notifyDataSetChanged()
        })

        return view
    }

    private fun setGridViewHeightBasedOnChildren(gridView: GridView, columns: Int) {
        val listAdapter = gridView.adapter
                ?: // pre-condition
                return

        val items = listAdapter.count

        val listItem = listAdapter.getView(0, null, gridView)
        listItem.measure(0, 0)
        var totalHeight = listItem.measuredHeight +
                resources.getDimensionPixelSize(R.dimen.item_grid_vspacing)
        val rows = Math.ceil(items.toDouble() / columns.toDouble()).toInt()
        totalHeight *= rows

        val params = gridView.layoutParams
        params.height = totalHeight
        gridView.layoutParams = params

    }
}