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


  def toDbData = data.map(p => Seq[ParameterValue](p.paymentAmount, p.balance, p.paymentType, p.paymentGroup, p.month, p.year))

  val name = Month.getMonthName(data(0).month)
  val month = data(0).month
  val year = data(0).year

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



}

object Month {
  def findAllForMonth(month: Int, year: Int): Month = {
    DB.withConnection {
      implicit c =>
        val data = SQL("Select * from PayRecord WHERE p_month={month} AND p_year={year}").on("month" -> month, "year" -> year).as(PayRecord.mappingPayRecord *)
        new Month(data)
    }
  }

  def findAll: List[Month] = {

    DB.withConnection {
      implicit c =>
       val all = SQL("Select * from PayRecord ORDER by p_month,p_year").as(PayRecord.mappingPayRecord *)

       all.groupBy(r => r.month+"-"+r.year).map(m => Month(m._2)).toList.sortBy(m=>(m.year,m.month))
         //.sortWith(_.month < _.month)


    }
  }





  def getMonthName(i: Int): String = {

    i match {
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

  def saveMonth(m: Month) = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into PayRecord(paymentAmount,balance,paymentType,paymentGroup,p_month,p_year)" +
          "values({paymentAmount},{balance},{paymentType},{paymentGroup},{p_month},{p_year})")
          .asBatch
          .addBatchParamsList(m.toDbData).execute()
    }
  }

  type monthYear = (Int, Int)

  val monthYear = {
    get[Int]("p_month") ~
      get[Int]("p_year") map {
      case month ~ year =>
        (month, year)
    }
  }

  def getListOfMonths(): List[monthYear] = {
    DB.withConnection {
      implicit c =>
        SQL("SELECT DISTINCT  p_month, p_year FROM payrecord").as(monthYear *)
    }
  }

  def findLast: monthYear = {

    DB.withConnection {
      implicit c =>
        val res = SQL("Select p_month,p_year from PayRecord ORDER by p_month,p_year Limit 1").as(monthYear *)

    res.head

    }

  }
}


