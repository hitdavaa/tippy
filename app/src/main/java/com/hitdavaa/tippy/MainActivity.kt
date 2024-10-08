package com.hitdavaa.tippy

import android.animation.ArgbEvaluator
import android.health.connect.datatypes.units.Percentage
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.temporal.TemporalAmount

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var sbTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etBaseAmount = findViewById(R.id.etBaseAmount);
        sbTip = findViewById(R.id.sbTip);
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel);
        tvTipAmount = findViewById(R.id.tvTipAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTipDescription = findViewById(R.id.tvTipDescription);
        sbTip.progress = INITIAL_TIP_PERCENT;
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%";
        updateTipDescription(INITIAL_TIP_PERCENT);

        sbTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                tvTipPercentLabel.text = "$p1%";
                computeTipAndTotal();
                updateTipDescription(p1);
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0");
                computeTipAndTotal();

            }


        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        var tipDescription = when(tipPercent){
            in 0..9 -> "Poor";
            in 10..14 -> "Acceptable";
            in 15..20 -> "Good";
            in 20..24 -> "Great";
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription;
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat()/sbTip.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int
        tvTipDescription.setTextColor(color);
    }

    private fun computeTipAndTotal() {
        if(etBaseAmount.text.isEmpty()){
            tvTipAmount.text = "";
            tvTotalAmount.text = "";
            return;
        }
        val baseAmount = etBaseAmount.text.toString().toDouble();
        val tipPercent = sbTip.progress.toString().toDouble();

        val tipAmount = (tipPercent/100) * baseAmount;
        val totalAmount = tipAmount + baseAmount;
        tvTipAmount.text = "%.2f".format(tipAmount);
        tvTotalAmount.text = "%.2f".format(totalAmount);
    }
}