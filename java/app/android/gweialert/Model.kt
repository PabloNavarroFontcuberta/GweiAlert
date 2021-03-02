package app.android.gweialert

object Model {
    data class Result(val result: GasPrize)
    data class GasPrize(val ProposeGasPrice: String)
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