## About

Scala utility scripts for getting US-stock prices via [Alpaca API](https://alpaca.markets/) 

## Get Started

1. Sign up to [Alpaca Market API](https://app.alpaca.markets/signup).
1. Clone this repository.
1. Copy `src/main/resources/_template_settings.json` to `src/main/resources/settings.json`.
1. Copy and Paste your `Key ID` and `Secret Key`.
1. Edit `src/main/scala/launch/Main.scala` and run the code.

``` scala
  // input your target ticker codes.
  cli.fetchDailyChart(Seq("AAPL"))
```