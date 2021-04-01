package com.example.stock_searching

import com.google.gson.annotations.SerializedName


data class StockSearchItem(@SerializedName("1. symbol") var symbol: String,
                           @SerializedName("2. name") var name: String,
                           @SerializedName("3. type") var type: String,
                           @SerializedName("4. region") var region: String,
                           @SerializedName("5. marketOpen") var marketOpen: String,
                           @SerializedName("6. marketClose") var marketClose: String,
                           @SerializedName("7. timezone") var timezone: String,
                           @SerializedName("8. currency") var currency: String,
                           @SerializedName("9. matchScore") var matchScore: Float

)








/**
"bestMatches": [
{
"1. symbol": "Y45.SGP",
"2. name": "Singapore Myanmar Investco Limited",
"3. type": "Equity",
"4. region": "Singapore",
"5. marketOpen": "09:00",
"6. marketClose": "17:00",
"7. timezone": "UTC+08",
"8. currency": "SGD",
"9. matchScore": "0.5000"
}
]
 */