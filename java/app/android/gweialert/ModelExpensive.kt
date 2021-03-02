package app.android.gweialert

object ModelExpensive {
    data class Result(val result: GasPrize)
    data class GasPrize(val FastGasPrice: String)
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