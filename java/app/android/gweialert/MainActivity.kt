package app.android.gweialert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import app.android.gweialert.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity(){



    private var disposable: Disposable? = null


    object constantes{
        const val gp = 0.038295454
        const val gp2 = 3.0939
    }
    private val gasApiServe by lazy {
        GasApiService.create()
    }

    private val gasApiServeCheap by lazy {
        GasApiServiceCheap.create()
    }

    private val gasApiServeExpensive by lazy {
        GasApiServiceExpensive.create()
    }

    var textInputInt = 0

    private var notificationManager: NotificationManager? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTheme(R.style.GweiAlert)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ////////////       Animations //////////////
        var notificationFlag = false
        val ttb = AnimationUtils.loadAnimation(this,
            R.anim.ttb
        )
        val textView2 = findViewById<TextView>(R.id.textView2)
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.startAnimation(ttb)
        textView2.startAnimation(ttb)
        val stb = AnimationUtils.loadAnimation(this,
            R.anim.stb
        )
        val text_gas = findViewById<TextView>(R.id.text_gas)
        val text_gas_cheap = findViewById<TextView>(R.id.text_gas_cheap)
        val text_gas_expensive = findViewById<TextView>(R.id.text_gas_expensive)
        text_gas.startAnimation(stb)
        text_gas_cheap.startAnimation(stb)
        text_gas_expensive.startAnimation(stb)

        //////////////////////////////////////////////

        notificationManager =
                getSystemService(
                        Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
                "gasAlert",
                "GasAlert",
                "Gwei Ethereum Fee")

        val notificationID = 101

        val channelID = "gasAlert"

        val textInput = findViewById<EditText>(R.id.gweiPriceText)
        val textPrice = findViewById<TextView>(R.id.estimatedGweiPrice)
        val sw1 = findViewById<Switch>(R.id.switch1)

        textInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.toString() == "") {

                } else {
                    val calculedPrice = (s.toString().toInt() * constantes.gp) * constantes.gp2
                    textPrice.setText("Estimated price ERC20 Transfer: " + String.format("%.3f",calculedPrice) + " $")
                }
            }
        })

        sw1?.setOnCheckedChangeListener { _, isChecked ->
            notificationFlag = isChecked
            textInput.isEnabled = !isChecked
        }


        //    notificationManager?.notify(notificationID, notification)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                //Call your function here
                beginSearchGas()
                val notificationTextGas = text_gas.text.split(' ')[0]
                val notificationTextGasCheap = text_gas_cheap.text.split(' ')[0]
                val notificationTextGasExpensive = text_gas_expensive.text.split(' ')[0]
                val notificationTextFinal = "Cheap:" + notificationTextGasCheap + "  | Avg:" + notificationTextGas + "  | Fast:" + notificationTextGasExpensive
                val bmp: Bitmap = BitmapFactory.decodeResource(resources,
                    R.drawable.ethblack
                )

                val notification = Notification.Builder(this@MainActivity,
                        channelID)
                        .setContentTitle("Gwei Update")
                        .setContentText(notificationTextFinal)
                        .setSmallIcon(R.drawable.ethblack)
                        .setOngoing(true)
                        .setLargeIcon(bmp)
                        .setChannelId(channelID)
                        .build()

                if(notificationFlag &&  textInput.text.toString().toInt() > notificationTextGas.substring(1).toInt()) {
                    notificationManager?.notify(notificationID, notification)
                }
                else
                {
                    notificationManager?.cancelAll()
                }
                handler.postDelayed(this, 10000)//1 sec delay
            }
        }, 0)

    }// onCreate




    private fun beginSearchGas() {
        val text_gas = findViewById<TextView>(R.id.text_gas)
        val text_gas_cheap = findViewById<TextView>(R.id.text_gas_cheap)
        val text_gas_expensive = findViewById<TextView>(R.id.text_gas_expensive)




        disposable = gasApiServe.hitCountCheck("gastracker", "gasoracle", "7XEPPZV2HJ3YQV8I688NV8A8C323XRFDSY")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> text_gas.text = "\n${result.result.ProposeGasPrice} Gwei \nEstimated price:\n" + String.format("%.3f", result.result.ProposeGasPrice.toInt() * constantes.gp) + "$" },
                        { error -> println(error.message) }
                )
        disposable = gasApiServeCheap.hitCountCheck("gastracker", "gasoracle", "7XEPPZV2HJ3YQV8I688NV8A8C323XRFDSY")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> text_gas_cheap.text = "\n${result.result.SafeGasPrice} Gwei \nEstimated price:\n" + String.format("%.3f",result.result.SafeGasPrice.toInt() * constantes.gp) + "$" },
                        { error -> println(error.message) }
                )
        disposable = gasApiServeExpensive.hitCountCheck("gastracker", "gasoracle", "7XEPPZV2HJ3YQV8I688NV8A8C323XRFDSY")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> text_gas_expensive.text = "\n${result.result.FastGasPrice} Gwei \nEstimated price:\n" + String.format("%.3f",result.result.FastGasPrice.toInt() * constantes.gp) + "$" },
                        { error -> println(error.message) }
                )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String,
                                          description: String) {

        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)
    }

    

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
