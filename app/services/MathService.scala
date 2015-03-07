package sk.dadizzz.accounting.web.services

/**
 * Created by DAVID on 13.1.2015.
 */
object MathService {

  def roundDoubleTo2Decimal(number: Double):Double = {BigDecimal(number).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble}


}
