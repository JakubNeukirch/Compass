package eu.jakubneukirch.compass.base

interface UseCase<PARAM, RESULT> {
    fun run(param: PARAM): RESULT
    operator fun invoke(param: PARAM): RESULT = run(param)
}