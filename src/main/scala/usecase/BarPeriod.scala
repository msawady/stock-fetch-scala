package usecase

sealed trait BarPeriod

case object OneMin extends BarPeriod
case object FiveMin extends BarPeriod
case object FifteenMin extends BarPeriod
case object OneDay extends BarPeriod
