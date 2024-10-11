package com.hitdavaa.tippy

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_SPLIT_NUMBER = 1
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var sbTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var spinnerLanguage: Spinner
    private lateinit var tvBaseLabel: TextView
    private lateinit var tvTipLabel: TextView
    private lateinit var tvTotalLabel: TextView
    private lateinit var tvSplitPeople: TextView
    private lateinit var etSplitPeopleNumber: EditText
    private lateinit var tvSplitTotal: TextView
    private lateinit var tvSplitTotalNumber: TextView
    private lateinit var tvSplitWarning: TextView

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
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        tvBaseLabel = findViewById(R.id.tvBaseLabel);
        tvTipLabel = findViewById(R.id.tvTipLabel);
        tvTotalLabel = findViewById(R.id.tvTotalLabel);
        tvSplitPeople = findViewById(R.id.tvSplitPeople);
        etSplitPeopleNumber = findViewById(R.id.etSplitPeopleNumber);
        tvSplitTotal = findViewById(R.id.tvSplitTotal);
        tvSplitTotalNumber = findViewById(R.id.tvSplitTotalNumber);
        tvSplitWarning = findViewById(R.id.tvSplitWarning);

        sbTip.progress = INITIAL_TIP_PERCENT;
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%";
        updateTipDescription(INITIAL_TIP_PERCENT, 0);
        etSplitPeopleNumber.setText("1");
        etSplitPeopleNumber.hint = "";
        tvSplitWarning.visibility = View.INVISIBLE

        val languages = arrayOf("English","Deutsch","Magyar","Pirate");
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        spinnerLanguage.adapter = arrayAdapter;
        spinnerLanguage.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(p2){
                    0 -> {
                        updateTipDescription(sbTip.progress, p2);
                        etBaseAmount.hint = "Bill amount";
                        tvBaseLabel.text =  "Bill";
                        tvTipLabel.text = "Tip";
                        tvTotalLabel.text = "Total";
                        tvSplitPeople.text = "Split by this many people"
                        tvSplitTotal. text = "Split total"
                        tvSplitWarning.text = "Too few people!"
                    }
                    1 -> {
                        updateTipDescription(sbTip.progress, p2);
                        etBaseAmount.hint = "Rechnungsbetrag";
                        tvBaseLabel.text =  "Rechnung";
                        tvTipLabel.text = "Trinkgeld";
                        tvTotalLabel.text = "Gesamtpreis";
                        tvSplitPeople.text = "Rechnung geteilt durch:"
                        tvSplitTotal. text = "Gesamtpreis aufteilen"
                        tvSplitWarning.text = "Zu wenig Leute!"
                    }
                    2 -> {
                        updateTipDescription(sbTip.progress, p2);
                        etBaseAmount.hint = "Számla ára";
                        tvBaseLabel.text =  "Számla";
                        tvTipLabel.text = "Borravaló";
                        tvTotalLabel.text = "Összesen";
                        tvSplitPeople.text = "Ennyi személy fizet külön"
                        tvSplitTotal. text = "Összesen fejenként"
                        tvSplitWarning.text = "Túl kevés személy!"
                    }
                    3 -> {
                        updateTipDescription(sbTip.progress, p2);
                        etBaseAmount.hint = "How much ye owe";
                        tvBaseLabel.text =  "Ye owe";
                        tvTipLabel.text = "Booty coin";
                        tvTotalLabel.text = "Yer givin'";
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        sbTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                tvTipPercentLabel.text = "$p1%";
                computeTipAndTotal();
                val spinner_pos: Int = spinnerLanguage.getSelectedItemPosition()
                updateTipDescription(p1, spinner_pos);
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

        etSplitPeopleNumber.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0");
                computeSplitTotal();
            }
        })

    }

    private fun computeSplitTotal() {
        tvSplitWarning.visibility = View.INVISIBLE
        if(etBaseAmount.text.isEmpty()||etSplitPeopleNumber.text.isEmpty()){
            tvSplitTotalNumber.text = "";
            return;
        }
        if(Integer.valueOf(etSplitPeopleNumber.text.toString()) == 0){
            tvSplitTotalNumber.text = "";
            tvSplitWarning.visibility = View.VISIBLE
            return;
        }
        val totalSplitAmount = tvTotalAmount.text.toString().toDouble();
        val splitPeople = etSplitPeopleNumber.text.toString().toDouble();
        val splitTotal = totalSplitAmount/splitPeople;
        tvSplitTotalNumber.text = "%.2f".format(splitTotal);
    }

    private fun updateTipDescription(tipPercent: Int, spinner_pos: Int) {

        var tipDescription = when(tipPercent){
            in 0..9 ->{
                when(spinner_pos){
                    0 -> "Poor";
                    1 -> "Schlecht";
                    2 -> "Rossz";
                    3 -> "Bloody robbery";
                    else -> "Poor";
                }
            }
            in 10..14 -> {
                when(spinner_pos){
                    0 -> "Acceptable";
                    1 -> "Akzeptabel";
                    2 -> "Elfogadható";
                    3 -> "Had worse";
                    else -> "Acceptable";
                }
            }
            in 15..20 -> {
                when(spinner_pos){
                    0 -> "Good";
                    1 -> "Gut";
                    2 -> "Jó";
                    3 -> "Yer alright mate";
                    else -> "Good";
                }
            };
            in 20..24 -> {
                when(spinner_pos){
                    0 -> "Great";
                    1 -> "Exzellent";
                    2 -> "Kiváló";
                    3 -> "Bloody great";
                    else -> "Great";
                }
            };
            else -> {
                when(spinner_pos){
                    0 -> "Amazing";
                    1 -> "Wunderbar";
                    2 -> "Elképesztő";
                    3 -> "Do me eyes deceive me!?";
                    else -> "Amazing";
                }
            };
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
        computeSplitTotal();
    }
}