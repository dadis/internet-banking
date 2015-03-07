package sk.dadizzz.accounting.web.services

/**
 * Created by DAVID on 8.1.2015.
 */
object PaymentType {

  val CardPayment = "Platba kartou"
  val ATM = "Výber kartou"
  val  others = List("Inkaso (platiteľ)","Platobný príkaz na úhradu / FIT 2.0 (EB)",
  "Platobný príkaz na úhradu (EB Sporopay)",
  "Trvalý Platobný príkaz na úhradu",
  "Daň z úroku")


  def isCardPayment(paymentType:String):Boolean = { paymentType==CardPayment }
  def isATMPayment(paymentType:String):Boolean = { paymentType==ATM }
  def isOtherPayment(paymentType:String):Boolean = { others.contains(paymentType) }





}
