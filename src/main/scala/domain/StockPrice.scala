package domain

case class StockPrice(value: BigDecimal) {
  require(value > 0, "Price must be Positive")

  def -(other: StockPrice): BigDecimal = value - other.value
  def %(other: StockPrice): BigDecimal = (this - other) / other.value
}
