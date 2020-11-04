package an.maguste.android.rxsample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val observable = Observable.just(1, 2, 3)
        val disposeObservable = observable.subscribe {
            logAndAppend("Observable new data $it")
        }

        val single = Single.just(1)
        val disposeSingle = single.subscribe({
            logAndAppend("Single new data $it")
        }, {})

        val flowable = Flowable.just(1, 2, 3, 4, 5)
        val disposeFlowable = flowable.subscribe {
            logAndAppend("Flowable new data $it")
        }


        textLog.setOnClickListener{
            logAndAppend("Button clicked")
            Toast.makeText(this.baseContext,"Button clicked", Toast.LENGTH_SHORT )
        }
        textLog.performClick()

        val dispose = dataSource()
                .subscribe { logAndAppend("next int = $it") }

    }

    fun dataSource(): Observable<Int> {
        return Observable.create { subscriber ->
            for (i in 0..100 step 5){
                Thread.sleep(100)
                subscriber.onNext(i)
            }
        }
    }

    private fun logAndAppend(text: String){
        textLog.append("$text \n")
    }
}