package com.e.androidtest.ui.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel: ViewModel() {

    private val compositeDisposable=CompositeDisposable()

    protected  fun Disposable.addDisposable(){
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        super.onCleared()

     compositeDisposable.clear()
    }
}