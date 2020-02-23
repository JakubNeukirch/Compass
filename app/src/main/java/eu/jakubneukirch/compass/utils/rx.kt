package eu.jakubneukirch.compass.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

//todo remove when rxKotlin updated to support rxJava version 3.0
operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

fun <T> Observable<T>.subscribeBy(
    onNext: (T) -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {}
): Disposable {
    return subscribe(onNext, onError, onComplete)
}

fun <T> Single<T>.subscribeBy(
    onSuccess: (T) -> Unit = {},
    onError: (Throwable) -> Unit = {}
): Disposable {
    return subscribe(onSuccess, onError)
}