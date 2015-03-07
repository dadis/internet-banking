package sk.dadizzz.accounting.web.models

import java.io.File

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current
import sk.dadizzz.accounting.web.services.{PaymentType, IBParserService, MathService}


/**
 * Created by DAVID on 10.1.2015.
 */
case class Month(data: List[PayRecord]) {


  def toDbData = data.map(p => Seq[ParameterValue](p.paymentAmount, p.balance, p.paymentType, p.paymentGroup, p.paymentDate))


  val month = data(0).paymentDate.getMonthOfYear
  val year = data(0).paymentDate.getYear
  val name = getMonthName

  val previousMonth = {

    if(month==1){(12,year-1)}
    else{(month-1,year)}
  }
  val nextMonth =
  {
    if(month==12){(1,year+1)}
    else{(month+1,year)}
  }

  //TODO do properly


  val atmGroup = groupForType(PaymentType.isATMPayment(_))
  val cardGroup = groupForType(PaymentType.isCardPayment(_))
  val othersGroup = groupForType(PaymentType.isOtherPayment(_))

  val atmTotal = total(atmGroup)
  val cardTotal = total(cardGroup)
  val othersTotal = total(othersGroup)

  val totalSaved = MathService.roundDoubleTo2Decimal(totalCredit + totalDebet)

  def groupForType(isInGroup: String => Boolean): List[PayRecord] = {

    data.filter(p => p.paymentAmount < 0 && isInGroup(p.paymentType))
  }

  def total(group: List[PayRecord]): Double = {
    MathService.roundDoubleTo2Decimal(group.map(_.paymentAmount).sum)
  }

  def totalDebet: Double = {

    val number = data.filter(r => r.paymentAmount < 0).map(_.paymentAmount).sum

    MathService.roundDoubleTo2Decimal(number)

  }

  def totalCredit: Double = {

    val number = data.filter(r => r.paymentAmount > 0).map(_.paymentAmount).sum

    MathService.roundDoubleTo2Decimal(number)
  }

  def getMonthName: String = {

    month match {
      case 1 => "January"
      case 2 => "February"
      case 3 => "March"
      case 4 => "April"
      case 5 => "May"
      case 6 => "June"
      case 7 => "July"
      case 8 => "August"
      case 9 => "September"
      case 10 => "October"
      case 11 => "November"
      case 12 => "December"
    }
  }

}

object Month {
  def findAllForMonth(month: Double, year: Double): Month = {
    DB.withConnection {
      implicit c =>
        val data = SQL("select * from logrecord where" +
          "extract(MONTH from paymentDate)={month}" +
          "extract(YEAR from paymentDate)={year}").on("month" -> month, "year" -> year).as(PayRecord.mappingPayRecord *)
        new Month(data)
    }
  }

  def findAll: List[Month] = {

    DB.withConnection {
      implicit c =>
       val all = SQL("Select * from PayRecord ORDER by paymentDate").as(PayRecord.mappingPayRecord *)

       all.groupBy(r => r.paymentDate).map(m => Month(m._2)).toList.sortBy(m=>(m.year,m.month))
         //.sortWith(_.month < _.month)


    }
  }







  def saveMonth(m: Month) = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into PayRecord(paymentAmount,balance,paymentType,paymentGroup,paymentDate)" +
          "select {paymentAmount},{balance},{paymentType},{paymentGroup},{paymentDate}" +
          "where not exists (" +
          "select * from PayRecord " +
          "where paymentAmount = cast({paymentAmount} as text) and balance = cast({balance} as text) and paymentType={paymentType} and paymentGroup={paymentGroup} and paymentDate={paymentDate}" +
          ")")
          .asBatch
          .addBatchParamsList(m.toDbData).execute()
    }
  }

  type monthYear = (Double, Double)

  val monthYear = {
    get[Double]("p_month") ~
      get[Double]("p_year") map {
      case month ~ year =>
        (month, year)
    }
  }

  def getListOfMonths(): List[monthYear] = {
    DB.withConnection {
      implicit c =>
        SQL("SELECT DISTINCT  extract(MONTH from paymentDate) as p_month, extract(YEAR from paymentDate) as p_year FROM payrecord").as(monthYear *)
    }
  }

  def findLast: monthYear = {

    DB.withConnection {
      implicit c =>
        val res = SQL("Select extract(MONTH from paymentDate) as p_month, extract(YEAR from paymentDate) as p_year from PayRecord ORDER by p_month,p_year Limit 1").as(monthYear *)

    res.head

    }

  }
}


