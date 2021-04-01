package com.example.stock_news

data class StockNewsItem(var title: String?,
                         var news_url: String?,
                        var image_url: String?,
                         var text: String?,
                        var sentiment: String?,
                        var source_name: String?,
                        var date: String?

)

/**
"data": [
{
"title": "Tesla sued in wrongful death lawsuit that alleges Autopilot caused crash",
"news_url": "http://feedproxy.google.com/~r/Techcrunch/~3/npc7dszj01U/",
"image_url": "https://stocknewsapi.com/images/v1/d/w/te7.jpg",
"text": "The family of Walter Huang, an Apple engineer who died after his Tesla Model X with Autopilot engaged crashed into a highway median, is suing Tesla.",
"sentiment": "Negative",
"type": "Article",
"source_name": "TechCrunch",
"date": "Wed, 01 May 2019 15:18:31 -0400",
"tickers": [
"TSLA"
],
"tags": []
}
 */