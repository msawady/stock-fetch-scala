package domain

case class Chart(bars: Seq[Bar]) {
  def latest: Option[Bar] = bars.headOption

  def beforeLatest: Option[Bar] = bars.lift.apply(1)
}
