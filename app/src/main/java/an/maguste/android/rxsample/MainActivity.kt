package an.maguste.android.rxsample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers
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

        /*val single = Single.just(1)
        val disposeSingle = single.subscribe({
            logAndAppend("Single new data $it")
        }, {})*/

        val flowable = Flowable.just(1, 2, 3, 4, 5)
        val disposeFlowable = flowable.subscribe {
            logAndAppend("Flowable new data $it")
        }


        textButton.setOnClickListener{
            logAndAppend("Button clicked")
            Toast.makeText(this,"Button clicked", Toast.LENGTH_SHORT )
        }

        val dispose = dataSourceFlowable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logAndAppend("next int = $it")
                }, {
                    Log.e(TAG, " error - ${it.localizedMessage}")
                    logAndAppend("dataSource error")
                }, {

                })

        val disposeSingle = dataSourceSingle()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logAndAppend("Singe ${it.joinToString { "$it#" }}")
                },{

                })

        val disposeCompletable = dataSourceCompletable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logAndAppend("Completable success")
                },{
                    logAndAppend("Completable failure")
                })

        val disposeMaybe = dataSourceMaybe()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    logAndAppend("Maybe success $it")
                },{
                    logAndAppend("Maybe failure")
                },{
                    logAndAppend("Maybe complete")
                })
    }

    fun dataSourceObservable(): Observable<Int> {
        return Observable.create { subscriber ->
            for (i in 0..100 step 5){
                Thread.sleep(10000)
                subscriber.onNext(i)
            }
        }
    }

    // Flowable almost the same as Observable
    fun dataSourceFlowable(): Flowable<Int> {
        return Flowable.create ({ subscriber ->
            for (i in 0..50 step 10){
                subscriber.onNext(i)
            }
            subscriber.onComplete()
        }, BackpressureStrategy.BUFFER) //, DROP, LATEST
    }

    fun dataSourceSingle(): Single<List<Int>> {
        return Single.create { subscriber ->
            val list = listOf(0,1,2,3,4,5,6,7,8,9,10)
            subscriber.onSuccess(list)
        }
    }

    fun dataSourceCompletable(): Completable {
        return Completable.create { subscriber ->
            subscriber.onComplete()
        }
    }

    fun dataSourceMaybe(): Maybe<Int> {
        return Maybe.create  { subscriber ->
            val list = listOf(0,1,2,3,4,5,6,7,8,9,10)
            subscriber.onSuccess(list.filter{ it % 2 != 0 }.sum())
            subscriber.onComplete()
        }
    }


    private fun logAndAppend(text: String){
        textLog.append("$text \n")
    }
}