package app.android.gweialert

object ModelCheap {
    data class Result(val result: GasPrize)
    data class GasPrize(val SafeGasPrice: String)
}

/*
{
"status":"1",
"message":"OK",
"result":{
"LastBlock":"11856625",
"SafeGasPrice":"117",
"ProposeGasPrice":"137",
"FastGasPrice":"147"}}
 */