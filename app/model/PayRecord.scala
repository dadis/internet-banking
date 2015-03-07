package sk.dadizzz.accounting.web.models

/**
 * Created by DAVID on 16.1.2015.
 */

import java.sql.Connection

import org.joda.time.DateTime
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class PayRecord(paymentAmount:Double,balance:Double,paymentType:String,paymentGroup:String,month:Int,year:Int)



object PayRecord{

  val mappingPayRecord = {
    get[Double]("paymentAmount") ~
      get[Double]("balance") ~
      get[String]("paymentType") ~
      get[String]("paymentGroup") ~
      get[Int]("p_month") ~
      get[Int]("p_year") map {
      case paymentAmount ~ balance ~ paymentType~group~month~year =>
        PayRecord(paymentAmount,balance,paymentType,group,month,year)
    }
  }



//
//  def create(r:PayRecord)= { implicit c: Connection =>        //TODO add manually record
//    SQL("insert into PayRecord(paymentAmount,balance,paymentType)values({paymentAmount},{balance},{paymentType})").on(
//      "paymentAmount" -> r.paymentAmount,
//      "balance" -> r.balance,
//      "paymentType" -> r.paymentType
//    ).executeInsert()
//  }





}




